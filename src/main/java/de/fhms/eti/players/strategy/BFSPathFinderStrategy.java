package de.fhms.eti.players.strategy;

import de.fhms.eti.players.domain.*;

import java.util.*;

/**
 * BFSPathFinderStrategy - Pfadsuche auf Basis einer einfachen Breitensuche.
 * <p>
 * Verantwortung (SRP):
 * - Speichert alle bereits gesehenen Zellen
 * - Berechnet den kürzesten Weg zu einem Ziel oder zur nächsten Front (unerforschte Nachbarn)
 * <p>
 * Strategien nutzen ausschließlich das {@link IPathFinder}-Interface (DIP),
 * sodass die Pfadlogik sauber gekapselt ist.
 */
public class BFSPathFinderStrategy implements IPathFinder {
    private static final Direction[] DIRECTIONS = {Direction.north, Direction.east, Direction.south, Direction.west};

    private final Map<Position, Cell> knownCells = new HashMap<>();

    @Override
    public void updateKnowledge(Position currentPos, Cell northCell, Cell eastCell,
                                Cell southCell, Cell westCell) {
        if (currentPos == null) {
            return;
        }

        knownCells.putIfAbsent(currentPos, new FloorCell());
        storeNeighbor(currentPos.move(Direction.north), northCell);
        storeNeighbor(currentPos.move(Direction.east), eastCell);
        storeNeighbor(currentPos.move(Direction.south), southCell);
        storeNeighbor(currentPos.move(Direction.west), westCell);
    }

    @Override
    public Deque<GoAction> buildPath(Position start, Position target, int myPlayerId) {
        Deque<GoAction> empty = new ArrayDeque<>();
        if (start == null || target == null || !knownCells.containsKey(target)) {
            return empty;
        }

        MapData mapData = runBfs(start, myPlayerId, position -> position.equals(target));

        if (!mapData.cameFrom.containsKey(target)) {
            return empty;
        }

        return reconstructPath(start, target, mapData);
    }

    @Override
    public Deque<GoAction> buildPathToFrontier(Position start, int myPlayerId) {
        Deque<GoAction> empty = new ArrayDeque<>();
        if (start == null || !knownCells.containsKey(start)) {
            return empty;
        }

        MapData mapData = runBfs(start, myPlayerId, this::hasUnknownNeighbor);
        Position frontier = mapData.goal;
        if (frontier == null || frontier.equals(start)) {
            return empty;
        }

        return reconstructPath(start, frontier, mapData);
    }

    private void storeNeighbor(Position pos, Cell status) {
        if (pos == null || status == null) {
            return;
        }
        knownCells.put(pos, status);
    }

    private boolean isWalkable(Cell status, int myPlayerId) {
        if (status == null) {
            return false;
        }
        if (status instanceof WallCell) {
            return false;
        }
        if (status instanceof FinishCell) {
            return !INavigationStrategy.isOtherPlayersFinish(status, myPlayerId);
        }
        return true;
    }

    /**
     * Fuehrt eine Breitensuche durch, bis die Zielbedingung erfüllt ist.
     * Die Bedingung kann z.B. "erreiche dieses Ziel" oder "finde eine Zelle
     * mit unbekannten Nachbarn" bedeuten.
     */
    private MapData runBfs(Position start, int myPlayerId, java.util.function.Predicate<Position> goalCondition) {
        Queue<Position> queue = new LinkedList<>();
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Direction> moveUsed = new HashMap<>();
        Set<Position> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        Position goal = null;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (goalCondition.test(current)) {
                goal = current;
                break;
            }

            for (Direction dir : DIRECTIONS) {
                Position next = current.move(dir);
                if (visited.contains(next)) {
                    continue;
                }

                Cell status = knownCells.get(next);
                if (!isWalkable(status, myPlayerId)) {
                    continue;
                }

                visited.add(next);
                queue.offer(next);
                cameFrom.put(next, current);
                moveUsed.put(next, dir);
            }
        }

        return new MapData(goal, cameFrom, moveUsed);
    }

    private boolean hasUnknownNeighbor(Position pos) {
        for (Direction dir : DIRECTIONS) {
            Position neighbor = pos.move(dir);
            if (!knownCells.containsKey(neighbor)) {
                return true;
            }
        }
        return false;
    }

    private Deque<GoAction> reconstructPath(Position start, Position target, MapData data) {
        Deque<GoAction> path = new ArrayDeque<>();
        Position node = target;
        while (!node.equals(start)) {
            Direction dir = data.moveUsed.get(node);
            if (dir == null) {
                return new ArrayDeque<>();
            }
            path.addFirst(new GoAction(dir));
            node = data.cameFrom.get(node);
        }
        return path;
    }

    private static class MapData {
        final Position goal;
        final Map<Position, Position> cameFrom;
        final Map<Position, Direction> moveUsed;

        MapData(Position goal, Map<Position, Position> cameFrom, Map<Position, Direction> moveUsed) {
            this.goal = goal;
            this.cameFrom = cameFrom;
            this.moveUsed = moveUsed;
        }
    }
}
