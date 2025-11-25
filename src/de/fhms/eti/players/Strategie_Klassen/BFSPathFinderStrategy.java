package de.fhms.eti.players.Strategie_Klassen;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.fhms.eti.players.Domain_Klassen.Position;

/**
 * BFSPathFinderStrategy - Pfadsuche auf Basis einer einfachen Breitensuche.
 *
 * Verantwortung (SRP):
 *  - Speichert alle bereits gesehenen Zellen
 *  - Berechnet den kürzesten Weg zu einem Ziel oder zur nächsten Front (unerforschte Nachbarn)
 *
 * Strategien nutzen ausschließlich das {@link IPathFinder}-Interface (DIP),
 * sodass die Pfadlogik sauber gekapselt ist.
 */
public class BFSPathFinderStrategy implements IPathFinder {
    private static final String[] DIRECTIONS = {"north", "east", "south", "west"};

    private final Map<Position, String> knownCells = new HashMap<>();

    @Override
    public void updateKnowledge(Position currentPos, String northCell, String eastCell,
                                String southCell, String westCell) {
        if (currentPos == null) {
            return;
        }

        knownCells.putIfAbsent(currentPos, "FLOOR");
        storeNeighbor(currentPos.move("north"), northCell);
        storeNeighbor(currentPos.move("east"), eastCell);
        storeNeighbor(currentPos.move("south"), southCell);
        storeNeighbor(currentPos.move("west"), westCell);
    }

    @Override
    public Deque<String> buildPath(Position start, Position target, int myPlayerId) {
        Deque<String> empty = new ArrayDeque<>();
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
    public Deque<String> buildPathToFrontier(Position start, int myPlayerId) {
        Deque<String> empty = new ArrayDeque<>();
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

    private void storeNeighbor(Position pos, String status) {
        if (pos == null || status == null || status.isEmpty()) {
            return;
        }
        knownCells.put(pos, status);
    }

    private boolean isWalkable(String status, int myPlayerId) {
        if (status == null) {
            return false;
        }
        if ("WALL".equals(status)) {
            return false;
        }
        if (status.startsWith("FINISH")) {
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
        Map<Position, String> moveUsed = new HashMap<>();
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

            for (String dir : DIRECTIONS) {
                Position next = current.move(dir);
                if (visited.contains(next)) {
                    continue;
                }

                String status = knownCells.get(next);
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
        for (String dir : DIRECTIONS) {
            Position neighbor = pos.move(dir);
            if (!knownCells.containsKey(neighbor)) {
                return true;
            }
        }
        return false;
    }

    private Deque<String> reconstructPath(Position start, Position target, MapData data) {
        Deque<String> path = new ArrayDeque<>();
        Position node = target;
        while (!node.equals(start)) {
            String dir = data.moveUsed.get(node);
            if (dir == null) {
                return new ArrayDeque<>();
            }
            path.addFirst("go " + dir);
            node = data.cameFrom.get(node);
        }
        return path;
    }

    private static class MapData {
        final Position goal;
        final Map<Position, Position> cameFrom;
        final Map<Position, String> moveUsed;

        MapData(Position goal, Map<Position, Position> cameFrom, Map<Position, String> moveUsed) {
            this.goal = goal;
            this.cameFrom = cameFrom;
            this.moveUsed = moveUsed;
        }
    }
}
