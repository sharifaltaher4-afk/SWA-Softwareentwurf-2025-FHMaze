package de.fhms.eti.players;

/**
 * MyBot - KORRIGIERT: Bessere Formularsuche und FINISH-Erkennung
 */
public class MyBot {
    
    private static int myPlayerId;
    
    public static void main(String[] args) {
        GameCommunication comm = new GameCommunication();
        Logger logger = Logger.getInstance();
        
        try {
            InitData initData = comm.readInit();
            Maze maze = new Maze(initData.sizeX, initData.sizeY, initData.level);
            
            myPlayerId = initData.playerId;

            String mazeName = "Unknown";
            if (args.length > 0) {
                mazeName = args[0];
            }
            logger.initialize(mazeName, myPlayerId, initData.level);

            LevelDetector levelDetector = new LevelDetector();
            levelDetector.setCurrentLevel(initData.level);
            FormManager formManager = new FormManager(myPlayerId);
            
            // Setze PlayerId SOFORT nach Erstellung
            INavigationStrategy strategy = new SimpleNavigationStrategy(maze);
            strategy.setPlayerId(myPlayerId);
            Navigator navigator = new Navigator(maze, initData.startPosition, strategy);
            
            logger.log("Bot initialisiert - ID: " + myPlayerId + 
                      ", Startposition: " + initData.startPosition);
            
            TurnData turnData;
            int turnCount = 0;
            
            Position lastPosition = initData.startPosition;
            int stuckCounter = 0;
            boolean useSimpleStrategyOnly = false;
            int simpleStrategyForceRounds = 0;
            
            while ((turnData = comm.readTurn()) != null) {
                turnCount++;
                logger.log("=== RUNDE " + turnCount + " ===");
                
                Position currentPos = navigator.getCurrentPosition();
                
                if (turnCount <= 50 ) {
                    logger.logCellStatus(currentPos, 
                    turnData.currentCell, 
                    turnData.northCell, 
                    turnData.eastCell,
                    turnData.southCell, 
                    turnData.westCell);
                }
                
                int oldLevel = levelDetector.getCurrentLevel();
                levelDetector.detectLevel(turnData.currentCell);
                int newLevel = levelDetector.getCurrentLevel();
                System.err.println("new level= "+newLevel+ " | oldlevel="+oldLevel);
                if (oldLevel != newLevel) {
                    logger.logLevelDetection(turnData.currentCell, newLevel);
                }

                // Verarbeite Formulare VOR shouldFinish
                processFormActions(turnData, comm, formManager, levelDetector, logger, myPlayerId);

                if (turnData.currentCell.startsWith("FINISH")) {
                    processFinishCell(turnData.currentCell, myPlayerId, formManager, levelDetector, navigator, logger);
                }

                // Prüfe ob finish möglich BEVOR wir weitergehen
                if (shouldFinish(turnData.currentCell, myPlayerId, formManager, levelDetector)) {
                    comm.sendAction("finish");
                    logger.log("FINISH gesendet - Mission erfüllt!");
                    break;
                }

                boolean finishInSight =
                            turnData.currentCell.startsWith("FINISH " + myPlayerId)
                            || turnData.northCell.startsWith("FINISH " + myPlayerId)
                            || turnData.eastCell.startsWith("FINISH " + myPlayerId)
                            || turnData.southCell.startsWith("FINISH " + myPlayerId)
                            || turnData.westCell.startsWith("FINISH " + myPlayerId);
                // Strategie-Wechsel nur wenn nicht blockiert
                if (levelDetector.isLevel2() && !finishInSight &&
                    !(navigator.getStrategy() instanceof FormCollectionStrategy) &&
                    !useSimpleStrategyOnly) {
                    logger.log("Wechsle zu FormCollectionStrategy für Level 2");
                    INavigationStrategy newStrategy = new FormCollectionStrategy(maze, formManager);
                    newStrategy.setPlayerId(myPlayerId);  // WICHTIG!
                    navigator.setStrategy(newStrategy);
                }
                
                // Countdown für Force-Rounds
                if (simpleStrategyForceRounds > 0) {
                    simpleStrategyForceRounds--;
                    if (simpleStrategyForceRounds == 0) {
                        useSimpleStrategyOnly = false;
                        logger.log("SimpleStrategy-Sperre aufgehoben");
                    }
                }
                
                // Endlosschleifen-Erkennung
                if (turnCount > 25 && navigator.getStrategy() instanceof FormCollectionStrategy) {
                    FormCollectionStrategy formStrat = (FormCollectionStrategy) navigator.getStrategy();
                    int visitCount = maze.getVisitCount(currentPos);
                    
                    if (currentPos.equals(lastPosition)) {
                        stuckCounter++;
                    } else {
                        stuckCounter = 0;
                    }
                    
                    // Wechsel zu Simple wenn stuck
                    if ((visitCount >= 3 || stuckCounter >= 2) && formStrat.getTargetPosition() == null) {
                        logger.log("ENDLOSSCHLEIFE ERKANNT bei " + currentPos);
                        logger.log(" Wechsle zurück zu SimpleNavigationStrategy");
                        
                        INavigationStrategy simpleStrat = new SimpleNavigationStrategy(maze);
                        simpleStrat.setPlayerId(myPlayerId);  
                        navigator.setStrategy(simpleStrat);
                        stuckCounter = 0;
                        
                        useSimpleStrategyOnly = true;
                        simpleStrategyForceRounds = 50;
                        logger.log("FormCollectionStrategy für 50 Runden blockiert");
                    }
                }
                
                lastPosition = currentPos;
                
                if (!formManager.getCollectedForms().isEmpty()) {
                    logger.logCollectedForms(formManager.getCollectedForms(), formManager.getNextFormId());
                }
                
                // Bewegungsentscheidung
                String action = navigator.decideNextMove(
                    turnData.northCell, turnData.eastCell, 
                    turnData.southCell, turnData.westCell
                );
                
                if (action == null) {
                    logger.log("Keine optimale Richtung! Fallback.");
                    action = getFallbackAction(turnData);
                }
                
                comm.sendAction(action);
                Position oldPos = navigator.getCurrentPosition();
                navigator.updatePosition(action);
                Position newPos = navigator.getCurrentPosition();
                logger.logMovement(oldPos, newPos, action, "Navigation");
                
                if (turnCount < 100 || turnCount % 10 == 0) {
                    logger.log("Bot " + myPlayerId + 
                             " | Level: " + levelDetector.getCurrentLevel() + 
                             " | Formulare: " + formManager.getCollectedForms() + 
                             " | Runde: " + turnCount);
                }
                
                if (turnCount > 1000) {
                    logger.log("ABBRUCH: Zu viele Runden (>1000)");
                    break;
                }
            }
            
            logger.log("Spiel beendet nach " + turnCount + " Runden");
            
        } catch (Exception e) {
            logger.log("FEHLER: " + e.getMessage());
            e.printStackTrace();
        } finally {
            comm.close();
            logger.close();
        }
    }
    
    private static void processFormActions(TurnData turnData, GameCommunication comm, 
                                         FormManager formManager, LevelDetector levelDetector, 
                                         Logger logger, int myPlayerId) {
        if (turnData.currentCell.startsWith("FORM")) {
            boolean canTake = formManager.canTakeForm(turnData.currentCell);
            
            if (canTake) {
                logger.logFormInfo(turnData.currentCell, true, "Kann genommen werden");
                boolean success = formManager.takeForm(turnData.currentCell);
                if (success) {
                    comm.sendAction("take");
                    logger.log("Formular genommen: take");
                    logger.logCollectedForms(formManager.getCollectedForms(), formManager.getNextFormId());
                }
            } else {
                // Debug-Info warum nicht genommen werden kann
                try {
                    String[] parts = turnData.currentCell.split(" ");
                    if (parts.length >= 3) {
                        int formPlayerId = Integer.parseInt(parts[1]);
                        int formId = Integer.parseInt(parts[2]);
                        int expectedId = formManager.getNextFormId();
                        
                        if (formPlayerId != myPlayerId) {
                            logger.logFormInfo(turnData.currentCell, false, "Falscher Spieler");
                        } else if (formId != expectedId) {
                            logger.logFormInfo(turnData.currentCell, false, 
                                "Falsche Reihenfolge: Erwartet " + expectedId + ", Gefunden " + formId);
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.logFormInfo(turnData.currentCell, false, "Ungültiges Format");
                }
            }
        }
    }
    
    /**
     * shouldFinish mit besserem Logging
     */
    private static boolean shouldFinish(String currentCell, int myPlayerId, 
                                       FormManager formManager, LevelDetector levelDetector) {
        if (!currentCell.startsWith("FINISH")) {
            return false;
        }
        
        try {
            String[] parts = currentCell.split(" ");
            int finishPlayerId = Integer.parseInt(parts[1]);
            
            if (finishPlayerId != myPlayerId) {
                System.err.println("FINISH gehört Spieler " + finishPlayerId + ", nicht mir (" + myPlayerId + ")");
                return false;
            }
            
            // Eigenes FINISH gefunden!
            if (levelDetector.isLevel1()) {
                System.err.println("Level 1: Kann finishen!");
                return true;
            } else if (levelDetector.isLevel2()) {
                boolean hasAll = formManager.hasAllForms();
                System.err.println("Level 2: Habe alle Formulare? " + hasAll + 
                                 " (" + formManager.getCollectedForms().size() + " gesammelt)");
             if (!hasAll) {
                return hasAll;
            }
            return hasAll;
                
        }
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Parsen: " + currentCell);
        }
        
        return false;
    }

    private static void processFinishCell(String finishCell, int myPlayerId, FormManager formManager, 
                                        LevelDetector levelDetector, Navigator navigator, Logger logger) {
        try {
            String[] parts = finishCell.split(" ");
            int finishPlayerId = Integer.parseInt(parts[1]);
            
            if (finishPlayerId == myPlayerId) {
                if (levelDetector.isLevel2() && parts.length >= 3) {
                    int formCount = Integer.parseInt(parts[2]);
                    formManager.setTotalFormsNeeded(formCount);
                    logger.log("Eigenes Ziel! Benötigte Formulare: " + formCount);
                    
                    if (navigator.getStrategy() instanceof FormCollectionStrategy) {
                        FormCollectionStrategy formStrategy = (FormCollectionStrategy) navigator.getStrategy();
                        formStrategy.setTargetPosition(navigator.getCurrentPosition());
                        logger.log("Ziel-Position gemerkt: " + navigator.getCurrentPosition());
                    }
                } else if (levelDetector.isLevel1()) {
                    logger.log("Level 1 Ziel gefunden");
                }
            } else {
                logger.log("Fremdes Ziel von Spieler " + finishPlayerId);
            }
        } catch (NumberFormatException e) {
            logger.log("Fehler beim Parsen: " + finishCell);
        }
    }
    
    private static String getFallbackAction(TurnData turnData) {
        if (!turnData.northCell.equals("WALL")) return "go north";
        else if (!turnData.eastCell.equals("WALL")) return "go east";
        else if (!turnData.southCell.equals("WALL")) return "go south";
        else if (!turnData.westCell.equals("WALL")) return "go west";
        else return "go north"; 
    }
}