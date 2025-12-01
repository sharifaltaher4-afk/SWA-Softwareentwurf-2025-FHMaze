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
public record BotConfig(Maze maze, FormManager formManager, INavigationStrategyFactory strategyFactory, Logger logger,
                        LevelDetector levelDetector, int playerId, Position startPosition) {
    /**
     * Public Konstruktor - durch Builder aufrufbar
     */
    public BotConfig {
    }
}
