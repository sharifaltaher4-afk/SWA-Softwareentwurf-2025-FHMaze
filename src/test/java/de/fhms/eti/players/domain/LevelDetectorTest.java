package test.java.de.fhms.eti.players.domain;

import main.java.de.fhms.eti.players.domain.LevelDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class LevelDetectorTest {
    
    private LevelDetector detector;
    
    @BeforeEach
    public void setUp() {
        // ============================================
        // INITIALISIERUNG
        // ============================================
        detector = new LevelDetector();
    }
    
    @Test
    public void testInitialLevel() {
        // ============================================
        // TEST 1: Initial Level 1
        // ============================================
        assertEquals(1, detector.getCurrentLevel(), "Initial: Level 1");
        assertTrue(detector.isLevel1(), "Is Level 1");
        assertFalse(detector.isLevel2(), "Not Level 2");
        assertFalse(detector.isLevel3(), "Not Level 3");
    }
    
    @Test
    public void testSetLevelManually() {
        // ============================================
        // TEST 2: Level manuell setzen (setCurrentLevel)
        // ============================================
        detector.setCurrentLevel(2);
        assertEquals(2, detector.getCurrentLevel(), "Level 2 gesetzt");
        assertTrue(detector.isLevel2(), "Is Level 2");
        assertFalse(detector.isLevel1(), "Not Level 1");
    }
    
    @Test
    public void testSetLevel3Manually() {
        // ============================================
        // TEST 3: Level 3 manuell setzen
        // ============================================
        detector.setCurrentLevel(3);
        assertEquals(3, detector.getCurrentLevel(), "Level 3 gesetzt");
        assertTrue(detector.isLevel3(), "Is Level 3");
        assertFalse(detector.isLevel1(), "Not Level 1");
        assertFalse(detector.isLevel2(), "Not Level 2");
    }
    
    @Test
    public void testLevelProgression() {
        // ============================================
        // TEST 4: Level-Fortschritt 1 → 2 → 3
        // ============================================
        assertEquals(1, detector.getCurrentLevel(), "Start: Level 1");
        
        // Level 2
        detector.setCurrentLevel(2);
        assertEquals(2, detector.getCurrentLevel(), "Level 2 erreicht");
        
        // Level 3
        detector.setCurrentLevel(3);
        assertEquals(3, detector.getCurrentLevel(), "Level 3 erreicht");
    }
}