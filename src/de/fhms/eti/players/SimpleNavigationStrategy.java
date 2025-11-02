package de.fhms.eti.players;

/**
 * SimpleNavigationStrategy ist Eine einfache Navigationsstrategie
 * Implementiert INavigationStrategy (LSP - Liskov Substitution Principle)
 * 
 * Algorithmus:
 * 1. Finde alle freien (nicht-Wand) Richtungen
 * 2. Wähle die Richtung mit den wenigsten Besuchen
 * 3. Bevorzugt: unbesuchte Felder (0 Besuche)
 * 4. Falls alle besucht: Wähle am wenigsten besuchtes (Backtracking)
 * 
 * Reihenfolge bei gleicher Besuchszahl: West dann Nord dann Ost dann Süd
 */

public class SimpleNavigationStrategy implements INavigationStrategy {
    private Maze maze; // Referenz zum Labyrinth-Gedächtnis
    
    /**
     * Konstruktor
     * @param maze Das Labyrinth-Gedächtnis (für getVisitCount)
     */
    public SimpleNavigationStrategy(Maze maze) {
        this.maze = maze;
    }
    
    /**
     * entscheidet die nächste Bewegung
     * 
     * Implementiert INavigationStrategy.decideNextMove()
     * 
     * @param currentPos Aktuelle Position
     * @param northCell Status der nördlichen Zelle
     * @param eastCell Status der östlichen Zelle
     * @param southCell Status der südlichen Zelle
     * @param westCell Status der westlichen Zelle
     * @return Befehl für nächste Bewegung, z.B "go west"
     */
    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, 
                                String southCell, String westCell) {
        
        String[] directions = {"west", "north", "east", "south"};
        String[] cells = {westCell, northCell, eastCell, southCell};
        
        String bestDirection = null;
        int minVisits = Integer.MAX_VALUE; // Minimale Besuchszahl (initial: sehr groß)
        
        // Finde die Richtung mit den wenigsten Besuchen
        // Durchlaufe alle 4 Richtungen
        for (int i = 0; i < 4; i++) {
            if (!cells[i].equals("WALL")) {//// Ist diese Richtung frei (keine Wand)?
                // Berechne die Position in dieser Richtung
                Position nextPos = currentPos.move(directions[i]);
                // Wie oft wurde diese Position bereits besucht?
                int visits = maze.getVisitCount(nextPos);
                
                // Bevorzuge weniger besuchte Felder
                if (visits < minVisits) {// Ist diese Position weniger besucht als bisherige beste?
                    minVisits = visits;// Neue minimale Besuchszahl
                    bestDirection = "go " + directions[i];// Neue beste Richtung
                }
            }
        }
        // Gib beste gefundene Richtung zurück
        // (null sollte nicht vorkommen, da immer mindestens eine Richtung frei ist)
        return bestDirection;
    }
}