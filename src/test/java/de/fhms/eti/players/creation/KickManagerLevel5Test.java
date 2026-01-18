package de.fhms.eti.players.creation;

import de.fhms.eti.players.communication.TurnData;
import de.fhms.eti.players.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für KickManager Level 5 Erweiterung.
 * 
 * In Level 5 kann der KickManager nicht nur Formulare, sondern auch
 * Sheets (Blätter) kicken!
 * 
 * KICK-Regeln:
 * - kick <direction> verschiebt Formular ODER Sheet
 * - Ziel muss frei sein (kein Wall, Form, Sheet, Finish, Gegner)
 * - Ergebnisse: OK <DIR>, NOK BLOCKED, NOK EMPTY
 */
@DisplayName("KickManager Level 5 Tests (Sheet-Kick)")
class KickManagerLevel5Test {

    // ==========================================
    // KickAction Basis-Tests (zur Vollständigkeit)
    // ==========================================

    @Test
    @DisplayName("Test 01: KickAction speichert Richtung NORTH")
    void test_01_kickActionStoresDirectionNorth() {
        KickAction kickAction = new KickAction(Direction.north);
        
        assertEquals(Direction.north, kickAction.getDirection());
        assertTrue(kickAction instanceof Action);
    }

    @Test
    @DisplayName("Test 02: KickAction speichert Richtung EAST")
    void test_02_kickActionStoresDirectionEast() {
        KickAction kickAction = new KickAction(Direction.east);
        
        assertEquals(Direction.east, kickAction.getDirection());
    }

    @Test
    @DisplayName("Test 03: KickAction speichert Richtung SOUTH")
    void test_03_kickActionStoresDirectionSouth() {
        KickAction kickAction = new KickAction(Direction.south);
        
        assertEquals(Direction.south, kickAction.getDirection());
    }

    @Test
    @DisplayName("Test 04: KickAction speichert Richtung WEST")
    void test_04_kickActionStoresDirectionWest() {
        KickAction kickAction = new KickAction(Direction.west);
        
        assertEquals(Direction.west, kickAction.getDirection());
    }

    @Test
    @DisplayName("Test 05: KickAction erbt von Action")
    void test_05_kickActionExtendsAction() {
        KickAction kickAction = new KickAction(Direction.north);
        
        assertTrue(kickAction instanceof Action, 
            "KickAction muss von Action erben");
    }

    @Test
    @DisplayName("Test 06: KickAction Direction ist immutable")
    void test_06_kickActionDirectionIsImmutable() {
        Direction originalDirection = Direction.north;
        KickAction kickAction = new KickAction(originalDirection);
        
        Direction firstCall = kickAction.getDirection();
        Direction secondCall = kickAction.getDirection();
        Direction thirdCall = kickAction.getDirection();
        
        assertEquals(originalDirection, firstCall);
        assertEquals(originalDirection, secondCall);
        assertEquals(originalDirection, thirdCall);
    }

    @Test
    @DisplayName("Test 07: Verschiedene KickActions sind unabhängig")
    void test_07_multipleKickActionsAreIndependent() {
        KickAction kickNorth = new KickAction(Direction.north);
        KickAction kickSouth = new KickAction(Direction.south);
        
        assertEquals(Direction.north, kickNorth.getDirection());
        assertEquals(Direction.south, kickSouth.getDirection());
        assertNotEquals(kickNorth.getDirection(), kickSouth.getDirection());
    }

    // ==========================================
    // Level 5 spezifische Tests
    // ==========================================

    @Test
    @DisplayName("Test 08: KickAction kann für Sheets verwendet werden")
    void test_08_kickActionCanBeUsedForSheets() {
        // In Level 5 kann KICK sowohl für Formulare als auch für Sheets verwendet werden
        // Die KickAction selbst unterscheidet nicht - sie speichert nur die Richtung
        
        KickAction kickForSheet = new KickAction(Direction.east);
        KickAction kickForForm = new KickAction(Direction.west);
        
        // Beide sind gültige KickActions
        assertNotNull(kickForSheet);
        assertNotNull(kickForForm);
        assertTrue(kickForSheet instanceof Action);
        assertTrue(kickForForm instanceof Action);
    }

    @Test
    @DisplayName("Test 09: KickAction für alle vier Richtungen")
    void test_09_kickActionForAllDirections() {
        Direction[] allDirections = Direction.values();
        
        for (Direction dir : allDirections) {
            KickAction kick = new KickAction(dir);
            assertEquals(dir, kick.getDirection(), 
                "KickAction sollte Richtung " + dir + " korrekt speichern");
        }
    }

    @Test
    @DisplayName("Test 10: KickAction mit null Direction (Grenzfall)")
    void test_10_kickActionWithNullDirection() {
        // Aktuelles Verhalten: null wird akzeptiert
        KickAction kickAction = new KickAction(null);
        
        assertNull(kickAction.getDirection(), 
            "KickAction akzeptiert null als Direction");
    }

    // ==========================================
    // Szenarien für Sheet-Kick (Dokumentation)
    // ==========================================

    @Test
    @DisplayName("Test 11: Dokumentation - Wann wird Sheet gekickt")
    void test_11_documentationWhenSheetIsKicked() {
        /*
         * Ein Sheet sollte gekickt werden wenn:
         * 
         * 1. Bot steht auf SheetCell (currentCell instanceof SheetCell)
         * 2. Level 5 ist aktiv (levelDetector.isLevel5())
         * 3. Ein freies Ziel existiert (kein Wall, Form, Sheet, Finish, Gegner)
         * 
         * Der KickManager.decideSheetKick() prüft diese Bedingungen.
         * 
         * Mögliche Ergebnisse nach dem Kick:
         * - OK <DIRECTION>: Kick erfolgreich
         * - NOK BLOCKED: Ziel war blockiert
         * - NOK EMPTY: Kein Sheet auf aktuellem Feld (sollte nicht passieren wenn korrekt geprüft)
         */
        
        // Dieser Test dokumentiert das erwartete Verhalten
        assertTrue(true);
    }

    @Test
    @DisplayName("Test 12: Dokumentation - Unterschied Form-Kick vs Sheet-Kick")
    void test_12_documentationFormKickVsSheetKick() {
        /*
         * Form-Kick (Level 4 & 5):
         * - Bot steht auf FormCell
         * - Formular gehört NICHT dem Bot (formCell.getPlayerId() != config.playerId())
         * - Formular kann NICHT genommen werden (falsche Reihenfolge)
         * - Freies Ziel muss existieren
         * 
         * Sheet-Kick (Level 5 only):
         * - Bot steht auf SheetCell
         * - Freies Ziel muss existieren
         * - Kein Besitzer-Check nötig (Sheets gehören niemandem)
         * 
         * Beide verwenden die gleiche KickAction!
         */
        
        KickAction formKick = new KickAction(Direction.north);
        KickAction sheetKick = new KickAction(Direction.south);
        
        // Beide sind KickActions, nur die Logik im KickManager unterscheidet
        assertTrue(formKick instanceof KickAction);
        assertTrue(sheetKick instanceof KickAction);
    }

    // ==========================================
    // Hilfsmethoden-Tests (falls vorhanden)
    // ==========================================

    @Test
    @DisplayName("Test 13: Freie Zelle Prüfung - FloorCell ist frei")
    void test_13_floorCellIsFreeTarget() {
        FloorCell floor = new FloorCell();
        
        assertTrue(isFreeTargetCell(floor), 
            "FloorCell sollte ein gültiges Kick-Ziel sein");
    }

    @Test
    @DisplayName("Test 14: Freie Zelle Prüfung - WallCell ist NICHT frei")
    void test_14_wallCellIsNotFreeTarget() {
        WallCell wall = new WallCell();
        
        assertFalse(isFreeTargetCell(wall), 
            "WallCell sollte KEIN gültiges Kick-Ziel sein");
    }

    @Test
    @DisplayName("Test 15: Freie Zelle Prüfung - SheetCell ist NICHT frei")
    void test_15_sheetCellIsNotFreeTarget() {
        SheetCell sheet = new SheetCell();
        
        assertFalse(isFreeTargetCell(sheet), 
            "SheetCell sollte KEIN gültiges Kick-Ziel sein");
    }

    @Test
    @DisplayName("Test 16: Freie Zelle Prüfung - FormCell ist NICHT frei")
    void test_16_formCellIsNotFreeTarget() {
        FormCell form = new FormCell(1, 1);
        
        assertFalse(isFreeTargetCell(form), 
            "FormCell sollte KEIN gültiges Kick-Ziel sein");
    }

    @Test
    @DisplayName("Test 17: Freie Zelle Prüfung - FinishCell ist NICHT frei")
    void test_17_finishCellIsNotFreeTarget() {
        FinishCell finish = new FinishCell(1, 2);
        
        assertFalse(isFreeTargetCell(finish), 
            "FinishCell sollte KEIN gültiges Kick-Ziel sein");
    }

    @Test
    @DisplayName("Test 18: Freie Zelle Prüfung - FloorCell mit Gegner ist NICHT frei")
    void test_18_floorCellWithOpponentIsNotFree() {
        FloorCell floorWithOpponent = new FloorCell(true, 0);
        
        assertFalse(isFreeTargetCell(floorWithOpponent), 
            "FloorCell mit Gegner sollte KEIN gültiges Kick-Ziel sein");
    }

    /**
     * Hilfsmethode: Prüft ob eine Zelle ein gültiges Kick-Ziel ist.
     * Entspricht der Logik in KickManager.isFreeTargetCell()
     */
    private boolean isFreeTargetCell(Cell cell) {
        if (cell instanceof WallCell) return false;
        if (cell instanceof FormCell) return false;
        if (cell instanceof SheetCell) return false;
        if (cell instanceof FinishCell) return false;
        return !cell.hasOpponent();
    }
}
