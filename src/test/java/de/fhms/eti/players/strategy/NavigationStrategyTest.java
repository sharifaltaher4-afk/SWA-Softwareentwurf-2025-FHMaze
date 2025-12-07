package test.java.de.fhms.eti.players.strategy;

import main.java.de.fhms.eti.players.domain.*;
import main.java.de.fhms.eti.players.strategy.SimpleNavigationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NavigationStrategyTest {

    @Test
    void test_01_finishPriorityBeatsExploration() {
        SimpleNavigationStrategy strategy = new SimpleNavigationStrategy(new Maze(10, 10, 1));
        strategy.setPlayerId(0);

        GoAction action = strategy.decideNextMove(
                new Position(0, 0),
                new FinishCell(0, 0), // own finish north
                new FloorCell(),
                new FloorCell(),
                new FloorCell());

        assertNotNull(action);
        assertEquals(Direction.north, action.getDirection());
    }

    @Test
    void test_02_skipsForeignFinishAndPicksLessVisited() {
        Maze maze = new Maze(10, 10, 1);
        SimpleNavigationStrategy strategy = new SimpleNavigationStrategy(maze);
        strategy.setPlayerId(0);

        Position start = new Position(0, 0);
        // Make east more visited than west to prefer west
        maze.markVisited(start.move(Direction.east));
        maze.markVisited(start.move(Direction.east));

        GoAction action = strategy.decideNextMove(
                start,
                new FloorCell(),
                new FinishCell(99, 0), // foreign finish should be skipped
                new FloorCell(),
                new FloorCell());

        assertEquals(Direction.west, action.getDirection());
    }

    @Test
    void test_03_fallbackWhenAllBlockedExceptOne() {
        SimpleNavigationStrategy strategy = new SimpleNavigationStrategy(new Maze(10, 10, 1));
        strategy.setPlayerId(0);

        GoAction action = strategy.decideNextMove(
                new Position(0, 0),
                new WallCell(),
                new WallCell(),
                new WallCell(),
                new FloorCell());

        assertEquals(Direction.west, action.getDirection());
    }
}
