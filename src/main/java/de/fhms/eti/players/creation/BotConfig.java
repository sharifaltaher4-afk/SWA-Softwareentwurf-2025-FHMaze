package de.fhms.eti.players.creation;

import de.fhms.eti.players.communication.Logger;
import de.fhms.eti.players.domain.*;

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
public record BotConfig(Maze maze, FormManager formManager, ISheetManager sheetManager,
                        INavigationStrategyFactory strategyFactory, Logger logger,
                        LevelDetector levelDetector, int playerId, Position startPosition) {
    /**
     * Public Konstruktor - durch Builder aufrufbar
     */
    public BotConfig {
    }
}
