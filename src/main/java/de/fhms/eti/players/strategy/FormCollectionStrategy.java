package de.fhms.eti.players.strategy;

import de.fhms.eti.players.domain.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * FormCollectionStrategy - SCHNELL & SMART
 * <p>
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
    private Deque<GoAction> currentPlan;
    private Position activeTarget;

    public FormCollectionStrategy(Maze maze, IFormManager formManager, IPathFinder pathFinder) {
        this.maze = maze;
        this.formManager = formManager;
        this.formPositions = new HashMap<>();
        this.pathFinder = pathFinder;
        this.fallbackStrategy = new SimpleNavigationStrategy(maze);
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
    public GoAction decideNextMove(Position currentPos, Cell northCell, Cell eastCell,
                                   Cell southCell, Cell westCell) {
        pathFinder.updateKnowledge(currentPos, northCell, eastCell, southCell, westCell);
        scanAndSave(currentPos, Direction.north, northCell);
        scanAndSave(currentPos, Direction.east, eastCell);
        scanAndSave(currentPos, Direction.south, southCell);
        scanAndSave(currentPos, Direction.west, westCell);

        Position target = determineTarget();
        if (target != null) {
            refreshPlan(currentPos, target);
            GoAction planned = pollPlannedMove(northCell, eastCell, southCell, westCell);
            if (planned != null)
                return planned;
        } else {
            currentPlan.clear();
            activeTarget = null;
            GoAction frontierMove = tryFrontierPlan(currentPos, northCell, eastCell, southCell, westCell);
            if (frontierMove != null)
                return frontierMove;
        }

        GoAction exploration = explore(currentPos, northCell, eastCell, southCell, westCell);
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

    private GoAction pollPlannedMove(Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {
        while (currentPlan != null && !currentPlan.isEmpty()) {
            GoAction move = currentPlan.peekFirst();
            if (isMoveCurrentlyPossible(move, northCell, eastCell, southCell, westCell)) {
                currentPlan.pollFirst();
                return move;
            }
            currentPlan.clear();
        }
        return null;
    }

    private GoAction tryFrontierPlan(Position currentPos, Cell northCell, Cell eastCell,
                                     Cell southCell, Cell westCell) {
        Deque<GoAction> frontierPlan = pathFinder.buildPathToFrontier(currentPos, myPlayerId);
        if (frontierPlan == null || frontierPlan.isEmpty()) {
            return null;
        }
        currentPlan = frontierPlan;
        activeTarget = null;
        return pollPlannedMove(northCell, eastCell, southCell, westCell);
    }

    private boolean isMoveCurrentlyPossible(GoAction action, Cell northCell, Cell eastCell,
                                            Cell southCell, Cell westCell) {
        Cell cellStatus = getCellInDirection(action.getDirection(), northCell, eastCell, southCell, westCell);
        // Prüfe ob Feld begehbar ist
        return canMove(cellStatus);
    }

    /**
     * Speichert sichtbare Formulare/Finish.
     */
    private void scanAndSave(Position currentPos, Direction direction, Cell cellStatus) {
        if (cellStatus == null) {
            return;
        }

        Position neighborPos = calculatePos(currentPos, direction);

        if (cellStatus instanceof FinishCell finishCell && finishPosition == null) {
            int foreignPlayerId = finishCell.getPlayerId();
            if (myPlayerId == foreignPlayerId) {
                finishPosition = neighborPos;
            }
        }

        if (cellStatus instanceof FormCell formCell) {
            int formId = formCell.getFormId();
            if (/*formId == myPlayerId &&*/ formCell.getPlayerId() == myPlayerId /*&& !formPositions.containsKey(formCell.getFormId())*/) {
                formPositions.put(formId, neighborPos);
            }
        }
    }

    private Position calculatePos(Position pos, Direction dir) {
        return switch (dir) {
            case Direction.north -> new Position(pos.x(), pos.y() - 1);
            case Direction.east -> new Position(pos.x() + 1, pos.y());
            case Direction.south -> new Position(pos.x(), pos.y() + 1);
            case Direction.west -> new Position(pos.x() - 1, pos.y());
        };
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

    private GoAction explore(Position curr, Cell north, Cell east, Cell south, Cell west) {
        Direction[] dirs = {Direction.north, Direction.east, Direction.south, Direction.west};
        Cell[] cells = {north, east, south, west};

        Direction bestDirection = null;
        int minVisits = Integer.MAX_VALUE;

        for (int i = 0; i < 4; i++) {
            if (canMove(cells[i])) {
                Position next = curr.move(dirs[i]);
                int visits = maze.getVisitCount(next);
                if (visits < minVisits) {
                    minVisits = visits;
                    bestDirection = dirs[i];
                }
            }
        } // ← Schleife ENDET HIER!

        // NACH der Schleife:
        if (bestDirection != null) {
            return new GoAction(bestDirection);
        }

        // Fallback wenn alle Richtungen blockiert
        return fallbackStrategy.decideNextMove(curr, north, east, south, west);
    }

    private boolean canMove(Cell cell) {
        if (cell instanceof WallCell) {
            return false;
        }
        if (cell instanceof FormCell) {
            return true;
        }
        return !INavigationStrategy.isOtherPlayersFinish(cell, myPlayerId);
    }

    private Cell getCellInDirection(Direction direction, Cell north, Cell east, Cell south, Cell west) {
        return switch (direction) {
            case Direction.north -> north;
            case Direction.east -> east;
            case Direction.south -> south;
            case Direction.west -> west;
        };
    }

    public void setTargetPosition(Position position) {
        this.finishPosition = position;
        this.activeTarget = null;
        this.currentPlan.clear();
    }
}
