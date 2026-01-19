package de.fhms.eti.players.creation;

import de.fhms.eti.players.communication.*;
import de.fhms.eti.players.domain.FormManager;
import de.fhms.eti.players.domain.LevelDetector;
import de.fhms.eti.players.domain.Maze;
import de.fhms.eti.players.domain.SheetManager;

/**
 * MyBot - Optimierte Version mit Erzeugungsmustern
 * <p>
 * LEVEL 3: Cell-basierte Gegner-Erkennung (KEIN OpponentDetector nötig!)
 * <p>
 * ENTWURFSMUSTER:
 * Factory Method Pattern für Strategie-Erzeugung
 * Builder Pattern für Bot-Konfiguration
 * Singleton Pattern für GameController
 * Dependency Injection
 * <p>
 * SOLID-PRINZIPIEN:
 * - SRP: Jede Klasse hat eine klare Verantwortung
 * - OCP: Erweiterbar ohne Änderung
 * - LSP: Alle Strategien sind austauschbar
 * - ISP: Kleine, fokussierte Interfaces
 * - DIP: Abhängigkeit von Abstraktionen
 */
public class MyBot {

    public static void main(String[] args) {
        ISerialize serializer = new TextSerializer();
        IDeserialize deserializer = new TextDeserializer();
        GameCommunication comm = new GameCommunication(serializer, deserializer);
        Logger logger = Logger.getInstance();

        try {
            // PHASE 1: INITIALISIERUNG
            InitData initData = comm.readInit();

            String mazeName = args.length > 0 ? args[0] : "Unknown";
            logger.initialize(mazeName, initData.playerId, initData.level);

            // PHASE 2: KOMPONENTEN-ERSTELLUNG
            Maze maze = new Maze(initData.sizeX, initData.sizeY, initData.level);
            LevelDetector levelDetector = new LevelDetector();
            levelDetector.setCurrentLevel(initData.level);
            FormManager formManager = new FormManager(initData.playerId);
            SheetManager sheetManager = new SheetManager(initData.sheetCount);

            // Factory für Strategien
            INavigationStrategyFactory strategyFactory = new NavigationStrategyFactory();

            // Builder Pattern für Bot-Konfiguration
            BotConfig botConfig = new BotConfigBuilder()
                    .withMaze(maze)
                    .withFormManager(formManager)
                    .withSheetManager(sheetManager)
                    .withStrategyFactory(strategyFactory)
                    .withLogger(logger)
                    .withLevelDetector(levelDetector)
                    .withPlayerId(initData.playerId)
                    .withStartPosition(initData.startPosition)
                    .build();

            logger.log(" Bot-Konfiguration erstellt");
            logger.log("Bot initialisiert - ID: " + initData.playerId +
                    ", Startposition: " + initData.startPosition);
            logger.log("Level 3: Cell-basierte Gegner-Erkennung aktiv");

            // PHASE 3: SPIELSTEUERUNG
            GameController controller = GameController.getInstance();
            controller.initialize(botConfig, comm);

            logger.log("GameController (Singleton) initialisiert");

            // PHASE 4: HAUPT-SPIELSCHLEIFE
            TurnData turnData;
            int turnCount = 0;

            while ((turnData = comm.readTurn()) != null) {
                turnCount++;
                logger.log("=== RUNDE " + turnCount + " ===");

                boolean continueGame = controller.processTurn(turnData, turnCount);

                if (!continueGame) {
                    break; // Spiel beendet
                }

                if (turnCount > 1000) {
                    logger.log("[TIMEOUT] Zu viele Runden (>1000)");
                    break;
                }
            }

            logger.log(" Spiel beendet nach " + turnCount + " Runden");

        } catch (Exception e) {
            logger.log("[FEHLER] " + e.getMessage());
            e.printStackTrace();
        } finally {
            comm.close();
            logger.close();
            GameController.reset();
        }
    }
}
