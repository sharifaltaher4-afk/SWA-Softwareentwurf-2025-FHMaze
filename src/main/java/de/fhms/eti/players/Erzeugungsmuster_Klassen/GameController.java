package main.java.de.fhms.eti.players.Erzeugungsmuster_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.*;
import main.java.de.fhms.eti.players.Kommunikations_Klassen.GameCommunication;
import main.java.de.fhms.eti.players.Kommunikations_Klassen.TurnData;
import main.java.de.fhms.eti.players.Strategie_Klassen.FormCollectionStrategy;
import main.java.de.fhms.eti.players.Strategie_Klassen.INavigationStrategy;

/**
 * GameController Zentrale Spielsteuerung
 * <p>
 * Implementiert Singleton Pattern (Erzeugungsmuster)
 * <p>
 * Verantwortung (SRP):
 * - Koordination des Spielablaufs
 * - Verwaltung von Komponenten
 * - Strategie-Wechsel
 * <p>
 * Erfuellt SOLID:
 * - SRP: Nur Spielablauf-Koordination
 * - OCP: Erweiterbar durch neue Strategien
 * - DIP: AbhÃ¤ngig von Interfaces
 */
public class GameController {
    private static GameController instance;

    private BotConfig config;
    private Navigator navigator;
    private GameCommunication comm;
    private boolean useSimpleStrategyOnly;
    private int simpleStrategyForceRounds;

    /**
     * Private Konstruktor fuer Singleton
     */
    private GameController() {
        this.useSimpleStrategyOnly = false;
        this.simpleStrategyForceRounds = 0;
    }

    /**
     * Singleton-Instanz abrufen
     */
    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    /**
     * Reset für neues Spiel
     */
    public static void reset() {
        instance = null;
    }

    /**
     * Initialisiert den Controller mit Konfiguration
     *
     * @param config Bot-Konfiguration
     * @param comm   GameCommunication
     */
    public void initialize(BotConfig config, GameCommunication comm) {
        this.config = config;
        this.comm = comm;

        // Erstelle initiale Strategie
        INavigationStrategy initialStrategy = config.strategyFactory()
                .createStrategy(config.levelDetector().getCurrentLevel(),
                        config.maze(),
                        config.formManager());

        initialStrategy.setPlayerId(config.playerId());

        // Erstelle Navigator
        this.navigator = new Navigator(config.maze(), config.startPosition(), initialStrategy);
        config.logger().log("GameController initialisiert");
    }

    /**
     * Verarbeitet eine einzelne Spielrunde
     * LEVEL 3: CellObjekte enthalten jetzt direkt Gegner-Info
     *
     * @param turnData  Rundendaten
     * @param turnCount Rundennummer
     * @return true wenn Spiel weitergehen soll, false bei Beendigung
     */
    public boolean processTurn(TurnData turnData, int turnCount) {
        // Logging Gegner-Status (aus Cell-Objekten!)
        int opponentCount = countOpponents(turnData);
        if (opponentCount > 0) {
            config.logger().log("[LEVEL3]  " + opponentCount + " Gegner sichtbar");
        }

        boolean opponentOnCurrentField = turnData.currentCell.hasOpponent()
                && turnData.currentCell.getOpponentDistance() == 0;

        if (opponentOnCurrentField) {
            config.logger().log("[LEVEL3] Gespräch erkannt! Gegner auf gleichem Feld (Distance 0)");

            // 50% Chance zu warten, 50% Chance sich zu bewegen
            if (Math.random() < 0.5) {
                config.logger().log("[LEVEL3] WARTE: Sende POSITION-Aktion");
                comm.sendAction(new PositionAction());
            } else {
                // Versuche eine zufällige Richtung
                Direction[] directions = {Direction.north, Direction.east, Direction.south, Direction.west};
                Cell[] cells = {turnData.northCell, turnData.eastCell, turnData.southCell, turnData.westCell};

                Direction chosenDirection = Direction.north; // Fallback
                for (int i = 0; i < 4; i++) {
                    if (!(cells[i] instanceof WallCell)) {
                        chosenDirection = directions[i];
                        break;
                    }
                }

                config.logger().log("[LEVEL3] ZUFALL: Versuche Bewegung " + chosenDirection);
                comm.sendAction(new GoAction(chosenDirection));
            }
            return true;
        }

        // Logging
        if (turnCount <= 100) {
            config.logger().log("ZELLSTATUS:");
            config.logger().log("Aktuell: " + turnData.currentCell +
                    " | hasOpponent=" + turnData.currentCell.hasOpponent() +
                    " | Distance=" + turnData.currentCell.getOpponentDistance());
            config.logger().log(
                    "Nord: " + turnData.northCell + " | hasOpp=" + turnData.northCell.hasOpponent() + " | Dist="
                            + turnData.northCell.getOpponentDistance() +
                            " | Ost: " + turnData.eastCell + " | hasOpp=" + turnData.eastCell.hasOpponent() + " | Dist="
                            + turnData.eastCell.getOpponentDistance() +
                            " | Sued: " + turnData.southCell + " | hasOpp=" + turnData.southCell.hasOpponent()
                            + " | Dist=" + turnData.southCell.getOpponentDistance() +
                            " | West: " + turnData.westCell + " | hasOpp=" + turnData.westCell.hasOpponent()
                            + " | Dist=" + turnData.westCell.getOpponentDistance());
        }

        // Level-Erkennung
        int oldLevel = config.levelDetector().getCurrentLevel();
        config.levelDetector().detectLevel(turnData.currentCell);
        int newLevel = config.levelDetector().getCurrentLevel();

        if (oldLevel != newLevel) {
            config.logger().logLevelDetection(turnData.currentCell, newLevel);
            handleLevelChange(newLevel);
        }

        // Formular-Verarbeitung
        if (processFormActions(turnData)) {
            return true; // Formular genommen, Runde beenden
        }

        // FINISH-Zelle Verarbeitung
        if (turnData.currentCell instanceof FinishCell finishCell) {
            processFinishCell(finishCell);
        }
        // Verarbeite auch Nachbar-FinishCells!
        if (turnData.northCell instanceof FinishCell northFinish) {
            processFinishCell(northFinish);
        }
        if (turnData.eastCell instanceof FinishCell eastFinish) {
            processFinishCell(eastFinish);
        }
        if (turnData.southCell instanceof FinishCell southFinish) {
            processFinishCell(southFinish);
        }
        if (turnData.westCell instanceof FinishCell westFinish) {
            processFinishCell(westFinish);
        }

        // Pruefe ob finish moeglich
        if (shouldFinish(turnData.currentCell)) {
            comm.sendAction(new FinishAction());
            config.logger().log("[SUCCESS] FINISH gesendet - Mission erfuellt!");
            return false; // Spiel beenden
        }

        // Strategie-Management
        manageStrategy();

        // Bewegungsentscheidung
        GoAction action = navigator.decideNextMove(
                turnData.northCell, turnData.eastCell,
                turnData.southCell, turnData.westCell);

        if (action == null) {
            config.logger().log("  Keine optimale Richtung! Fallback.");
            action = getFallbackAction(turnData);
        }

        // LEVEL 3: Kollisionsvermeidung
        action = avoidOpponentCollision(action, turnData);

        // Aktion ausfuehren
        comm.sendAction(action);
        Position oldPos = navigator.getCurrentPosition();
        navigator.updatePosition(action);
        Position newPos = navigator.getCurrentPosition();
        config.logger().logMovement(oldPos, newPos, action, "Navigation");

        // Status-Logging
        if (turnCount < 100 || turnCount % 10 == 0) {
            logStatus(turnCount);
        }

        return true; // Spiel weitergehen
    }

    /**
     * Behandelt Level-Wechsel
     */
    private void handleLevelChange(int newLevel) {
        config.logger().log("[STAT] Level-Wechsel zu Level " + newLevel);

        // Erstelle neue Strategie fuer neues Level
        INavigationStrategy newStrategy = config.strategyFactory()
                .createStrategy(newLevel, config.maze(), config.formManager());

        newStrategy.setPlayerId(config.playerId());
        navigator.setStrategy(newStrategy);
    }

    /**
     * Verarbeitet Formular-Aktionen
     */
    private boolean processFormActions(TurnData turnData) {
        if (turnData.currentCell instanceof FormCell formCell) {
            boolean canTake = config.formManager().canTakeForm(formCell);

            if (canTake) {
                config.logger().logFormInfo(turnData.currentCell, true, "Kann genommen werden");
                boolean success = config.formManager().takeForm(formCell);
                if (success) {
                    comm.sendAction(new TakeAction());
                    config.logger().log("[INFO] Formular genommen: take");
                    config.logger().logCollectedForms(
                            config.formManager().getCollectedForms(),
                            config.formManager().getNextFormId());
                    return true; //  Formular genommen, kein Bewegung mehr
                }
            } else {
                logFormRejectionReason(formCell);
            }
        }
        return false; // Kein Formular genommen, Bewegung erlauben
    }

    /**
     * Loggt warum Formular nicht genommen werden kann
     */
    private void logFormRejectionReason(FormCell cellStatus) {
        int formPlayerId = cellStatus.getPlayerId();
        int formId = cellStatus.getFormId();
        int expectedId = config.formManager().getNextFormId();

        if (formPlayerId != config.playerId()) {
            config.logger().logFormInfo(cellStatus, false, "Falscher Spieler");
        } else if (formId != expectedId) {
            config.logger().logFormInfo(cellStatus, false,
                    "Falsche Reihenfolge: Erwartet " + expectedId + ", Gefunden " + formId);
        }
    }

    /**
     * Verarbeitet FINISH-Zelle
     */
    private void processFinishCell(FinishCell finishCell) {
        int finishPlayerId = finishCell.getPlayerId();

        // Level 2/3: Form-Count aus FINISH-Zelle holen (gilt für ALLE Spieler)
        if (config.levelDetector().isLevel2() || config.levelDetector().isLevel3()) {
            int formCount = finishCell.getFormCount();

            // Setze Form-Count auch wenn es fremdes FINISH ist
            // In Level 2/3 haben alle Spieler die gleiche Anzahl Formulare
            if (config.formManager().getTotalFormsNeeded() == -1) {
                config.formManager().setTotalFormsNeeded(formCount);
                config.logger().log("[INFO] Form-Count aus FINISH erkannt: " + formCount);
            }

            // Zusaetzliche Infos wenn es UNSER FINISH ist
            if (finishPlayerId == config.playerId()) {
                config.logger().log("[INFO] Eigenes Ziel gefunden!");

                if (navigator.getStrategy() instanceof FormCollectionStrategy formStrategy) {
                    formStrategy.setTargetPosition(navigator.getCurrentPosition());
                    config.logger().log("[POS] Ziel-Position gemerkt: " + navigator.getCurrentPosition());
                }
            } else {
                config.logger().log("[BLOCKED] Fremdes Ziel von Spieler " + finishPlayerId);
            }
        } else if (config.levelDetector().isLevel1()) {
            if (finishPlayerId == config.playerId()) {
                config.logger().log("[INFO] Level 1 Ziel gefunden");
            }
        }
    }

    /**
     * Prueft ob FINISH ausgefuehrt werden kann
     */
    private boolean shouldFinish(Cell currentCell) {
        if (!(currentCell instanceof FinishCell finishCell)) {
            return false;
        }

        try {
            int finishPlayerId = finishCell.getPlayerId();

            if (finishPlayerId != config.playerId()) {
                return false;
            }

            if (config.levelDetector().isLevel1()) {
                return true; // Level 1: Sofort FINISH
            } else if (config.levelDetector().isLevel2()) {
                return config.formManager().hasAllForms(); // Level 2: Alle Formulare
            } else if (config.levelDetector().isLevel3()) {
                return config.formManager().hasAllForms(); // Level 3: Alle Formulare + Gegner vermeiden
            }
        } catch (NumberFormatException e) {
            config.logger().log("  Fehler beim Parsen: " + currentCell);
        }

        return false;
    }

    /**
     * Verwaltet Strategie-Wechsel basierend auf Spielsituation
     */
    private void manageStrategy() {
        // Strategie-Wechsel zu FormCollectionStrategy für Level 2 UND Level 3

        // Countdown für Force-Rounds (SimpleStrategy-Sperre)
        if (simpleStrategyForceRounds > 0) {
            simpleStrategyForceRounds--;
            config.logger().log("[DEBUG] SimpleStrategy-Sperre: noch " + simpleStrategyForceRounds + " Runden"); // 3p.NEU
            if (simpleStrategyForceRounds == 0) {
                useSimpleStrategyOnly = false;
                config.logger().log("  SimpleStrategy-Sperre aufgehoben");
            }
        }

        // WICHTIG: Wenn wir Formulare brauchen, MUSS FormCollection aktiv sein
        boolean needsFormCollection = !config.formManager().isComplete();
        boolean isLevel2or3 = config.levelDetector().isLevel2() || config.levelDetector().isLevel3();

        // Aktiviere FormCollectionStrategy wenn nötig UND nicht blockiert
        if (isLevel2or3 && needsFormCollection && !useSimpleStrategyOnly) {
            if (!(navigator.getStrategy() instanceof FormCollectionStrategy)) {
                int level = config.levelDetector().isLevel3() ? 3 : 2;
                config.logger().log("[CHANGE] Wechsle zu FormCollectionStrategy für Level " + level);

                INavigationStrategy newStrategy = config.strategyFactory()
                        .createStrategyByType(StrategyType.FORM_COLLECTION,
                                config.maze(),
                                config.formManager());
                newStrategy.setPlayerId(config.playerId());
                navigator.setStrategy(newStrategy);
            }
        }
    }

    /**
     * Fallback-Aktion wenn keine optimale Richtung gefunden
     */
    private GoAction getFallbackAction(TurnData turnData) {
        // TODO: Verbesserung: switch-case
        if (!(turnData.northCell instanceof WallCell))
            return new GoAction(Direction.north);
        else if (!(turnData.eastCell instanceof WallCell))
            return new GoAction(Direction.east);
        else if (!(turnData.southCell instanceof WallCell))
            return new GoAction(Direction.south);
        else if (!(turnData.westCell instanceof WallCell))
            return new GoAction(Direction.west);
        else
            return new GoAction(Direction.north);
    }

    /**
     * Loggt aktuellen Status
     */
    private void logStatus(int turnCount) {
        config.logger().log("Bot " + config.playerId() +
                " | Level: " + config.levelDetector().getCurrentLevel() +
                " | Formulare: " + config.formManager().getCollectedForms() +
                " | Position: " + navigator.getCurrentPosition() +
                " | Strategie: " + navigator.getStrategy().getClass().getSimpleName() +
                " | Runde: " + turnCount);

        if (turnCount % 50 == 0) {
            Position pos = navigator.getCurrentPosition();
            int visits = config.maze().getVisitCount(pos);
            config.logger().log("[STAT] Statistik Runde " + turnCount + ": Position " + pos +
                    " wurde " + visits + "x besucht");
        }
    }

    /**
     * LEVEL 3: Zählt Gegner aus Cell-Objekten
     */
    private int countOpponents(TurnData turnData) {
        int count = 0;
        if (turnData.currentCell.hasOpponent())
            count++;
        if (turnData.northCell.hasOpponent())
            count++;
        if (turnData.eastCell.hasOpponent())
            count++;
        if (turnData.southCell.hasOpponent())
            count++;
        if (turnData.westCell.hasOpponent())
            count++;
        return count;
    }

    /**
     * LEVEL 3: Vermeidet Kollisionen mit Gegnern (Cell-basiert!)
     * <p>
     * Prüft ob die geplante Aktion zu einem Feld mit Gegner führt.
     * Falls ja, wähle alternative Richtung ohne Gegner.
     * <p>
     * STRATEGIE:
     * - Vermeide Distance ≤1 (Distance 0 = Kollision, Distance 1 = Gespräch droht!)
     * - Suche alternative Richtung ohne nahe Gegner
     * - Falls keine Alternative: Akzeptiere Kollision (besser als Wand)
     *
     * @param plannedAction Ursprüngliche geplante Aktion
     * @param turnData      Aktuelle Rundendaten
     * @return Sichere Aktion (ohne Gegner-Kollision wenn möglich)
     */
    private GoAction avoidOpponentCollision(GoAction plannedAction, TurnData turnData) {
        // Alle Zellen und Richtungen
        Cell[] cells = {
                turnData.northCell,
                turnData.eastCell,
                turnData.southCell,
                turnData.westCell
        };

        Direction[] directions = {
                Direction.north,
                Direction.east,
                Direction.south,
                Direction.west
        };

        // Finde Index der geplanten Richtung
        int plannedIndex = getDirectionIndex(plannedAction.getDirection());
        Cell plannedCell = cells[plannedIndex];

        // KRITISCH: Vermeide Gegner mit Distance ≤1
        // Distance 0 = Kollision (beide Bots auf gleichem Feld)
        // Distance 1 = Gespräch droht nächste Runde!
        boolean hasNearOpponent = plannedCell.hasOpponent() &&
                plannedCell.getOpponentDistance() <= 1;

        if (hasNearOpponent) {
            config.logger().log("[LEVEL3] Gegner in Richtung " + plannedAction.getDirection() +
                    " (Distance: " + plannedCell.getOpponentDistance() + ")");
            config.logger().log("[LEVEL3] KOLLISION/GESPRÄCH droht! Suche Alternative...");

            // Suche alternative Richtung ohne nahe Gegner
            for (int i = 0; i < 4; i++) {
                Cell cell = cells[i];

                // Prüfe: Kein naher Gegner UND keine Wand
                boolean isWall = cell instanceof WallCell;

                if (!isWall) {
                    config.logger().log("[LEVEL3] Alternative gefunden: " + directions[i] +
                            " (keine nahen Gegner)");
                    return new GoAction(directions[i]);
                }
            }

            // Keine sichere Alternative gefunden
            // Strategie: Wähle Richtung mit größter Distance (weniger schlimm)
            config.logger().log("[LEVEL3] Keine gegner-freie Alternative!");

            int bestIndex = -1;
            int maxDistance = -1;

            for (int i = 0; i < 4; i++) {
                // Keine Wände
                if (cells[i] instanceof WallCell)
                    continue;

                int distance = cells[i].hasOpponent() ? cells[i].getOpponentDistance() : 999; // Kein Gegner = sehr hoch

                if (distance > maxDistance) {
                    maxDistance = distance;
                    bestIndex = i;
                }
            }

            if (bestIndex != -1) {
                config.logger().log("[LEVEL3] Wähle Richtung mit größter Distance: " +
                        directions[bestIndex] + " (Distance: " + maxDistance + ")");
                return new GoAction(directions[bestIndex]);
            }

            // Falls immer noch nichts: Original behalten (besser als gar nichts)
            config.logger().log("[LEVEL3] Keine Alternative! Akzeptiere geplante Richtung.");
        }

        // Keine Änderung nötig  geplante Richtung ist sicher
        return plannedAction;
    }

    /**
     * Hilfsmethode: Wandelt Direction in Array-Index um
     *
     * @param direction Die Richtung
     * @return Index (0=north, 1=east, 2=south, 3=west)
     */
    private int getDirectionIndex(Direction direction) {
        return switch (direction) {
            case north -> 0;
            case east -> 1;
            case south -> 2;
            case west -> 3;
        };
    }
}
