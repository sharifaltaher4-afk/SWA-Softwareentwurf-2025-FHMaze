package de.fhms.eti.players;

import java.util.HashMap;

/**
 * Maze stellt das Wissen vom Bots über das Labyrinth dar
 * 
 * Verantwortung (SRP):
 * - Speichert Labyrinth-Metadaten (Größe, Level)
 * - Verwaltet besuchte Positionen mit Besuchszähler
 * 
 * Implementiert IMazeMemory (DIP Dependency Inversion Principle)
 */
public class Maze implements IMazeMemory {
    private int sizeX;
    private int sizeY;
    private int level;
    
    // Gedächtnis: HashMap speichert Position ==> Anzahl der besuche
    // Wichtig: HashMap statt HashSet, um besuche zu zählen
    private HashMap<Position, Integer> visitCount;
    
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
     * Gibt zurück, wie oft eine Position besucht wurde
     * 
     * Implementiert IMazeMemory.getVisitCount()
     * 
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
    
    public int getSizeX() {
        return sizeX;
    }
    
    public int getSizeY() {
        return sizeY;
    }
    
    public int getLevel() {
        return level;
    }
}