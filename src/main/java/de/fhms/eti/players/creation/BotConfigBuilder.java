package de.fhms.eti.players.creation;

import de.fhms.eti.players.domain.FormManager;
import de.fhms.eti.players.domain.LevelDetector;
import de.fhms.eti.players.domain.Maze;
import de.fhms.eti.players.domain.Position;
import de.fhms.eti.players.communication.Logger;

/**
 * Builder fuer Bot-Konfiguration
 * <p>
 * Implementiert das Builder Pattern (Erzeugungsmuster)
 * LEVEL 3: OpponentDetector NICHT mehr noetig - Cell enthält Gegner-Info!
 * <p>
 * Vorteile:
 * - Schrittweise Konfiguration komplexer Objekte
 * - Fluent Interface fuer bessere Lesbarkeit
 * - Unveraenderliche Konfigurationsobjekte
 * - Validierung vor Objekt-Erstellung
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
     * @return Unveraenderliche BotConfig
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
