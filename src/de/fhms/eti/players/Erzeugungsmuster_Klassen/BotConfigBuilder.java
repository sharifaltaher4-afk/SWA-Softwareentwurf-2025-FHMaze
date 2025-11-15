package de.fhms.eti.players.Erzeugungsmuster_Klassen;

import de.fhms.eti.players.Domain_Klassen.FormManager;
import de.fhms.eti.players.Domain_Klassen.LevelDetector;
import de.fhms.eti.players.Domain_Klassen.Maze;
import de.fhms.eti.players.Domain_Klassen.Position;
import de.fhms.eti.players.Kommunikations_Klassen.Logger;

/**
 * Builder für Bot-Konfiguration
 * 
 * Implementiert das Builder Pattern (Erzeugungsmuster)
 * 
 * Vorteile:
 * - Schrittweise Konfiguration komplexer Objekte
 * - Fluent Interface für bessere Lesbarkeit
 * - Unveränderliche Konfigurationsobjekte
 * - Validierung vor Objekt-Erstellung
 * 
 * Verwendung:
 * BotConfig config = new BotConfigBuilder()
 *     .withMaze(maze)
 *     .withFormManager(formManager)
 *     .withStrategyFactory(factory)
 *     .build();
 */
public class BotConfigBuilder {
    private Maze maze;
    private FormManager formManager;
    private INavigationStrategyFactory strategyFactory;
    private Logger logger;
    private LevelDetector levelDetector;
    private int playerId;
    private Position startPosition;
    
    /**
     * Setzt das Labyrinth
     */
    public BotConfigBuilder withMaze(Maze maze) {
        this.maze = maze;
        return this;
    }
    
    /**
     * Setzt den FormManager
     */
    public BotConfigBuilder withFormManager(FormManager formManager) {
        this.formManager = formManager;
        return this;
    }
    
    /**
     * Setzt die Strategy Factory
     */
    public BotConfigBuilder withStrategyFactory(INavigationStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
        return this;
    }
    
    /**
     * Setzt den Logger
     */
    public BotConfigBuilder withLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
    
    /**
     * Setzt den LevelDetector
     */
    public BotConfigBuilder withLevelDetector(LevelDetector levelDetector) {
        this.levelDetector = levelDetector;
        return this;
    }
    
    /**
     * Setzt die Spieler-ID
     */
    public BotConfigBuilder withPlayerId(int playerId) {
        this.playerId = playerId;
        return this;
    }
    
    /**
     * Setzt die Startposition
     */
    public BotConfigBuilder withStartPosition(Position startPosition) {
        this.startPosition = startPosition;
        return this;
    }
    
    /**
     * Validiert und erstellt die finale Konfiguration
     * 
     * @return Unveränderliche BotConfig
     * @throws IllegalStateException wenn erforderliche Parameter fehlen
     */
    public BotConfig build() {
        // Validierung
        if (maze == null) {
            throw new IllegalStateException("Maze ist erforderlich");
        }
        if (formManager == null) {
            throw new IllegalStateException("FormManager ist erforderlich");
        }
        if (strategyFactory == null) {
            throw new IllegalStateException("StrategyFactory ist erforderlich");
        }
        if (logger == null) {
            throw new IllegalStateException("Logger ist erforderlich");
        }
        if (levelDetector == null) {
            throw new IllegalStateException("LevelDetector ist erforderlich");
        }
        if (startPosition == null) {
            throw new IllegalStateException("StartPosition ist erforderlich");
        }
        
        return new BotConfig(maze, formManager, strategyFactory, logger, 
                           levelDetector, playerId, startPosition);
    }
}
