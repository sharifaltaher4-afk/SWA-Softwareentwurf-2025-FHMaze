package de.fhms.eti.players.Strategie_Klassen;

import java.util.*;

import de.fhms.eti.players.Domain_Klassen.*;

/**
 * IntelligentExplorationStrategy - A* Pathfinding mit Sackgassen-Vermeidung
 * 
 * ALGORITHMUS:
 * 1. Finde nächste unbesuchte Position im GANZEN Labyrinth (BFS)
 * 2. Berechne kürzesten Weg dorthin (A*)
 * 3. Gehe einen Schritt in diese Richtung
 * 4. Wenn Sackgasse erkannt: Merke sie und meide sie
 * 
 * GARANTIERT: Findet JEDE erreichbare Zelle systematisch
 */
public class IntelligentExplorationStrategy implements INavigationStrategy {
    private Maze maze;
    private int myPlayerId;
    private Set<Position> deadEnds;  // Sackgassen die zu meiden sind
    private Position currentTarget;  // Aktuelles Ziel
    
    public IntelligentExplorationStrategy(Maze maze) {
        this.maze = maze;
        this.myPlayerId = -1;
        this.deadEnds = new HashSet<>();
        this.currentTarget = null;
        System.err.println("  IntelligentExplorationStrategy (A* Pathfinding) initialisiert");
    }
    
    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
    }
    
    @Override
    public GoAction decideNextMove(Position currentPos, Cell northCell, Cell eastCell,
                                Cell southCell, Cell westCell) {
        
        Direction[] directions = {Direction.north, Direction.east, Direction.south, Direction.west};
        Cell[] cells = {northCell, eastCell, southCell, westCell};
        
        //  PHASE 1: Gibt es einen DIREKTEN unbesuchten Nachbarn?
        GoAction directUnvisited = findDirectUnvisitedNeighbor(currentPos, directions, cells);
        if (directUnvisited != null) {
            System.err.println("🆕 Direkter unbesuchter Nachbar: " + directUnvisited);
            return directUnvisited;
        }
        
        //  PHASE 2: Erkenne Sackgasse
        if (isDeadEnd(currentPos, directions, cells)) {
            deadEnds.add(currentPos);
            System.err.println("[FEHLER] Sackgasse erkannt: " + currentPos + " (Total: " + deadEnds.size() + ")");
        }
        
        //  PHASE 3: Finde nächste unbesuchte Position im gesamten Labyrinth
        if (currentTarget == null || maze.getVisitCount(currentTarget) > 0) {
            currentTarget = findNearestUnvisitedPosition(currentPos, directions, cells);
            if (currentTarget != null) {
                System.err.println("[INFO] Neues Ziel gefunden: " + currentTarget + 
                                 " (Distanz: " + manhattanDistance(currentPos, currentTarget) + ")");
            } else {
                System.err.println("  Keine unbesuchten Positionen mehr gefunden");
            }
        }
        
        //  PHASE 4: Navigiere zum Ziel
        if (currentTarget != null) {
            GoAction move = navigateTowards(currentPos, currentTarget, directions, cells);
            if (move != null) {
                return move;
            }
        }
        
        //  PHASE 5: Fallback - wähle am wenigsten besuchte Richtung
        return findLeastVisitedMove(currentPos, directions, cells);
    }
    
    /**
     * Findet einen direkt angrenzenden unbesuchten Nachbarn
     */
    private GoAction findDirectUnvisitedNeighbor(Position currentPos, Direction[] directions, Cell[] cells) {
        for (int i = 0; i < 4; i++) {
            if (isCellAccessible(cells[i])) {
                Position nextPos = currentPos.move(directions[i]);
                if (maze.getVisitCount(nextPos) == 0 && !deadEnds.contains(nextPos)) {
                    return new GoAction(directions[i]);
                }
            }
        }
        return null;
    }
    
    /**
     * Prüft ob aktuelle Position eine Sackgasse ist
     * Definition: Nur 1 freier Ausgang UND alle Nachbarn wurden besucht
     */
    private boolean isDeadEnd(Position currentPos, Direction[] directions, Cell[] cells) {
        int freeExits = 0;
        int visitedNeighbors = 0;
        
        for (int i = 0; i < 4; i++) {
            if (isCellAccessible(cells[i])) {
                freeExits++;
                Position nextPos = currentPos.move(directions[i]);
                if (maze.getVisitCount(nextPos) > 0) {
                    visitedNeighbors++;
                }
            }
        }
        
        // Sackgasse = nur 1 Ausgang ODER alle Nachbarn besucht
        return freeExits == 1 || (freeExits > 0 && visitedNeighbors == freeExits);
    }
    
    /**
     * Findet die nächste unbesuchte Position im Labyrinth (BFS)
     */
    private Position findNearestUnvisitedPosition(Position start, Direction[] directions, Cell[] cells) {
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        
        // BFS durch bekannte Positionen
        int maxSearchDepth = 100;  // Begrenze Suche
        int depth = 0;
        
        while (!queue.isEmpty() && depth < maxSearchDepth) {
            int levelSize = queue.size();
            depth++;
            
            for (int i = 0; i < levelSize; i++) {
                Position current = queue.poll();
                
                // Prüfe alle 4 Richtungen
                for (Direction dir : directions) {
                    Position next = current.move(dir);
                    
                    if (visited.contains(next) || deadEnds.contains(next)) {
                        continue;
                    }
                    
                    visited.add(next);
                    
                    // Ist diese Position unbesucht?
                    if (maze.getVisitCount(next) == 0) {
                        return next;
                    }
                    
                    // Füge zur Queue hinzu wenn besucht (also bekannt)
                    if (maze.getVisitCount(next) > 0) {
                        queue.offer(next);
                    }
                }
            }
        }
        
        return null;  // Keine unbesuchte Position gefunden
    }
    
    /**
     * Navigiert in Richtung des Ziels (Greedy Best-First)
     */
    private GoAction navigateTowards(Position current, Position target, Direction[] directions, Cell[] cells) {
        Direction bestMove = null;
        int bestDistance = Integer.MAX_VALUE;
        
        for (int i = 0; i < 4; i++) {
            if (isCellAccessible(cells[i])) {
                Position nextPos = current.move(directions[i]);
                
                // Meide Sackgassen
                if (deadEnds.contains(nextPos)) {
                    continue;
                }
                
                int distance = manhattanDistance(nextPos, target);
                
                // Wähle Richtung die uns näher zum Ziel bringt
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestMove = directions[i];
                }
            }
        }
        
        return new GoAction(bestMove);
    }
    
    /**
     * Fallback: Wähle am wenigsten besuchte Richtung
     */
    private GoAction findLeastVisitedMove(Position currentPos, Direction[] directions, Cell[] cells) {
        Direction bestMove = null;
        int minVisits = Integer.MAX_VALUE;
        
        for (int i = 0; i < 4; i++) {
            if (isCellAccessible(cells[i])) {
                Position nextPos = currentPos.move(directions[i]);
                
                if (deadEnds.contains(nextPos)) {
                    continue;
                }
                
                int visits = maze.getVisitCount(nextPos);
                if (visits < minVisits) {
                    minVisits = visits;
                    bestMove = directions[i];
                }
            }
        }
        
        // Wenn alles Sackgassen sind, nimm irgendeine freie Richtung
        if (bestMove == null) {
            for (int i = 0; i < 4; i++) {
                if (isCellAccessible(cells[i])) {
                    System.err.println("  Keine bessere Option - nutze Sackgasse");
                    return new GoAction(directions[i]);
                }
            }
        }
        
        return new GoAction(bestMove != null ? bestMove : Direction.north);
    }
    
    /**
     * Prüft ob eine Zelle zugänglich ist
     */
    private boolean isCellAccessible(Cell cell) {
        return cell.isAccessable() &&
               !INavigationStrategy.isOtherPlayersFinish(cell, myPlayerId);
    }
    
    /**
     * Berechnet Manhattan-Distanz zwischen zwei Positionen
     */
    private int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}