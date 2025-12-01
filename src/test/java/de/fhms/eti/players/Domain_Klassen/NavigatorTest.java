package test.java.de.fhms.eti.players.Domain_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.*;
import main.java.de.fhms.eti.players.Strategie_Klassen.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class NavigatorTest {
    
    private Maze maze;
    private Position startPosition;
    private TestStrategy testStrategy;
    private Navigator navigator;
    
    @BeforeEach
    public void setUp() {
        // ============================================================
        // INITIALISIERUNG
        // ============================================================
        maze = new Maze(10, 10, 1); // Maze(sizeX, sizeY, level)
        startPosition = new Position(5, 5);
        testStrategy = new TestStrategy();
        navigator = new Navigator(maze, startPosition, testStrategy);
    }
    
    @Test
    public void testInitialPosition() {
        // ============================================================
        // TEST 1: Initialisierung mit Startposition
        // ============================================================
        assertEquals(startPosition, navigator.getCurrentPosition(), "Navigator startet bei (5, 5)");
        assertTrue(maze.wasVisited(startPosition), "Startposition als besucht markiert");
    }
    
    @Test
    public void testUpdatePositionNorth() {
        // ============================================================
        // TEST 2: Bewegung nach Norden
        // ============================================================
        GoAction northAction = new GoAction(Direction.north);
        navigator.updatePosition(northAction);
        
        Position expected = new Position(5, 4);
        assertEquals(expected, navigator.getCurrentPosition(), "Position nach Norden aktualisiert");
        assertTrue(maze.wasVisited(expected), "Neue Position als besucht markiert");
    }
    
    @Test
    public void testUpdatePositionEast() {
        // ============================================================
        // TEST 3: Bewegung nach Osten
        // ============================================================
        GoAction eastAction = new GoAction(Direction.east);
        navigator.updatePosition(eastAction);
        
        Position expected = new Position(6, 5);
        assertEquals(expected, navigator.getCurrentPosition(), "Position nach Osten aktualisiert");
    }
    
    @Test
    public void testUpdatePositionSouth() {
        // ============================================================
        // TEST 4: Bewegung nach Süden
        // ============================================================
        GoAction southAction = new GoAction(Direction.south);
        navigator.updatePosition(southAction);
        
        Position expected = new Position(5, 6);
        assertEquals(expected, navigator.getCurrentPosition(), "Position nach Süden aktualisiert");
    }
    
    @Test
    public void testUpdatePositionWest() {
        // ============================================================
        // TEST 5: Bewegung nach Westen
        // ============================================================
        GoAction westAction = new GoAction(Direction.west);
        navigator.updatePosition(westAction);
        
        Position expected = new Position(4, 5);
        assertEquals(expected, navigator.getCurrentPosition(), "Position nach Westen aktualisiert");
    }
    
    @Test
    public void testStrategyDelegation() {
        // ============================================================
        // TEST 6: Delegation an Strategie
        // ============================================================
        Cell north = new FloorCell();
        Cell east = new FloorCell();
        Cell south = new WallCell();
        Cell west = new FloorCell();
        
        testStrategy.setNextMove(Direction.north);
        GoAction result = navigator.decideNextMove(north, east, south, west);
        
        assertEquals(Direction.north, result.getDirection(), "Strategie entscheidet NORTH");
    }
    
    @Test
    public void testSetStrategy() {
        // ============================================================
        // TEST 7: Strategie wechseln
        // ============================================================
        TestStrategy newStrategy = new TestStrategy();
        newStrategy.setNextMove(Direction.south);
        
        navigator.setStrategy(newStrategy);
        
        assertEquals(newStrategy, navigator.getStrategy(), "Neue Strategie gesetzt");
        
        Cell north = new FloorCell();
        Cell east = new FloorCell();
        Cell south = new FloorCell();
        Cell west = new FloorCell();
        
        GoAction result = navigator.decideNextMove(north, east, south, west);
        assertEquals(Direction.south, result.getDirection(), "Neue Strategie wird vrwendet");
    }
    
    @Test
    public void testMultipleMovements() {
        // ============================================================
        // TEST 8: Mehrfache Bewegungen
        // ============================================================
        navigator.updatePosition(new GoAction(Direction.north)); // (5, 4)
        navigator.updatePosition(new GoAction(Direction.east));  // (6, 4)
        navigator.updatePosition(new GoAction(Direction.south)); // (6, 5)
        navigator.updatePosition(new GoAction(Direction.west));  // (5, 5)
        
        assertEquals(startPosition, navigator.getCurrentPosition(), "Zurück am Start nach 4 Bewegungen");
    }
    
    @Test
    public void testVisitCounting() {
        // ============================================================
        // TEST 9: Besuchszähler
        // ============================================================
        Position testPos = new Position(5, 4);
        
        // Erste Bewegung nach Norden
        navigator.updatePosition(new GoAction(Direction.north));
        assertEquals(1, maze.getVisitCount(testPos), "Position einmal besucht");
        
        // Zurück nach Süden
        navigator.updatePosition(new GoAction(Direction.south));
        
        // Nochmal nach Norden (gleiche Position)
        navigator.updatePosition(new GoAction(Direction.north));
        assertEquals(2, maze.getVisitCount(testPos), "Position zweimal besucht");
    }
    
    // ============================================================
    // HILFS-KLASSE: Test-Strategie
    // ============================================================
    
    private static class TestStrategy implements INavigationStrategy {
        private Direction nextMove = Direction.north;
        private int playerId = 0;
        
        public void setNextMove(Direction direction) {
            this.nextMove = direction;
        }
        
        @Override
        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }
        
        @Override
        public GoAction decideNextMove(Position currentPosition, 
                                       Cell northCell, Cell eastCell, 
                                       Cell southCell, Cell westCell) {
            return new GoAction(nextMove);
        }
    }
}