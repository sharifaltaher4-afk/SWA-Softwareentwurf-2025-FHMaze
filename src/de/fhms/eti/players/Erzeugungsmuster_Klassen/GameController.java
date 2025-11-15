package de.fhms.eti.players.Erzeugungsmuster_Klassen;

import de.fhms.eti.players.Domain_Klassen.Navigator;
import de.fhms.eti.players.Domain_Klassen.Position;
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
 * Erfüllt SOLID:
 * - SRP: Nur Spielablauf-Koordination
 * - OCP: Erweiterbar durch neue Strategien
 * - DIP: Abhängig von Interfaces
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
     * Private Konstruktor für Singleton
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
     * @param comm GameCommunication
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
     * 
     * @param turnData Rundendaten
     * @param turnCount Rundennummer
     * @return true wenn Spiel weitergehen soll, false bei Beendigung
     */
    public boolean processTurn(TurnData turnData, int turnCount) {
        Position currentPos = navigator.getCurrentPosition();
        
        // Logging
        if (turnCount <= 50) {
            config.getLogger().logCellStatus(currentPos, 
                turnData.currentCell, 
                turnData.northCell, 
                turnData.eastCell,
                turnData.southCell, 
                turnData.westCell);
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
        if (turnData.currentCell.startsWith("FINISH")) {
            processFinishCell(turnData.currentCell);
        }
        
        // Prüfe ob finish möglich
        if (shouldFinish(turnData.currentCell)) {
            comm.sendAction("finish");
            config.getLogger().log("[SUCCESS] FINISH gesendet - Mission erfüllt!");
            return false; // Spiel beenden
        }
        
        // Strategie-Management
        manageStrategy(turnCount, currentPos);
        
        // Bewegungsentscheidung
        String action = navigator.decideNextMove(
            turnData.northCell, turnData.eastCell, 
            turnData.southCell, turnData.westCell
        );
        
        if (action == null) {
            config.getLogger().log("  Keine optimale Richtung! Fallback.");
            action = getFallbackAction(turnData);
        }
        
        // Aktion ausführen
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
        
        // Erstelle neue Strategie für neues Level
        INavigationStrategy newStrategy = config.getStrategyFactory()
            .createStrategy(newLevel, config.getMaze(), config.getFormManager());
        
        newStrategy.setPlayerId(config.getPlayerId());
        navigator.setStrategy(newStrategy);
    }
    
    /**
     * Verarbeitet Formular-Aktionen
     */
    private boolean processFormActions(TurnData turnData, int turnCount) {
    if (turnData.currentCell.startsWith("FORM")) {
        boolean canTake = config.getFormManager().canTakeForm(turnData.currentCell);
        
        if (canTake) {
            config.getLogger().logFormInfo(turnData.currentCell, true, "Kann genommen werden");
            boolean success = config.getFormManager().takeForm(turnData.currentCell);
            if (success) {
                comm.sendAction("take");
                config.getLogger().log("[INFO] Formular genommen: take");
                config.getLogger().logCollectedForms(
                    config.getFormManager().getCollectedForms(), 
                    config.getFormManager().getNextFormId()
                );
                return true;  // ← Formular genommen, KEINE Bewegung mehr!
            }
        } else {
            logFormRejectionReason(turnData.currentCell);
        }
    }
    return false;  // WICHTIG! Kein Formular genommen, Bewegung erlauben
    }
    
    /**
     * Loggt warum Formular nicht genommen werden kann
     */
    private void logFormRejectionReason(String cellStatus) {
        try {
            String[] parts = cellStatus.split(" ");
            if (parts.length >= 3) {
                int formPlayerId = Integer.parseInt(parts[1]);
                int formId = Integer.parseInt(parts[2]);
                int expectedId = config.getFormManager().getNextFormId();
                
                if (formPlayerId != config.getPlayerId()) {
                    config.getLogger().logFormInfo(cellStatus, false, "Falscher Spieler");
                } else if (formId != expectedId) {
                    config.getLogger().logFormInfo(cellStatus, false, 
                        "Falsche Reihenfolge: Erwartet " + expectedId + ", Gefunden " + formId);
                }
            }
        } catch (NumberFormatException e) {
            config.getLogger().logFormInfo(cellStatus, false, "Ungültiges Format");
        }
    }
    
    /**
     * Verarbeitet FINISH-Zelle
     */
    private void processFinishCell(String finishCell) {
        try {
            String[] parts = finishCell.split(" ");
            int finishPlayerId = Integer.parseInt(parts[1]);
            
            if (finishPlayerId == config.getPlayerId()) {
                if (config.getLevelDetector().isLevel2() && parts.length >= 3) {
                    int formCount = Integer.parseInt(parts[2]);
                    config.getFormManager().setTotalFormsNeeded(formCount);
                    config.getLogger().log("[INFO] Eigenes Ziel! Benötigte Formulare: " + formCount);
                    
                    if (navigator.getStrategy() instanceof FormCollectionStrategy) {
                        FormCollectionStrategy formStrategy = (FormCollectionStrategy) navigator.getStrategy();
                        formStrategy.setTargetPosition(navigator.getCurrentPosition());
                        config.getLogger().log("[POS] Ziel-Position gemerkt: " + navigator.getCurrentPosition());
                    }
                } else if (config.getLevelDetector().isLevel1()) {
                    config.getLogger().log("[INFO] Level 1 Ziel gefunden");
                }
            } else {
                config.getLogger().log("[BLOCKED] Fremdes Ziel von Spieler " + finishPlayerId);
            }
        } catch (NumberFormatException e) {
            config.getLogger().log("  Fehler beim Parsen: " + finishCell);
        }
    }
    
    /**
     * Prüft ob FINISH ausgeführt werden kann
     */
    private boolean shouldFinish(String currentCell) {
        if (!currentCell.startsWith("FINISH")) {
            return false;
        }
        
        try {
            String[] parts = currentCell.split(" ");
            int finishPlayerId = Integer.parseInt(parts[1]);
            
            if (finishPlayerId != config.getPlayerId()) {
                return false;
            }
            
            if (config.getLevelDetector().isLevel1()) {
                return true;
            } else if (config.getLevelDetector().isLevel2()) {
                return config.getFormManager().hasAllForms();
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
        // Strategie-Wechsel zu FormCollectionStrategy für Level 2
        if (config.getLevelDetector().isLevel2() && 
            !(navigator.getStrategy() instanceof FormCollectionStrategy) &&
            !useSimpleStrategyOnly) {
            
            config.getLogger().log("[CHANGE] Wechsle zu FormCollectionStrategy für Level 2");
            INavigationStrategy newStrategy = config.getStrategyFactory()
                .createStrategyByType(StrategyType.FORM_COLLECTION, 
                                    config.getMaze(), 
                                    config.getFormManager());
            newStrategy.setPlayerId(config.getPlayerId());
            navigator.setStrategy(newStrategy);
        }
        
        // Countdown für Force-Rounds
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
            config.getLogger().log("   - Besuchszähler: " + visitCount);
            config.getLogger().log("   - Stuck-Counter: " + stuckCounter);
            config.getLogger().log("[CHANGE] Wechsle zurück zu SimpleNavigationStrategy");
            
            INavigationStrategy simpleStrat = config.getStrategyFactory()
                .createStrategyByType(StrategyType.SIMPLE, config.getMaze(), null);
            simpleStrat.setPlayerId(config.getPlayerId());
            navigator.setStrategy(simpleStrat);
            stuckCounter = 0;
            
            useSimpleStrategyOnly = true;
            simpleStrategyForceRounds = 100;
            config.getLogger().log("🔒 FormCollectionStrategy für 100 Runden blockiert");
        }
    }
    
    /**
     * Fallback-Aktion wenn keine optimale Richtung gefunden
     */
    private String getFallbackAction(TurnData turnData) {
        if (!turnData.northCell.equals("WALL")) return "go north";
        else if (!turnData.eastCell.equals("WALL")) return "go east";
        else if (!turnData.southCell.equals("WALL")) return "go south";
        else if (!turnData.westCell.equals("WALL")) return "go west";
        else return "go north";
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
     * Reset für neues Spiel
     */
    public static void reset() {
        instance = null;
    }
}

