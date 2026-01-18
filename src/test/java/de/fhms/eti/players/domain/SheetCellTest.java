package de.fhms.eti.players.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für SheetCell (Level 5 Funktionalität).
 * 
 * SheetCell repräsentiert ein Feld mit einem Blatt.
 * Blätter können:
 * - Formulare verdecken
 * - aufgenommen werden (TAKE)
 * - gekickt werden (KICK)
 * 
 * SheetCell ist immer begehbar (isAccessable = true).
 */
@DisplayName("SheetCell Tests (Level 5)")
class SheetCellTest {

    // ==========================================
    // Konstruktor Tests
    // ==========================================

    @Test
    @DisplayName("Test 01: Default-Konstruktor erstellt SheetCell ohne Gegner")
    void test_01_defaultConstructorNoOpponent() {
        SheetCell cell = new SheetCell();
        
        assertFalse(cell.hasOpponent(), 
            "Default-Konstruktor sollte keinen Gegner setzen");
        assertEquals(-1, cell.getOpponentDistance(), 
            "Default-Distanz sollte -1 sein");
    }

    @Test
    @DisplayName("Test 02: Konstruktor mit Gegner-Info")
    void test_02_constructorWithOpponentInfo() {
        SheetCell cell = new SheetCell(true, 2);
        
        assertTrue(cell.hasOpponent(), 
            "hasOpponent sollte true sein");
        assertEquals(2, cell.getOpponentDistance(), 
            "Gegner-Distanz sollte 2 sein");
    }

    @Test
    @DisplayName("Test 03: Konstruktor ohne Gegner")
    void test_03_constructorWithoutOpponent() {
        SheetCell cell = new SheetCell(false, -1);
        
        assertFalse(cell.hasOpponent());
        assertEquals(-1, cell.getOpponentDistance());
    }

    // ==========================================
    // isAccessable Tests
    // ==========================================

    @Test
    @DisplayName("Test 04: SheetCell ist immer begehbar")
    void test_04_isAlwaysAccessable() {
        SheetCell cell1 = new SheetCell();
        SheetCell cell2 = new SheetCell(true, 0);
        SheetCell cell3 = new SheetCell(false, -1);
        
        assertTrue(cell1.isAccessable(), 
            "SheetCell sollte immer begehbar sein");
        assertTrue(cell2.isAccessable(), 
            "SheetCell mit Gegner sollte begehbar sein");
        assertTrue(cell3.isAccessable(), 
            "SheetCell ohne Gegner sollte begehbar sein");
    }

    // ==========================================
    // Vererbung Tests
    // ==========================================

    @Test
    @DisplayName("Test 05: SheetCell erbt von Cell")
    void test_05_extendsCell() {
        SheetCell sheetCell = new SheetCell();
        
        assertTrue(sheetCell instanceof Cell, 
            "SheetCell muss von Cell erben");
    }

    @Test
    @DisplayName("Test 06: SheetCell kann als Cell verwendet werden (LSP)")
    void test_06_canBeUsedAsCell() {
        Cell cell = new SheetCell(true, 1);
        
        // Alle Cell-Methoden müssen funktionieren
        assertTrue(cell.hasOpponent());
        assertEquals(1, cell.getOpponentDistance());
        assertTrue(cell.isAccessable());
    }

    // ==========================================
    // Gegner-Info Tests (von Cell geerbt)
    // ==========================================

    @Test
    @DisplayName("Test 07: Gegner auf gleichem Feld (Distance 0)")
    void test_07_opponentOnSameField() {
        SheetCell cell = new SheetCell(true, 0);
        
        assertTrue(cell.hasOpponent());
        assertEquals(0, cell.getOpponentDistance(), 
            "Distance 0 bedeutet Gegner auf gleichem Feld");
    }

    @Test
    @DisplayName("Test 08: Gegner in der Nähe (Distance > 0)")
    void test_08_opponentNearby() {
        SheetCell cell = new SheetCell(true, 3);
        
        assertTrue(cell.hasOpponent());
        assertEquals(3, cell.getOpponentDistance());
    }

    // ==========================================
    // Vergleich mit anderen Cell-Typen
    // ==========================================

    @Test
    @DisplayName("Test 09: SheetCell ist kein WallCell")
    void test_09_isNotWallCell() {
        Cell sheetCell = new SheetCell();
        
        assertFalse(sheetCell instanceof WallCell,  "SheetCell ist kein WallCell");
    }

    @Test
    @DisplayName("Test 10: SheetCell ist kein FormCell")
    void test_10_isNotFormCell() {
        Cell sheetCell = new SheetCell();
        
        assertFalse(sheetCell instanceof FormCell, 
            "SheetCell ist kein FormCell");
    }

    @Test
    @DisplayName("Test 11: SheetCell ist kein FloorCell")
    void test_11_isNotFloorCell() {
        Cell sheetCell = new SheetCell();
        
        assertFalse(sheetCell instanceof FloorCell, 
            "SheetCell ist kein FloorCell");
    }

    @Test
    @DisplayName("Test 12: SheetCell ist kein FinishCell")
    void test_12_isNotFinishCell() {
        Cell sheetCell = new SheetCell();
        
        assertFalse(sheetCell instanceof FinishCell, 
            "SheetCell ist kein FinishCell");
    }

    // ==========================================
    // instanceof Pattern Matching Tests
    // ==========================================

    @Test
    @DisplayName("Test 13: Pattern Matching funktioniert")
    void test_13_patternMatchingWorks() {
        Cell cell = new SheetCell(true, 2);
        
        if (cell instanceof SheetCell sheetCell) {
            assertTrue(sheetCell.hasOpponent());
            assertEquals(2, sheetCell.getOpponentDistance());
        } else {
            fail("Pattern Matching sollte SheetCell erkennen");
        }
    }

    @Test
    @DisplayName("Test 14: Switch-Expression für Cell-Typen")
    void test_14_switchExpressionForCellTypes() {
        Cell cell = new SheetCell();
        
        String type = switch (cell) {
            case WallCell w -> "WALL";
            case FloorCell f -> "FLOOR";
            case FormCell f -> "FORM";
            case FinishCell f -> "FINISH";
            case SheetCell s -> "SHEET";
            default -> "UNKNOWN";
        };
        
        assertEquals("SHEET", type, 
            "Switch sollte SheetCell erkennen");
    }
}
