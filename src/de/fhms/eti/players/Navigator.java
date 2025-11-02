package de.fhms.eti.players;

/**
 * Navigator organisiert Position und Navigationsstrategie
 * 
 * Verantwortung (SRP):
 * - Verwaltet aktuelle Position
 * - Nutzt eine Strategie zur Entscheidungsfindung
 * - Aktualisiert Position nach Bewegung
 * - Markiert besuchte Positionen
 * 
 * Erfüllt DIP (Dependency Inversion):
 * - Hängt von INavigationStrategy ab (nicht von konkreter Implementierung)
 * - Strategie kann zur Laufzeit gewechselt werden
 */

public class Navigator {
    private Maze maze;
    private Position currentPosition;
    private INavigationStrategy strategy;
    
    public Navigator(Maze maze, Position startPosition, INavigationStrategy strategy) {
        this.maze = maze;
        this.currentPosition = startPosition;
        this.strategy = strategy;
        maze.markVisited(startPosition);// Markiere Startposition als besucht
    }
    
    /**
     * entscheidet die nächste Bewegung
     * 
     * gibt  die Entscheidung an die Strategie (Strategy Pattern). Del
     * 
     * @param northCell Status nördliche Zelle
     * @param eastCell Status östliche Zelle
     * @param southCell Status südliche Zelle
     * @param westCell Status westliche Zelle
     * @return Bewegungsbefehl, z.B. "go north"
     */
    public String decideNextMove(String northCell, String eastCell, String southCell, String westCell) {
        // Frage die Strategie nach der Entscheidung
        return strategy.decideNextMove(currentPosition, northCell, eastCell, southCell, westCell);
    }
    
     /**
     * Aktualisiert die Position nach einer Bewegung
     * 
     * Muss nach jeder Bewegung aufgerufen werden!
     * - Berechnet neue Position
     * - Markiert neue Position als besucht
     */
    public void updatePosition(String action) {
        String direction = action.replace("go ", "");
        currentPosition = currentPosition.move(direction);
        maze.markVisited(currentPosition);
    }
    
    public Position getCurrentPosition() {
        return currentPosition;
    }
    
    public void setStrategy(INavigationStrategy newStrategy) {
        this.strategy = newStrategy;
    }
}