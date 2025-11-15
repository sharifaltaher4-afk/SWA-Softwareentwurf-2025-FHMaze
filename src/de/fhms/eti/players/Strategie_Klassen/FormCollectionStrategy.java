package de.fhms.eti.players.Strategie_Klassen;
import java.util.HashMap;
import java.util.Map;

import de.fhms.eti.players.Domain_Klassen.IFormManager;
import de.fhms.eti.players.Domain_Klassen.Maze;
import de.fhms.eti.players.Domain_Klassen.Position;

/**
 * FormCollectionStrategy - SCHNELL & SMART
 * 
 * OPTIMIERUNGEN:
 * 1. Minimale Berechnungen (< 50ms)
 * 2. Speichert Formulare SOFORT beim ersten Sehen
 * 3. Geht DIREKT zu bekannten Formularen
 * 4. Keine komplexen Listen/Sortierungen
 */
public class FormCollectionStrategy implements INavigationStrategy {
    private Maze maze;
    private int myPlayerId;
    private IFormManager formManager;
    private Map<Integer, Position> formPositions;
    private Position finishPosition;

    public FormCollectionStrategy(Maze maze, IFormManager formManager) {
        this.maze = maze;
        this.formManager = formManager;
        this.formPositions = new HashMap<>();
        this.myPlayerId = -1;
        this.finishPosition = null;
        System.err.println("[OK] FormCollectionStrategy (SCHNELL) initialisiert");
    }

    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
    }

    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, 
                                String southCell, String westCell) {
        System.err.println("[DEBUG] FormCollectionStrategy.decideNextMove aufgerufen bei " + currentPos);
        // Speichere sichtbare Formulare & FINISH
        scanAndSave(currentPos, "north", northCell);
        scanAndSave(currentPos, "east", eastCell);
        scanAndSave(currentPos, "south", southCell);
        scanAndSave(currentPos, "west", westCell);
        
        // Bestimme Ziel
        Position target = determineTarget();
        
        // Navigiere
        if (target != null) {
            String move = navigateToTarget(currentPos, target, northCell, eastCell, southCell, westCell);
            if (move != null) return move;
        }
        
        // Exploration
        return explore(currentPos, northCell, eastCell, southCell, westCell);
    }

    /**
     * Scannt und speichert Formular/FINISH sofort
     */
    private void scanAndSave(Position currentPos, String direction, String cellStatus) {
        if (cellStatus == null) return;
        
        Position neighborPos = calculatePos(currentPos, direction);
        
        // FINISH speichern
        if (cellStatus.startsWith("FINISH") && finishPosition == null) {
            try {
                String[] parts = cellStatus.split(" ");
                if (parts.length >= 2) {
                    int fpid = Integer.parseInt(parts[1]);
                    if (fpid == myPlayerId) {
                        finishPosition = neighborPos;
                        System.err.println("[INFO] FINISH bei " + neighborPos);
                    }
                }
            } catch (Exception e) {}
        }
        
        // FORMULAR speichern
        if (cellStatus.startsWith("FORM")) {
            try {
                String[] parts = cellStatus.split(" ");
                if (parts.length >= 3) {
                    int fpid = Integer.parseInt(parts[1]);
                    int fid = Integer.parseInt(parts[2]);
                    if (fpid == myPlayerId && !formPositions.containsKey(fid)) {
                        formPositions.put(fid, neighborPos);
                        System.err.println("[DEBUG] FORM " + fid + " bei " + neighborPos);
                    }
                }
            } catch (Exception e) {}
        }
    }

    /**
     * Berechnet Nachbar-Position
     */
    private Position calculatePos(Position pos, String dir) {
        switch (dir) {
            case "north": return new Position(pos.getX(), pos.getY() - 1);
            case "east":  return new Position(pos.getX() + 1, pos.getY());
            case "south": return new Position(pos.getX(), pos.getY() + 1);
            case "west":  return new Position(pos.getX() - 1, pos.getY());
            default: return pos;
        }
    }

    /**
     * Bestimmt Ziel
     */
    private Position determineTarget() {
        int nextFormId = formManager.getNextFormId();
        
        // PRIO 1: Nächstes Formular
        if (formPositions.containsKey(nextFormId)) {
            System.err.println("[INFO] Ziel: FORM " + nextFormId);
            return formPositions.get(nextFormId);
        }
        
        // PRIO 2: Zum FINISH wenn alle Formulare da
        if (formManager.hasAllForms() && finishPosition != null) {
            System.err.println("[INFO] Zum FINISH");
            return finishPosition;
        }
        
        return null;
    }

    /**
     * Navigiert zum Ziel (einfache Manhattan-Strategie)
     */
    private String navigateToTarget(Position curr, Position target, 
                                    String north, String east, String south, String west) {
        int dx = target.getX() - curr.getX();
        int dy = target.getY() - curr.getY();
        
        // Priorisiere größere Distanz
        if (Math.abs(dx) >= Math.abs(dy)) {
            // X-Richtung wichtiger
            if (dx > 0 && canMove(east)) return "go east";
            if (dx < 0 && canMove(west)) return "go west";
            if (dy > 0 && canMove(south)) return "go south";
            if (dy < 0 && canMove(north)) return "go north";
        } else {
            // Y-Richtung wichtiger
            if (dy > 0 && canMove(south)) return "go south";
            if (dy < 0 && canMove(north)) return "go north";
            if (dx > 0 && canMove(east)) return "go east";
            if (dx < 0 && canMove(west)) return "go west";
        }
        
        // Versuch andere Richtungen
        if (dx > 0 && canMove(east)) return "go east";
        if (dx < 0 && canMove(west)) return "go west";
        if (dy > 0 && canMove(south)) return "go south";
        if (dy < 0 && canMove(north)) return "go north";
        
        return null;
    }

    /**
     * Exploration - wähle am wenigsten besuchte Richtung
     */
    private String explore(Position curr, String north, String east, String south, String west) {
        String[] dirs = {"north", "east", "south", "west"};
        String[] cells = {north, east, south, west};
        
        String best = null;
        int minVisits = Integer.MAX_VALUE;
        
        for (int i = 0; i < 4; i++) {
            if (canMove(cells[i])) {
                Position next = curr.move(dirs[i]);
                int visits = maze.getVisitCount(next);
                if (visits < minVisits) {
                    minVisits = visits;
                    best = "go " + dirs[i];
                }
            }
        }
        
        return best != null ? best : "go north";
    }

    /**
     * Prüft ob Bewegung möglich
     */
    private boolean canMove(String cell) {
        if (cell == null || cell.equals("WALL")) return false;
        if (cell.startsWith("FORM")) return true;
        return !INavigationStrategy.isOtherPlayersFinish(cell, myPlayerId);
    }

    public void setTargetPosition(Position position) {
        // Für Kompatibilität
    }
}