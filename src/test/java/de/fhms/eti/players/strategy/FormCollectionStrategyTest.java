package de.fhms.eti.players.strategy;

import de.fhms.eti.players.domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FormCollectionStrategyTest {

    @Test
    void test_01_picksAdjacentNeededForm() {
        FakeFormManager formManager = new FakeFormManager(1, false);
        FakePathFinder pathFinder = new FakePathFinder();
        FormCollectionStrategy strategy = new FormCollectionStrategy(new Maze(10, 10, 2), formManager, pathFinder);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        Cell north = new FormCell(0, 1);

        GoAction action = strategy.decideNextMove(start, north, new FloorCell(), new FloorCell(), new FloorCell());

        assertNotNull(action);
        assertEquals(Direction.north, action.getDirection());
    }

    @Test
    void test_02_ignoresAdjacentForeignForm() {
        FakeFormManager formManager = new FakeFormManager(1, false);
        FakePathFinder pathFinder = new FakePathFinder();
        Maze maze = new Maze(10, 10, 2);
        FormCollectionStrategy strategy = new FormCollectionStrategy(maze, formManager, pathFinder);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        Position northPos = start.move(Direction.north);
        // Make north look unattractive so exploration prefers east
        maze.markVisited(northPos);
        maze.markVisited(northPos);

        Cell north = new FormCell(99, 1); // foreign form
        GoAction action = strategy.decideNextMove(start, north, new FloorCell(), new FloorCell(), new FloorCell());

        assertNotNull(action);
        // Adjacent foreign form should not be chosen; with visit counts set, east is preferred
        assertEquals(Direction.east, action.getDirection());
    }

    @Test
    void test_03_usesRememberedFinishWhenReady() {
        FakeFormManager formManager = new FakeFormManager(1, true); // hasAllForms = true
        FakePathFinder pathFinder = new FakePathFinder();
        // Preload pathfinder to return a single step north
        pathFinder.nextPath = new ArrayDeque<>();
        pathFinder.nextPath.add(new GoAction(Direction.north));

        FormCollectionStrategy strategy = new FormCollectionStrategy(new Maze(10, 10, 3), formManager, pathFinder);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        Cell north = new FinishCell(0, 3);

        GoAction action = strategy.decideNextMove(start, north, new FloorCell(), new FloorCell(), new FloorCell());

        assertNotNull(action);
        assertEquals(Direction.north, action.getDirection());
        assertEquals(start, pathFinder.lastStart);
        assertNotNull(pathFinder.lastTarget);
        assertEquals(start.move(Direction.north), pathFinder.lastTarget);
    }

    @Test
    void test_04_fallsBackToFrontierWhenNoTargets() {
        FakeFormManager formManager = new FakeFormManager(1, false);
        FakePathFinder pathFinder = new FakePathFinder();
        pathFinder.frontierPath = new ArrayDeque<>();
        pathFinder.frontierPath.add(new GoAction(Direction.east));

        FormCollectionStrategy strategy = new FormCollectionStrategy(new Maze(10, 10, 2), formManager, pathFinder);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        GoAction action = strategy.decideNextMove(start, new FloorCell(), new FloorCell(), new FloorCell(), new FloorCell());

        assertNotNull(action);
        assertEquals(Direction.east, action.getDirection());
        // With no known forms/finish, target should remain null
        assertNull(pathFinder.lastTarget);
    }

    @Test
    void test_05_refreshesPlanWhenTargetChanges() {
        FakeFormManager formManager = new FakeFormManager(1, false);
        FakePathFinder pathFinder = new FakePathFinder();
        pathFinder.nextPath.add(new GoAction(Direction.east));

        FormCollectionStrategy strategy = new FormCollectionStrategy(new Maze(10, 10, 2), formManager, pathFinder);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        GoAction first = strategy.decideNextMove(start, new FloorCell(), new FormCell(0, 1), new FloorCell(),
                new FloorCell());
        assertEquals(Direction.east, first.getDirection());
        assertEquals(1, pathFinder.buildPathCallCount);

        // Change target (next form id) and provide that form to the north
        formManager.setNextFormId(2);
        pathFinder.nextPath.clear();
        pathFinder.nextPath.add(new GoAction(Direction.north));

        GoAction second = strategy.decideNextMove(start, new FormCell(0, 2), new FloorCell(), new FloorCell(),
                new FloorCell());
        assertEquals(Direction.north, second.getDirection());
        assertEquals(2, pathFinder.buildPathCallCount);
    }

    @Test
    void test_06_blocksWallsAndForeignFinish() {
        FakeFormManager formManager = new FakeFormManager(1, false);
        FakePathFinder pathFinder = new FakePathFinder();
        // No frontier plan so exploration is used
        pathFinder.frontierPath.clear();

        FormCollectionStrategy strategy = new FormCollectionStrategy(new Maze(10, 10, 2), formManager, pathFinder);
        strategy.setPlayerId(0);

        GoAction action = strategy.decideNextMove(
                new Position(0, 0),
                new WallCell(),
                new FinishCell(99, 3), // foreign finish should be blocked
                new FloorCell(),
                new FloorCell());

        assertEquals(Direction.south, action.getDirection()); // east blocked, south chosen over west due to order
    }

    private static class FakePathFinder implements IPathFinder {
        Position lastStart;
        Position lastTarget;
        Deque<GoAction> nextPath = new ArrayDeque<>();
        Deque<GoAction> frontierPath = new ArrayDeque<>();
        int buildPathCallCount;

        @Override
        public void updateKnowledge(Position currentPos, Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {
            // no-op for tests
        }

        @Override
        public Deque<GoAction> buildPath(Position start, Position target, int myPlayerId) {
            this.lastStart = start;
            this.lastTarget = target;
            buildPathCallCount++;
            return new ArrayDeque<>(nextPath);
        }

        @Override
        public Deque<GoAction> buildPathToFrontier(Position start, int myPlayerId) {
            this.lastStart = start;
            return new ArrayDeque<>(frontierPath);
        }
    }

    private static class FakeFormManager implements IFormManager {
        private int nextFormId;
        private boolean hasAllForms;
        private final List<Integer> collected = new ArrayList<>();

        FakeFormManager(int nextFormId, boolean hasAllForms) {
            this.nextFormId = nextFormId;
            this.hasAllForms = hasAllForms;
        }

        void setNextFormId(int nextFormId) {
            this.nextFormId = nextFormId;
        }

        @Override
        public boolean canTakeForm(FormCell cellStatus) {
            return cellStatus != null && cellStatus.getFormId() == nextFormId && cellStatus.getPlayerId() == 0;
        }

        @Override
        public boolean takeForm(FormCell cellStatus) {
            collected.add(cellStatus.getFormId());
            nextFormId++;
            return true;
        }

        @Override
        public boolean hasAllForms() {
            return hasAllForms;
        }

        @Override
        public int getNextFormId() {
            return nextFormId;
        }

        @Override
        public List<Integer> getCollectedForms() {
            return collected;
        }
    }
}
