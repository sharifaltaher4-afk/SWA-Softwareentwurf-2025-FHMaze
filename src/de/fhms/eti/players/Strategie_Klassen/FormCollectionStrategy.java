package de.fhms.eti.players.Strategie_Klassen;

import java.util.ArrayDeque;
import java.util.Deque;
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
    private final Maze maze;
    private final IFormManager formManager;
    private final Map<Integer, Position> formPositions;
    private final IPathFinder pathFinder;
    private final INavigationStrategy fallbackStrategy;

    private int myPlayerId;
    private Position finishPosition;
    private Deque<String> currentPlan;
    private Position activeTarget;

    public FormCollectionStrategy(Maze maze, IFormManager formManager, IPathFinder pathFinder) {
        this.maze = maze;
        this.formManager = formManager;
        this.formPositions = new HashMap<>();
        this.pathFinder = pathFinder;
        this.fallbackStrategy = new RightHandRuleStrategy();
        this.currentPlan = new ArrayDeque<>();
        this.activeTarget = null;
        this.myPlayerId = -1;
        this.finishPosition = null;
    }

    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
        fallbackStrategy.setPlayerId(playerId);
    }

    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell,
                                 String southCell, String westCell) {
        pathFinder.updateKnowledge(currentPos, northCell, eastCell, southCell, westCell);
        scanAndSave(currentPos, "north", northCell);
        scanAndSave(currentPos, "east", eastCell);
        scanAndSave(currentPos, "south", southCell);
        scanAndSave(currentPos, "west", westCell);

        Position target = determineTarget();
        if (target != null) {
            refreshPlan(currentPos, target);
            String planned = pollPlannedMove(northCell, eastCell, southCell, westCell);
            if (planned != null) {
                return planned;
            }
        } else {
            currentPlan.clear();
            activeTarget = null;
            String frontierMove = tryFrontierPlan(currentPos, northCell, eastCell, southCell, westCell);
            if (frontierMove != null) {
                return frontierMove;
            }
        }

        String exploration = explore(currentPos, northCell, eastCell, southCell, westCell);
        if (exploration != null) {
            return exploration;
        }
        return fallbackStrategy.decideNextMove(currentPos, northCell, eastCell, southCell, westCell);
    }

    private void refreshPlan(Position currentPos, Position target) {
        if (!target.equals(activeTarget) || currentPlan.isEmpty()) {
            currentPlan = pathFinder.buildPath(currentPos, target, myPlayerId);
            activeTarget = target;
        }
    }

    private String pollPlannedMove(String northCell, String eastCell, String southCell, String westCell) {
        while (currentPlan != null && !currentPlan.isEmpty()) {
            String move = currentPlan.peekFirst();
            if (isMoveCurrentlyPossible(move, northCell, eastCell, southCell, westCell)) {
                currentPlan.pollFirst();
                return move;
            }
            currentPlan.clear();
        }
        return null;
    }

    private String tryFrontierPlan(Position currentPos, String northCell, String eastCell,
                                   String southCell, String westCell) {
        Deque<String> frontierPlan = pathFinder.buildPathToFrontier(currentPos, myPlayerId);
        if (frontierPlan == null || frontierPlan.isEmpty()) {
            return null;
        }
        currentPlan = frontierPlan;
        activeTarget = null;
        return pollPlannedMove(northCell, eastCell, southCell, westCell);
    }

    private boolean isMoveCurrentlyPossible(String move, String northCell, String eastCell,
                                            String southCell, String westCell) {
        if (move == null) {
            return false;
        }
        String direction = move.replace("go ", "");
        String cellStatus = getCellInDirection(direction, northCell, eastCell, southCell, westCell);
        return canMove(cellStatus);
    }

    /**
     * Speichert sichtbare Formulare/Finish.
     */
    private void scanAndSave(Position currentPos, String direction, String cellStatus) {
        if (cellStatus == null) {
            return;
        }

        Position neighborPos = calculatePos(currentPos, direction);

        if (cellStatus.startsWith("FINISH") && finishPosition == null) {
            try {
                String[] parts = cellStatus.split(" ");
                if (parts.length >= 2) {
                    int ownerId = Integer.parseInt(parts[1]);
                    if (ownerId == myPlayerId) {
                        finishPosition = neighborPos;
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (cellStatus.startsWith("FORM")) {
            try {
                String[] parts = cellStatus.split(" ");
                if (parts.length >= 3) {
                    int ownerId = Integer.parseInt(parts[1]);
                    int formId = Integer.parseInt(parts[2]);
                    if (ownerId == myPlayerId && !formPositions.containsKey(formId)) {
                        formPositions.put(formId, neighborPos);
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private Position calculatePos(Position pos, String dir) {
        switch (dir) {
            case "north": return new Position(pos.getX(), pos.getY() - 1);
            case "east":  return new Position(pos.getX() + 1, pos.getY());
            case "south": return new Position(pos.getX(), pos.getY() + 1);
            case "west":  return new Position(pos.getX() - 1, pos.getY());
            default: return pos;
        }
    }

    private Position determineTarget() {
        int nextFormId = formManager.getNextFormId();
        if (formPositions.containsKey(nextFormId)) {
            return formPositions.get(nextFormId);
        }

        if (formManager.hasAllForms() && finishPosition != null) {
            return finishPosition;
        }

        return null;
    }

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

        return best;
    }

    private boolean canMove(String cell) {
        if (cell == null || "WALL".equals(cell)) {
            return false;
        }
        if (cell.startsWith("FORM")) {
            return true;
        }
        return !INavigationStrategy.isOtherPlayersFinish(cell, myPlayerId);
    }

    private String getCellInDirection(String direction, String north, String east, String south, String west) {
        switch (direction) {
            case "north": return north;
            case "east": return east;
            case "south": return south;
            case "west": return west;
            default: return null;
        }
    }

    public void setTargetPosition(Position position) {
        this.finishPosition = position;
        this.activeTarget = null;
        this.currentPlan.clear();
    }
}
