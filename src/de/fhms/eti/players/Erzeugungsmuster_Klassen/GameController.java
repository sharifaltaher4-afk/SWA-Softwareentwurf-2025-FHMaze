package de.fhms.eti.players.Erzeugungsmuster_Klassen;

import de.fhms.eti.players.Domain_Klassen.*;
import de.fhms.eti.players.Kommunikations_Klassen.GameCommunication;
import de.fhms.eti.players.Kommunikations_Klassen.TurnData;
import de.fhms.eti.players.Strategie_Klassen.FormCollectionStrategy;
import de.fhms.eti.players.Strategie_Klassen.INavigationStrategy;

/**
 * GameController - Zentrale Spielsteuerung
 * 
 * Implementiert Singleton Pattern (Erzeugungsmuster)
 * 
 * Verantwortung (SRP):
 * - Koordination des Spielablaufs
 * - Verwaltung von Komponenten
 * - Strategie-Wechsel
 * 
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

    private Position lastPosition;
    private int stuckCounter;
    private boolean useSimpleStrategyOnly;
    private int simpleStrategyForceRounds;

    /**
     * Private Konstruktor fuer Singleton
     */
    private GameController() {
        this.stuckCounter = 0;
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
     * Initialisiert den Controller mit Konfiguration
     * 
     * @param config Bot-Konfiguration
     * @param comm   GameCommunication
     */
    public void initialize(BotConfig config, GameCommunication comm) {
        this.config = config;
        this.comm = comm;

        // Erstelle initiale Strategie
        INavigationStrategy initialStrategy = config.getStrategyFactory()
                .createStrategy(config.getLevelDetector().getCurrentLevel(),
                        config.getMaze(),
                        config.getFormManager());

        initialStrategy.setPlayerId(config.getPlayerId());

        // Erstelle Navigator
        this.navigator = new Navigator(config.getMaze(), config.getStartPosition(), initialStrategy);

        this.lastPosition = config.getStartPosition();

        config.getLogger().log("GameController initialisiert");
    }

    /**
     * Verarbeitet eine einzelne Spielrunde
     * LEVEL 3: Cell-Objekte enthalten jetzt direkt Gegner-Info!
     * @param turnData  Rundendaten
     * @param turnCount Rundennummer
     * @return true wenn Spiel weitergehen soll, false bei Beendigung
     */
    public boolean processTurn(TurnData turnData, int turnCount) {
        Position currentPos = navigator.getCurrentPosition();
        // Logging Gegner-Status (aus Cell-Objekten!)
        int opponentCount = countOpponents(turnData);
        if (opponentCount > 0) {
            config.getLogger().log("[LEVEL3]  " + opponentCount + " Gegner sichtbar");
        }

        // Logging
        if (turnCount <= 20) {
            config.getLogger().log("ZELLSTATUS:");
            config.getLogger().log("Aktuell: " + turnData.currentCell +
                    " | hasOpponent=" + turnData.currentCell.hasOpponent() +
                    " | Distance=" + turnData.currentCell.getOpponentDistance());
            config.getLogger().log(
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
        int oldLevel = config.getLevelDetector().getCurrentLevel();
        config.getLevelDetector().detectLevel(turnData.currentCell);
        int newLevel = config.getLevelDetector().getCurrentLevel();

        if (oldLevel != newLevel) {
            config.getLogger().logLevelDetection(turnData.currentCell, newLevel);
            handleLevelChange(newLevel);
        }

        // Formular-Verarbeitung
        if (processFormActions(turnData, turnCount)) {
            return true; // Formular genommen, Runde beenden
        }

        // FINISH-Zelle Verarbeitung
        if (turnData.currentCell instanceof FinishCell finishCell) {
            processFinishCell(finishCell);
        }

        // Pruefe ob finish mÃ¶glich
        if (shouldFinish(turnData.currentCell)) {
            comm.sendAction(new FinishAction());
            config.getLogger().log("[SUCCESS] FINISH gesendet - Mission erfuellt!");
            return false; // Spiel beenden
        }

        // Strategie-Management
        manageStrategy(turnCount, currentPos);

        // Bewegungsentscheidung
        GoAction action = navigator.decideNextMove(
                turnData.northCell, turnData.eastCell,
                turnData.southCell, turnData.westCell);

        if (action == null) {
            config.getLogger().log("  Keine optimale Richtung! Fallback.");
            action = getFallbackAction(turnData);
        }

        // LEVEL 3: Kollisionsvermeidung
        action = avoidOpponentCollision(action, turnData);

        // Aktion ausfuehren
        comm.sendAction(action);
        Position oldPos = navigator.getCurrentPosition();
        navigator.updatePosition(action);
        Position newPos = navigator.getCurrentPosition();
        config.getLogger().logMovement(oldPos, newPos, action, "Navigation");

        // Status-Logging
        if (turnCount < 100 || turnCount % 10 == 0) {
            logStatus(turnCount);
        }

        lastPosition = currentPos;

        return true; // Spiel weitergehen
    }

    /**
     * Behandelt Level-Wechsel
     */
    private void handleLevelChange(int newLevel) {
        config.getLogger().log("[STAT] Level-Wechsel zu Level " + newLevel);

        // Erstelle neue Strategie fuer neues Level
        INavigationStrategy newStrategy = config.getStrategyFactory()
                .createStrategy(newLevel, config.getMaze(), config.getFormManager());

        newStrategy.setPlayerId(config.getPlayerId());
        navigator.setStrategy(newStrategy);
    }

    /**
     * Verarbeitet Formular-Aktionen
     */
    private boolean processFormActions(TurnData turnData, int turnCount) {
        if (turnData.currentCell instanceof FormCell formCell) {
            boolean canTake = config.getFormManager().canTakeForm(formCell);

            if (canTake) {
                config.getLogger().logFormInfo(turnData.currentCell, true, "Kann genommen werden");
                boolean success = config.getFormManager().takeForm(formCell);
                if (success) {
                    comm.sendAction(new TakeAction());
                    config.getLogger().log("[INFO] Formular genommen: take");
                    config.getLogger().logCollectedForms(
                            config.getFormManager().getCollectedForms(),
                            config.getFormManager().getNextFormId());
                    return true; // â† Formular genommen, KEINE Bewegung mehr!
                }
            } else {
                logFormRejectionReason(formCell);
            }
        }
        return false; // WICHTIG! Kein Formular genommen, Bewegung erlauben
    }

    /**
     * Loggt warum Formular nicht genommen werden kann
     */
    private void logFormRejectionReason(FormCell cellStatus) {
        int formPlayerId = cellStatus.getPlayerId();
        int formId = cellStatus.getFormId();
        int expectedId = config.getFormManager().getNextFormId();

        if (formPlayerId != config.getPlayerId()) {
            config.getLogger().logFormInfo(cellStatus, false, "Falscher Spieler");
        } else if (formId != expectedId) {
            config.getLogger().logFormInfo(cellStatus, false,
                    "Falsche Reihenfolge: Erwartet " + expectedId + ", Gefunden " + formId);
        }
    }

    /**
     * Verarbeitet FINISH-Zelle
     */
    private void processFinishCell(FinishCell finishCell) {
        int finishPlayerId = finishCell.getPlayerId();

        // Level 2/3: Form-Count aus FINISH-Zelle holen (gilt für ALLE Spieler!)
        if (config.getLevelDetector().isLevel2() || config.getLevelDetector().isLevel3()) {
            int formCount = finishCell.getFormCount();

            // Setze Form-Count auch wenn es fremdes FINISH ist!
            // In Level 2/3 haben alle Spieler die gleiche Anzahl Formulare!
            if (config.getFormManager().getTotalFormsNeeded() == -1) {
                config.getFormManager().setTotalFormsNeeded(formCount);
                config.getLogger().log("[INFO] Form-Count aus FINISH erkannt: " + formCount);
            }

            // Zusätzliche Infos wenn es UNSER FINISH ist
            if (finishPlayerId == config.getPlayerId()) {
                config.getLogger().log("[INFO] Eigenes Ziel gefunden!");

                if (navigator.getStrategy() instanceof FormCollectionStrategy formStrategy) {
                    formStrategy.setTargetPosition(navigator.getCurrentPosition());
                    config.getLogger().log("[POS] Ziel-Position gemerkt: " + navigator.getCurrentPosition());
                }
            } else {
                config.getLogger().log("[BLOCKED] Fremdes Ziel von Spieler " + finishPlayerId);
            }
        } else if (config.getLevelDetector().isLevel1()) {
            if (finishPlayerId == config.getPlayerId()) {
                config.getLogger().log("[INFO] Level 1 Ziel gefunden");
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

            if (finishPlayerId != config.getPlayerId()) {
                return false;
            }

            if (config.getLevelDetector().isLevel1()) {
                return true; // Level 1: Sofort FINISH
            } else if (config.getLevelDetector().isLevel2()) {
                return config.getFormManager().hasAllForms(); // Level 2: Alle Formulare
            } else if (config.getLevelDetector().isLevel3()) {
                return config.getFormManager().hasAllForms(); // Level 3: Alle Formulare + Gegner vermeiden
            }
        } catch (NumberFormatException e) {
            config.getLogger().log("  Fehler beim Parsen: " + currentCell);
        }

        return false;
    }

    /**
     * Verwaltet Strategie-Wechsel basierend auf Spielsituation
     */
    private void manageStrategy(int turnCount, Position currentPos) {
        // Strategie-Wechsel zu FormCollectionStrategy für Level 2 UND Level 3
        if ((config.getLevelDetector().isLevel2() || config.getLevelDetector().isLevel3()) &&
                !(navigator.getStrategy() instanceof FormCollectionStrategy) &&
                !useSimpleStrategyOnly) {

            int level = config.getLevelDetector().isLevel3() ? 3 : 2;
            config.getLogger().log("[CHANGE] Wechsle zu FormCollectionStrategy für Level " + level);
            INavigationStrategy newStrategy = config.getStrategyFactory()
                    .createStrategyByType(StrategyType.FORM_COLLECTION,
                            config.getMaze(),
                            config.getFormManager());
            newStrategy.setPlayerId(config.getPlayerId());
            navigator.setStrategy(newStrategy);
        }

        // Countdown fuer Force-Rounds
        if (simpleStrategyForceRounds > 0) {
            simpleStrategyForceRounds--;
            if (simpleStrategyForceRounds == 0) {
                useSimpleStrategyOnly = false;
                config.getLogger().log("  SimpleStrategy-Sperre aufgehoben");
            }
        }

        // Endlosschleifen-Erkennung
        if (turnCount > 50 && navigator.getStrategy() instanceof FormCollectionStrategy) {
            detectAndHandleLoop(currentPos);
        }
    }

    /**
     * Erkennt und behandelt Endlosschleifen
     */
    private void detectAndHandleLoop(Position currentPos) {
        int visitCount = config.getMaze().getVisitCount(currentPos);

        if (currentPos.equals(lastPosition)) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
        }

        if (visitCount >= 5 || stuckCounter >= 5) {
            config.getLogger().log("  ENDLOSSCHLEIFE ERKANNT bei " + currentPos);
            config.getLogger().log("   - BesuchszÃ¤hler: " + visitCount);
            config.getLogger().log("   - Stuck-Counter: " + stuckCounter);
            config.getLogger().log("[CHANGE] Wechsle zurueck zu SimpleNavigationStrategy");

            INavigationStrategy simpleStrat = config.getStrategyFactory()
                    .createStrategyByType(StrategyType.SIMPLE, config.getMaze(), null);
            simpleStrat.setPlayerId(config.getPlayerId());
            navigator.setStrategy(simpleStrat);
            stuckCounter = 0;

            useSimpleStrategyOnly = true;
            simpleStrategyForceRounds = 100;
            config.getLogger().log("ðŸ”’ FormCollectionStrategy fuer 100 Runden blockiert");
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
        config.getLogger().log("Bot " + config.getPlayerId() +
                " | Level: " + config.getLevelDetector().getCurrentLevel() +
                " | Formulare: " + config.getFormManager().getCollectedForms() +
                " | Position: " + navigator.getCurrentPosition() +
                " | Strategie: " + navigator.getStrategy().getClass().getSimpleName() +
                " | Runde: " + turnCount);

        if (turnCount % 50 == 0) {
            Position pos = navigator.getCurrentPosition();
            int visits = config.getMaze().getVisitCount(pos);
            config.getLogger().log("[STAT] Statistik Runde " + turnCount + ": Position " + pos +
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
     * 
     * Prüft ob die geplante Aktion zu einem Feld mit Gegner führt.
     * Falls ja, wähle alternative Richtung ohne Gegner.
     * 
     * @param plannedAction Ursprüngliche geplante Aktion
     * @param turnData      Aktuelle Rundendaten
     * @return Sichere Aktion (ohne Gegner-Kollision wenn möglich)
     */
    private GoAction avoidOpponentCollision(GoAction plannedAction, TurnData turnData) {
        // Gegner-Info direkt aus Cells!
        return plannedAction; // Keine Änderung nötig
    }

    /**
     * Reset für neues Spiel
     */
    public static void reset() {
        instance = null;
    }
}
