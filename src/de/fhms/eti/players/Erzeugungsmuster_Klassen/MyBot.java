package de.fhms.eti.players.Erzeugungsmuster_Klassen;

import de.fhms.eti.players.Domain_Klassen.FormManager;
import de.fhms.eti.players.Domain_Klassen.LevelDetector;
import de.fhms.eti.players.Domain_Klassen.Maze;
import de.fhms.eti.players.Kommunikations_Klassen.GameCommunication;
import de.fhms.eti.players.Kommunikations_Klassen.InitData;
import de.fhms.eti.players.Kommunikations_Klassen.Logger;
import de.fhms.eti.players.Kommunikations_Klassen.TurnData;

/**
 * MyBot - Optimierte Version mit Erzeugungsmustern
 * 
 * REFACTORING-ÄNDERUNGEN:
 *   Factory Method Pattern für Strategie-Erzeugung
 *   Builder Pattern für Bot-Konfiguration
 *   Singleton Pattern für GameController
 *   Dependency Injection statt hardcodierte Abhängigkeiten
 *   Reduzierte Komplexität der main-Methode
 *   Bessere Trennung von Verantwortlichkeiten (SRP)
 *   Höhere Testbarkeit durch Entkopplung
 * 
 * SOLID-PRINZIPIEN:
 * - SRP: Jede Klasse hat eine klare Verantwortung
 * - OCP: Erweiterbar ohne Änderung (neue Strategien, Konfigurationen)
 * - LSP: Alle Strategien sind austauschbar
 * - ISP: Kleine, fokussierte Interfaces
 * - DIP: Abhängigkeit von Abstraktionen
 */
public class MyBot {
    
    public static void main(String[] args) {
        GameCommunication comm = new GameCommunication();
        Logger logger = Logger.getInstance();
        
        try {
            // 📖 PHASE 1: INITIALISIERUNG
            InitData initData = comm.readInit();
            
            String mazeName = args.length > 0 ? args[0] : "Unknown";
            logger.initialize(mazeName, initData.playerId, initData.level);
            
            // [FACTORY] PHASE 2: KOMPONENTEN-ERSTELLUNG MIT ERZEUGUNGSMUSTERN
            
            // Erstelle Basis-Komponenten
            Maze maze = new Maze(initData.sizeX, initData.sizeY, initData.level);
            LevelDetector levelDetector = new LevelDetector();
            levelDetector.setCurrentLevel(initData.level);
            FormManager formManager = new FormManager(initData.playerId);
            
            // Erstelle Factory für Strategien (Factory Method Pattern)
            INavigationStrategyFactory strategyFactory = new NavigationStrategyFactory();
            
            // 🔨 Erstelle Bot-Konfiguration mit Builder Pattern
            BotConfig botConfig = new BotConfigBuilder()
                .withMaze(maze)
                .withFormManager(formManager)
                .withStrategyFactory(strategyFactory)
                .withLogger(logger)
                .withLevelDetector(levelDetector)
                .withPlayerId(initData.playerId)
                .withStartPosition(initData.startPosition)
                .build();
            
            logger.log("  Bot-Konfiguration erstellt mit Builder Pattern");
            logger.log("Bot initialisiert - ID: " + initData.playerId + 
                      ", Startposition: " + initData.startPosition);
            
            // PHASE 3: SPIELSTEUERUNG MIT SINGLETON
            GameController controller = GameController.getInstance();
            controller.initialize(botConfig, comm);
            
            logger.log("GameController (Singleton) initialisiert");
            
            // [INFO] PHASE 4: HAUPT-SPIELSCHLEIFE
            TurnData turnData;
            int turnCount = 0;
            
            while ((turnData = comm.readTurn()) != null) {
                turnCount++;
                logger.log("=== RUNDE " + turnCount + " ===");
                
                // Verarbeite Runde durch Controller
                boolean continueGame = controller.processTurn(turnData, turnCount);
                
                if (!continueGame) {
                    break; // Spiel beendet (FINISH erreicht)
                }
                
                // Sicherheits-Abbruch
                if (turnCount > 1000) {
                    logger.log("[TIMEOUT] ABBRUCH: Zu viele Runden (>1000)");
                    break;
                }
            }
            
            logger.log("  Spiel beendet nach " + turnCount + " Runden");
            
        } catch (Exception e) {
            logger.log("[FEHLER] FEHLER: " + e.getMessage());
            e.printStackTrace();
        } finally {
            comm.close();
            logger.close();
            GameController.reset(); // Reset für nächstes Spiel
        }
    }
}