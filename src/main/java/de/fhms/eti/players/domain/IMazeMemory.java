package main.java.de.fhms.eti.players.domain;

/**
 * Interface für Labyrinthen-Gedächtnis(ISP Interface Segregation Principle)
 * <p>
 * Definiert nur die Methoden, die für das Speichern und Aufrufen von besuchten Posi. notwendig sind.
 * <p>
 * Vorteil: Klassen die diese Interface nutzen hängen nicht von allen Funktioenen der Maze-Klasse ab
 * (z.B sizeX, sizeY nicht notwendig )
 */
public interface IMazeMemory {
    /**
     * Markiert eine Pos. als besucht
     *
     * @param pos Die zu markierende Position
     */
    void markVisited(Position pos);

    /**
     * Prüft ob eine Pos. bereits besucht wurde
     *
     * @param pos Die zu prüfende Position
     * @return true wenn besucht sonst false
     */
    boolean wasVisited(Position pos);

    /**
     * Gibt zurück, wie oft eine Position besucht wurde
     * Wichtig für Backtracking aus Sackgassen
     *
     * @param pos Die zu prüfende Position
     * @return Anzahl der Besuche (0 wenn nie besucht)
     */
    int getVisitCount(Position pos);
}
