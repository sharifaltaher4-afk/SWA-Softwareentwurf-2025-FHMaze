package de.fhms.eti.players.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für SheetManager (Level 5 Funktionalität).
 * 
 * Der SheetManager verwaltet den Blättervorrat des Bots.
 * Blätter können:
 * - aufgenommen werden (addSheet)
 * - abgelegt werden (consumeSheet)
 * - gezählt werden (getSheetCount, hasSheets)
 */
@DisplayName("SheetManager Tests (Level 5)")
class SheetManagerTest {

    private SheetManager sheetManager;

    @BeforeEach
    void setUp() {
        // Standard: Bot startet mit 3 Blättern
        sheetManager = new SheetManager(3);
    }

    // ==========================================
    // Konstruktor Tests
    // ==========================================

    @Test
    @DisplayName("Test 01: Konstruktor mit initialem Blättervorrat")
    void test_01_constructorWithInitialSheets() {
        SheetManager manager = new SheetManager(5);
        
        assertEquals(5, manager.getSheetCount(), 
            "SheetCount sollte dem Initialwert entsprechen");
        assertTrue(manager.hasSheets(), 
            "hasSheets() sollte true sein bei Vorrat > 0");
    }

    @Test
    @DisplayName("Test 02: Konstruktor mit 0 Blättern")
    void test_02_constructorWithZeroSheets() {
        SheetManager manager = new SheetManager(0);
        
        assertEquals(0, manager.getSheetCount(), 
            "SheetCount sollte 0 sein");
        assertFalse(manager.hasSheets(), 
            "hasSheets() sollte false sein bei leerem Vorrat");
    }

    // ==========================================
    // getSheetCount Tests
    // ==========================================

    @Test
    @DisplayName("Test 03: getSheetCount gibt korrekten Wert zurück")
    void test_03_getSheetCountReturnsCorrectValue() {
        assertEquals(3, sheetManager.getSheetCount());
    }

    // ==========================================
    // hasSheets Tests
    // ==========================================

    @Test
    @DisplayName("Test 04: hasSheets true bei Vorrat > 0")
    void test_04_hasSheetsReturnsTrueWhenSheetsAvailable() {
        assertTrue(sheetManager.hasSheets());
    }

    @Test
    @DisplayName("Test 05: hasSheets false bei leerem Vorrat")
    void test_05_hasSheetsReturnsFalseWhenEmpty() {
        SheetManager emptyManager = new SheetManager(0);
        assertFalse(emptyManager.hasSheets());
    }

    // ==========================================
    // addSheet Tests
    // ==========================================

    @Test
    @DisplayName("Test 06: addSheet erhöht den Vorrat um 1")
    void test_06_addSheetIncreasesCount() {
        int before = sheetManager.getSheetCount();
        sheetManager.addSheet();
        int after = sheetManager.getSheetCount();
        
        assertEquals(before + 1, after, 
            "addSheet() sollte den Vorrat um 1 erhöhen");
    }

    @Test
    @DisplayName("Test 07: addSheet funktioniert auch bei leerem Vorrat")
    void test_07_addSheetWorksWhenEmpty() {
        SheetManager emptyManager = new SheetManager(0);
        assertFalse(emptyManager.hasSheets());
        
        emptyManager.addSheet();
        
        assertTrue(emptyManager.hasSheets(), 
            "Nach addSheet() sollte hasSheets() true sein");
        assertEquals(1, emptyManager.getSheetCount());
    }

    @Test
    @DisplayName("Test 08: Mehrfaches addSheet")
    void test_08_multipleAddSheet() {
        SheetManager manager = new SheetManager(0);
        
        manager.addSheet();
        manager.addSheet();
        manager.addSheet();
        
        assertEquals(3, manager.getSheetCount());
    }

    // ==========================================
    // consumeSheet Tests
    // ==========================================

    @Test
    @DisplayName("Test 09: consumeSheet verringert Vorrat und gibt true zurück")
    void test_09_consumeSheetDecreasesCountAndReturnsTrue() {
        int before = sheetManager.getSheetCount();
        boolean result = sheetManager.consumeSheet();
        int after = sheetManager.getSheetCount();
        
        assertTrue(result, "consumeSheet() sollte true zurückgeben bei Vorrat > 0");
        assertEquals(before - 1, after, 
            "consumeSheet() sollte den Vorrat um 1 verringern");
    }

    @Test
    @DisplayName("Test 10: consumeSheet gibt false zurück bei leerem Vorrat")
    void test_10_consumeSheetReturnsFalseWhenEmpty() {
        SheetManager emptyManager = new SheetManager(0);
        
        boolean result = emptyManager.consumeSheet();
        
        assertFalse(result, 
            "consumeSheet() sollte false zurückgeben bei leerem Vorrat");
        assertEquals(0, emptyManager.getSheetCount(), 
            "Vorrat sollte bei 0 bleiben");
    }

    @Test
    @DisplayName("Test 11: consumeSheet verringert nicht unter 0")
    void test_11_consumeSheetDoesNotGoBelowZero() {
        SheetManager manager = new SheetManager(1);
        
        assertTrue(manager.consumeSheet());  // Jetzt 0
        assertFalse(manager.consumeSheet()); // Bleibt 0
        assertFalse(manager.consumeSheet()); // Bleibt 0
        
        assertEquals(0, manager.getSheetCount(), 
            "Vorrat sollte nie unter 0 fallen");
    }

    @Test
    @DisplayName("Test 12: Alle Blätter verbrauchen")
    void test_12_consumeAllSheets() {
        SheetManager manager = new SheetManager(3);
        
        assertTrue(manager.consumeSheet()); // 2 übrig
        assertTrue(manager.consumeSheet()); // 1 übrig
        assertTrue(manager.consumeSheet()); // 0 übrig
        assertFalse(manager.consumeSheet()); // Kein Blatt mehr
        
        assertEquals(0, manager.getSheetCount());
        assertFalse(manager.hasSheets());
    }

    // ==========================================
    // Kombinierte Tests (Add + Consume)
    // ==========================================

    @Test
    @DisplayName("Test 13: Add und Consume in Kombination")
    void test_13_addAndConsumeInCombination() {
        SheetManager manager = new SheetManager(1);
        
        assertTrue(manager.consumeSheet()); // 0
        assertFalse(manager.hasSheets());
        
        manager.addSheet(); // 1
        assertTrue(manager.hasSheets());
        
        manager.addSheet(); // 2
        assertEquals(2, manager.getSheetCount());
        
        assertTrue(manager.consumeSheet()); // 1
        assertEquals(1, manager.getSheetCount());
    }

    @Test
    @DisplayName("Test 14: Realistisches Spielszenario")
    void test_14_realisticGameScenario() {
        // Bot startet mit 2 Blättern
        SheetManager manager = new SheetManager(2);
        
        // Legt erstes Blatt ab (PUT)
        assertTrue(manager.consumeSheet());
        assertEquals(1, manager.getSheetCount());
        
        // Nimmt ein Blatt vom Boden auf (TAKE -> OK SHEET)
        manager.addSheet();
        assertEquals(2, manager.getSheetCount());
        
        // Legt zwei Blätter ab
        assertTrue(manager.consumeSheet());
        assertTrue(manager.consumeSheet());
        assertEquals(0, manager.getSheetCount());
        
        // Versucht weiteres Blatt abzulegen - geht nicht
        assertFalse(manager.consumeSheet());
    }

    // ==========================================
    // Interface Tests
    // ==========================================

    @Test
    @DisplayName("Test 15: SheetManager implementiert ISheetManager")
    void test_15_implementsInterface() {
        ISheetManager manager = new SheetManager(5);
        
        // Alle Interface-Methoden müssen funktionieren
        assertEquals(5, manager.getSheetCount());
        assertTrue(manager.hasSheets());
        manager.addSheet();
        assertEquals(6, manager.getSheetCount());
        assertTrue(manager.consumeSheet());
        assertEquals(5, manager.getSheetCount());
    }
}
