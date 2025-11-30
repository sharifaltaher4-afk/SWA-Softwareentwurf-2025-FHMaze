package test.java.de.fhms.eti.players.Domain_Klassen;

import de.fhms.eti.players.Domain_Klassen.OpponentDetector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpponentDetectorTest {

    @Test
    void test_01_detectsOpponentOnAdjacentCell() {
        OpponentDetector detector = new OpponentDetector(0);
        detector.scanForOpponents("FLOOR", "FLOOR !", "FLOOR", "FLOOR", "FLOOR");

        assertTrue(detector.hasOpponentInDirection("north"));
        assertEquals(0, detector.getOpponentDistance("north"));
        assertFalse(detector.hasOpponentOnCurrentField());
    }

    @Test
    void test_02_detectsDistanceMarker() {
        OpponentDetector detector = new OpponentDetector(0);
        detector.scanForOpponents("FLOOR", "FLOOR !3", "FLOOR", "FLOOR", "FLOOR");

        assertTrue(detector.hasOpponentInDirection("north"));
        assertEquals(3, detector.getOpponentDistance("north"));
    }

    @Test
    void test_03_currentFieldMarker() {
        OpponentDetector detector = new OpponentDetector(0);
        detector.scanForOpponents("FLOOR !", "FLOOR", "FLOOR", "FLOOR", "FLOOR");

        assertTrue(detector.hasOpponentOnCurrentField());
        assertEquals(1, detector.getOpponentCount());
    }

    @Test
    void test_04_removeOpponentMarker() {
        assertEquals("FLOOR", OpponentDetector.removeOpponentMarker("FLOOR !2"));
        assertEquals("FORM 0 1", OpponentDetector.removeOpponentMarker("FORM 0 1 !"));
        assertEquals("WALL", OpponentDetector.removeOpponentMarker("WALL"));
    }
}
