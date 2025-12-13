package de.fhms.eti.players.domain;

import de.fhms.eti.players.strategy.INavigationStrategy;

/**
 * Navigator organisiert Position und Navigationsstrategie
 * <p>
 * Verantwortung (SRP):
 * - Verwaltet aktuelle Position
 * - Nutzt eine Strategie zur Entscheidungsfindung
 * - Aktualisiert Position nach Bewegung
 * - Markiert besuchte Positionen
 * <p>
 * Erfüllt DIP (Dependency Inversion):
 * - Hängt von INavigationStrategy ab (nicht von konkreter Implementierung)
 * - Strategie kann zur Laufzeit gewechselt werden
 */

public class Navigator {
    private final Maze maze;
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
     * <p>
     * gibt  die Entscheidung an die Strategie (Strategy Pattern). Del
     *
     * @param northCell Status nördliche Zelle
     * @param eastCell  Status östliche Zelle
     * @param southCell Status südliche Zelle
     * @param westCell  Status westliche Zelle
     * @return Bewegungsbefehl, z.B. "go north"
     */
    public GoAction decideNextMove(Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {
        // Frage die Strategie nach der Entscheidung
        return strategy.decideNextMove(currentPosition, northCell, eastCell, southCell, westCell);
    }

    /**
     * Aktualisiert die Position nach einer Bewegung
     * <p>
     * Muss nach jeder Bewegung aufgerufen werden!
     * - Berechnet neue Position
     * - Markiert neue Position als besucht
     */
    public void updatePosition(GoAction action) {
        currentPosition = currentPosition.move(action.getDirection());
        maze.markVisited(currentPosition);
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Gibt die aktuelle Strategie zurück
     * Wichtig für Strategy-Wechsel in MyBot
     *
     * @return Die aktuelle Navigations-Strategie
     */
    public INavigationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(INavigationStrategy newStrategy) {
        this.strategy = newStrategy;
    }
}
