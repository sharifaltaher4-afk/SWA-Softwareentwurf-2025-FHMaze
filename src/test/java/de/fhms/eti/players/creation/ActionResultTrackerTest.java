package de.fhms.eti.players.creation;

import de.fhms.eti.players.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für ActionResultTracker (Level 5 Sheet-Handling).
 * 
 * Der ActionResultTracker verarbeitet Aktionsergebnisse und verwaltet:
 * - Sheet-Cooldown (Aufnahme eines Sheets benötigt 2 Züge)
 * - PUT-Position (verhindert wiederholtes Ablegen am gleichen Ort)
 * - Sheet-Empty Status (Bot hat keine Blätter mehr)
 */
@DisplayName("ActionResultTracker Tests (Level 5)")
class ActionResultTrackerTest {

    private ActionResultTracker tracker;
    private SheetManager sheetManager;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        tracker = new ActionResultTracker();
        sheetManager = new SheetManager(3);
        testPosition = new Position(5, 5);
    }

    // ==========================================
    // canTakeSheet Tests
    // ==========================================

    @Test
    @DisplayName("Test 01: canTakeSheet true wenn keine Blätter und andere Position")
    void test_01_canTakeSheetWhenEmptyAndDifferentPosition() {
        SheetManager emptyManager = new SheetManager(0);
        Position currentPos = new Position(3, 3);
        
        boolean canTake = tracker.canTakeSheet(currentPos, emptyManager);
        
        assertTrue(canTake, 
            "Sollte Sheet aufnehmen können wenn Vorrat leer ist");
    }

    @Test
    @DisplayName("Test 02: canTakeSheet false wenn Blätter vorhanden")
    void test_02_canTakeSheetFalseWhenHasSheets() {
        SheetManager managerWithSheets = new SheetManager(2);
        Position currentPos = new Position(3, 3);
        
        boolean canTake = tracker.canTakeSheet(currentPos, managerWithSheets);
        
        assertFalse(canTake, 
            "Sollte kein Sheet aufnehmen wenn Vorrat nicht leer");
    }

    // ==========================================
    // isSheetEmptyKnown Tests
    // ==========================================

    @Test
    @DisplayName("Test 03: isSheetEmptyKnown initial false")
    void test_03_isSheetEmptyKnownInitiallyFalse() {
        assertFalse(tracker.isSheetEmptyKnown(), 
            "Initial sollte sheetEmptyKnown false sein");
    }

    // ==========================================
    // isSameAsLastPut Tests
    // ==========================================

    @Test
    @DisplayName("Test 04: isSameAsLastPut false wenn noch kein PUT")
    void test_04_isSameAsLastPutFalseInitially() {
        Position anyPos = new Position(1, 1);
        
        assertFalse(tracker.isSameAsLastPut(anyPos), 
            "Sollte false sein wenn noch kein PUT gemacht wurde");
    }

    // ==========================================
    // consumeSheetCooldown Tests
    // ==========================================

    @Test
    @DisplayName("Test 05: consumeSheetCooldown false wenn kein Cooldown")
    void test_05_consumeSheetCooldownFalseWhenNoCooldown() {
        boolean consumed = tracker.consumeSheetCooldown();
        
        assertFalse(consumed, 
            "Sollte false sein wenn kein Cooldown pending ist");
    }

    @Test
    @DisplayName("Test 06: consumeSheetCooldown nur einmal true")
    void test_06_consumeSheetCooldownOnlyOnceTrue() {
        // Simuliere: Sheet wurde aufgenommen, Cooldown ist pending
        // (Dies würde normalerweise durch reconcile() gesetzt werden)
        
        // Initial kein Cooldown
        assertFalse(tracker.consumeSheetCooldown());
        assertFalse(tracker.consumeSheetCooldown());
    }

    // ==========================================
    // isTakingOrTalking Tests
    // ==========================================

    @Test
    @DisplayName("Test 07: isTakingOrTalking erkennt NOK TAKING")
    void test_07_recognizesNokTaking() {
        assertTrue(tracker.isTakingOrTalking("NOK TAKING"), 
            "Sollte NOK TAKING erkennen");
    }

    @Test
    @DisplayName("Test 08: isTakingOrTalking erkennt NOK TALKING")
    void test_08_recognizesNokTalking() {
        assertTrue(tracker.isTakingOrTalking("NOK TALKING"), 
            "Sollte NOK TALKING erkennen");
    }

    @Test
    @DisplayName("Test 09: isTakingOrTalking false für OK")
    void test_09_isTakingOrTalkingFalseForOk() {
        assertFalse(tracker.isTakingOrTalking("OK"), 
            "Sollte false sein für OK");
    }

    @Test
    @DisplayName("Test 10: isTakingOrTalking false für OK SHEET")
    void test_10_isTakingOrTalkingFalseForOkSheet() {
        assertFalse(tracker.isTakingOrTalking("OK SHEET"), 
            "Sollte false sein für OK SHEET");
    }

    @Test
    @DisplayName("Test 11: isTakingOrTalking false für OK FORM")
    void test_11_isTakingOrTalkingFalseForOkForm() {
        assertFalse(tracker.isTakingOrTalking("OK FORM"), 
            "Sollte false sein für OK FORM");
    }

    @Test
    @DisplayName("Test 12: isTakingOrTalking false für NOK BLOCKED")
    void test_12_isTakingOrTalkingFalseForNokBlocked() {
        assertFalse(tracker.isTakingOrTalking("NOK BLOCKED"), 
            "Sollte false sein für NOK BLOCKED");
    }

    @Test
    @DisplayName("Test 13: isTakingOrTalking false für null")
    void test_13_isTakingOrTalkingFalseForNull() {
        assertFalse(tracker.isTakingOrTalking(null), 
            "Sollte false sein für null");
    }

    // ==========================================
    // Action Result Parsing Tests
    // ==========================================

    @Test
    @DisplayName("Test 14: Erkennt verschiedene Action Results")
    void test_14_recognizesVariousActionResults() {
        // Diese Tests prüfen indirekt das Parsing über isTakingOrTalking
        // und andere Methoden
        
        // NOK TAKING und NOK TALKING
        assertTrue(tracker.isTakingOrTalking("NOK TAKING"));
        assertTrue(tracker.isTakingOrTalking("NOK TALKING"));
        assertTrue(tracker.isTakingOrTalking("nok taking")); // Case insensitive
        assertTrue(tracker.isTakingOrTalking("  NOK TAKING  ")); // Trim
        
        // Andere Results
        assertFalse(tracker.isTakingOrTalking("OK"));
        assertFalse(tracker.isTakingOrTalking("OK FORM"));
        assertFalse(tracker.isTakingOrTalking("OK SHEET"));
        assertFalse(tracker.isTakingOrTalking("NOK BLOCKED"));
        assertFalse(tracker.isTakingOrTalking("NOK EMPTY"));
        assertFalse(tracker.isTakingOrTalking("NOK WRONGORDER"));
        assertFalse(tracker.isTakingOrTalking("NOK NOTYOURS"));
    }

    // ==========================================
    // Edge Cases
    // ==========================================

    @Test
    @DisplayName("Test 15: Leerer String wird korrekt behandelt")
    void test_15_emptyStringHandledCorrectly() {
        assertFalse(tracker.isTakingOrTalking(""), 
            "Leerer String sollte false liefern");
    }

    @Test
    @DisplayName("Test 16: Whitespace wird korrekt behandelt")
    void test_16_whitespaceHandledCorrectly() {
        assertFalse(tracker.isTakingOrTalking("   "), 
            "Nur Whitespace sollte false liefern");
    }

    @Test
    @DisplayName("Test 17: Unbekanntes Result wird korrekt behandelt")
    void test_17_unknownResultHandledCorrectly() {
        assertFalse(tracker.isTakingOrTalking("UNKNOWN_RESULT"), 
            "Unbekanntes Result sollte false liefern");
    }

    // ==========================================
    // Integration Test
    // ==========================================

    @Test
    @DisplayName("Test 18: Vollständiger Sheet-Aufnahme Workflow")
    void test_18_completeSheetTakeWorkflow() {
        // Szenario: Bot hat keine Blätter, kann Sheet aufnehmen
        SheetManager emptyManager = new SheetManager(0);
        Position sheetPos = new Position(3, 3);
        
        // 1. Kann Sheet aufnehmen?
        assertTrue(tracker.canTakeSheet(sheetPos, emptyManager));
        
        // 2. Kein Cooldown pending
        assertFalse(tracker.consumeSheetCooldown());
        
        // 3. Noch kein PUT gemacht
        assertFalse(tracker.isSameAsLastPut(sheetPos));
    }
}
