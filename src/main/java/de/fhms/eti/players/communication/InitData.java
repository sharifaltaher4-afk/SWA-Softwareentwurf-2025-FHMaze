package de.fhms.eti.players.communication;

import de.fhms.eti.players.domain.Position;

/**
 * Data Transfer Object (DTO) für Initialisierungsdaten
 * <p>
 * Speichert alle Informationen, die der Bot beim Start erhält:
 * - Labyrinthgröße
 * - Level
 * - Spieler-ID
 * - Startposition
 * <p>
 * Vorteile:
 * - Kapselt zusammengehörige daten
 * - Einfache Übergabe zwischen Methoden
 * - unveränderlich (final) - keine versehentlichen änderungen
 */
public class InitData {
    public final int sizeX, sizeY, level;
    public final int playerId;
    public final Position startPosition;
    public final int sheetCount;

    /**
     * Konstruktor: Erstellt InitData aus den eingelesenen Werten
     *
     * @param sizeX    Breite des Labyrinths
     * @param sizeY    Höhe des Labyrinths
     * @param level    Level des Spiels
     * @param playerId ID des Spielers
     * @param startX   X-Koordinate der Startposition
     * @param startY   Y-Koordinate der Startposition
     */
    public InitData(int sizeX, int sizeY, int level, int playerId, int startX, int startY) {
        this(sizeX, sizeY, level, playerId, startX, startY, 0);
    }

    /**
     * Konstruktor: Erstellt InitData aus den eingelesenen Werten (Level 5)
     *
     * @param sheetCount Anzahl der anfaenglich verfuegbaren Blaetter
     */
    public InitData(int sizeX, int sizeY, int level, int playerId, int startX, int startY, int sheetCount) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.level = level;
        this.playerId = playerId;
        this.startPosition = new Position(startX, startY);
        this.sheetCount = sheetCount;
    }
}
