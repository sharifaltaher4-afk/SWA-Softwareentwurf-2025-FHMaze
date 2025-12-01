package main.java.de.fhms.eti.players.Domain_Klassen;

import java.util.HashMap;


/**
 * Maze stellt das Wissen vom Bots über das Labyrinth dar
 * <p>
 * Verantwortung (SRP):
 * - Speichert Labyrinth-Metadaten (GröÃŸe, Level)
 * - Verwaltet besuchte Positionen mit Besuchszähler
 * <p>
 * Implementiert IMazeMemory (DIP Dependency Inversion Principle)
 */
public class Maze implements IMazeMemory {
    private final int sizeX;
    private final int sizeY;
    private final int level;

    // Gedächtnis: HashMap speichert Position ==> Anzahl der besuche
    // Wichtig: HashMap statt HashSet, um besuche zu zählen
    private final HashMap<Position, Integer> visitCount;

    /**
     * Konstruktor: Erstellt ein neues Labyrinth-Gedächtnis
     *
     * @param sizeX Breite des Labyrinths
     * @param sizeY Höhe des Labyrinths
     * @param level Level des Spiels
     */
    public Maze(int sizeX, int sizeY, int level) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.level = level;
        this.visitCount = new HashMap<>();
    }

    /**
     * Markiert eine Posi. als besucht (erhöht den Zähler)
     *
     * @param pos Die besuchte Position
     */
    @Override
    public void markVisited(Position pos) {
        // getOrDefault: Wenn Position noch nicht in Map ==> 0, sonst aktueller Wert
        // Dann +1 und zurück in Map speichern
        visitCount.put(pos, visitCount.getOrDefault(pos, 0) + 1);
    }

    @Override
    public boolean wasVisited(Position pos) {
        return visitCount.containsKey(pos);
    }

    // Wie oft wurde diese Position besucht?

    /**
     * Gibt zurÃück, wie oft eine Position besucht wurde
     * <p>
     * Implementiert IMazeMemory.getVisitCount()
     * <p>
     * Wichtig für intelligentes Backtracking:
     * - Feld mit 0 Besuchen ==> Bevorzugen!
     * - Feld mit 1 Besuch ==> Ok
     * - Feld mit 5 Besuchen ==> Nur wenn nichts anderes geht
     *
     * @param pos Die zu prüfende Position
     * @return Anzahl der Besuche (0 wenn nie besucht)
     */
    public int getVisitCount(Position pos) {
        return visitCount.getOrDefault(pos, 0);
    }

    /**
     * Die Breite des Labyrinths.
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Die Höhe des Labyrinths.
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Die Level-ID des Spiels.
     */
    public int getLevel() {
        return level;
    }
}
