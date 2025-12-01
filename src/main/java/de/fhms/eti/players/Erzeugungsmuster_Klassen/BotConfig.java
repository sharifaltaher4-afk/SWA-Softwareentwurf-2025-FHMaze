package main.java.de.fhms.eti.players.Erzeugungsmuster_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.FormManager;
import main.java.de.fhms.eti.players.Domain_Klassen.LevelDetector;
import main.java.de.fhms.eti.players.Domain_Klassen.Maze;
import main.java.de.fhms.eti.players.Domain_Klassen.Position;
import main.java.de.fhms.eti.players.Kommunikations_Klassen.Logger;

/**
 * Unveränderliche Bot-Konfiguration
 * <p>
 * Wird durch BotConfigBuilder erstellt (Builder Pattern)
 * LEVEL 3: OpponentDetector NICHT mehr nötig - Cell enthält Gegner-Info!
 * Vorteile:
 * - Immutable: Keine versehentlichen Änderungen
 * - Thread-Safe: Kann sicher geteilt werden
 * - Klarheit: Alle benötigten Komponenten an einem Ort
 */
public class BotConfig {
    private final Maze maze;
    private final FormManager formManager;
    private final INavigationStrategyFactory strategyFactory;
    private final Logger logger;
    private final LevelDetector levelDetector;
    private final int playerId;
    private final Position startPosition;

    /**
     * Public Konstruktor - durch Builder aufrufbar
     */
    public BotConfig(Maze maze, FormManager formManager, INavigationStrategyFactory strategyFactory,
                     Logger logger, LevelDetector levelDetector, int playerId, Position startPosition) {
        this.maze = maze;
        this.formManager = formManager;
        this.strategyFactory = strategyFactory;
        this.logger = logger;
        this.levelDetector = levelDetector;
        this.playerId = playerId;
        this.startPosition = startPosition;
    }

    // Getter (keine Setter - immutable!)

    public Maze getMaze() {
        return maze;
    }

    public FormManager getFormManager() {
        return formManager;
    }

    public INavigationStrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public Logger getLogger() {
        return logger;
    }

    public LevelDetector getLevelDetector() {
        return levelDetector;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Position getStartPosition() {
        return startPosition;
    }
}
