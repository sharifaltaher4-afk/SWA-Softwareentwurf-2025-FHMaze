package de.fhms.eti.players.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für Level 5 Actions: PutAction, KickAction (erweitert), TakeAction.
 * 
 * Level 5 Actions:
 * - PUT: Blatt auf aktuelles Feld legen
 * - TAKE: Blatt oder Formular aufnehmen
 * - KICK: Formular ODER Blatt in eine Richtung kicken (Level 5 erweitert!)
 */
@DisplayName("Level 5 Actions Tests")
class Level5ActionsTest {

    // ==========================================
    // PutAction Tests
    // ==========================================

    @Test
    @DisplayName("Test 01: PutAction ist eine Action")
    void test_01_putActionIsAction() {
        PutAction putAction = new PutAction();
        
        assertTrue(putAction instanceof Action, 
            "PutAction muss von Action erben");
    }

    @Test
    @DisplayName("Test 02: PutAction kann erstellt werden")
    void test_02_putActionCanBeCreated() {
        PutAction putAction = new PutAction();
        
        assertNotNull(putAction, 
            "PutAction sollte nicht null sein");
    }

    @Test
    @DisplayName("Test 03: Mehrere PutActions sind unabhängig")
    void test_03_multiplePutActionsAreIndependent() {
        PutAction put1 = new PutAction();
        PutAction put2 = new PutAction();
        
        assertNotSame(put1, put2, 
            "Verschiedene PutAction-Instanzen sollten unterschiedlich sein");
    }

    // ==========================================
    // TakeAction Tests (Level 5 erweitert)
    // ==========================================

    @Test
    @DisplayName("Test 04: TakeAction ist eine Action")
    void test_04_takeActionIsAction() {
        TakeAction takeAction = new TakeAction();
        
        assertTrue(takeAction instanceof Action, 
            "TakeAction muss von Action erben");
    }

    @Test
    @DisplayName("Test 05: TakeAction kann für Formulare und Sheets verwendet werden")
    void test_05_takeActionForFormsAndSheets() {
        // In Level 5 kann TAKE sowohl Formulare als auch Sheets aufnehmen
        // Die Aktion selbst unterscheidet nicht - das Ergebnis unterscheidet:
        // - OK FORM = Formular aufgenommen
        // - OK SHEET = Blatt aufgenommen
        
        TakeAction takeAction = new TakeAction();
        assertNotNull(takeAction);
        assertTrue(takeAction instanceof Action);
    }

    // ==========================================
    // KickAction Tests (Level 5 erweitert für Sheets)
    // ==========================================

    @Test
    @DisplayName("Test 06: KickAction ist eine Action")
    void test_06_kickActionIsAction() {
        KickAction kickAction = new KickAction(Direction.north);
        
        assertTrue(kickAction instanceof Action, 
            "KickAction muss von Action erben");
    }

    @Test
    @DisplayName("Test 07: KickAction speichert Richtung korrekt")
    void test_07_kickActionStoresDirection() {
        KickAction kickNorth = new KickAction(Direction.north);
        KickAction kickEast = new KickAction(Direction.east);
        KickAction kickSouth = new KickAction(Direction.south);
        KickAction kickWest = new KickAction(Direction.west);
        
        assertEquals(Direction.north, kickNorth.getDirection());
        assertEquals(Direction.east, kickEast.getDirection());
        assertEquals(Direction.south, kickSouth.getDirection());
        assertEquals(Direction.west, kickWest.getDirection());
    }

    @Test
    @DisplayName("Test 08: KickAction für alle Richtungen")
    void test_08_kickActionAllDirections() {
        for (Direction dir : Direction.values()) {
            KickAction kickAction = new KickAction(dir);
            assertEquals(dir, kickAction.getDirection(), 
                "KickAction sollte Richtung " + dir + " korrekt speichern");
        }
    }

    @Test
    @DisplayName("Test 09: KickAction ist immutable")
    void test_09_kickActionIsImmutable() {
        KickAction kickAction = new KickAction(Direction.north);
        
        // Mehrfache Aufrufe sollten immer gleichen Wert liefern
        Direction first = kickAction.getDirection();
        Direction second = kickAction.getDirection();
        Direction third = kickAction.getDirection();
        
        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(Direction.north, first);
    }

    // ==========================================
    // Action Polymorphie Tests
    // ==========================================

    @Test
    @DisplayName("Test 10: Alle Level 5 Actions können polymorph verwendet werden")
    void test_10_actionsCanBeUsedPolymorphically() {
        Action[] actions = {
            new GoAction(Direction.north),
            new FinishAction(),
            new PositionAction(),
            new TakeAction(),
            new PutAction(),
            new KickAction(Direction.east)
        };
        
        assertEquals(6, actions.length, 
            "Alle 6 Action-Typen sollten erstellt werden können");
        
        for (Action action : actions) {
            assertNotNull(action);
            assertTrue(action instanceof Action);
        }
    }

    @Test
    @DisplayName("Test 11: Action Switch-Expression")
    void test_11_actionSwitchExpression() {
        Action putAction = new PutAction();
        Action takeAction = new TakeAction();
        Action kickAction = new KickAction(Direction.south);
        
        String putType = getActionType(putAction);
        String takeType = getActionType(takeAction);
        String kickType = getActionType(kickAction);
        
        assertEquals("PUT", putType);
        assertEquals("TAKE", takeType);
        assertEquals("KICK", kickType);
    }
    
    private String getActionType(Action action) {
        return switch (action) {
            case GoAction g -> "GO";
            case FinishAction f -> "FINISH";
            case PositionAction p -> "POSITION";
            case TakeAction t -> "TAKE";
            case PutAction p -> "PUT";
            case KickAction k -> "KICK";
        };
    }

    // ==========================================
    // GoAction Tests (zur Vollständigkeit)
    // ==========================================

    @Test
    @DisplayName("Test 12: GoAction speichert Richtung")
    void test_12_goActionStoresDirection() {
        GoAction goAction = new GoAction(Direction.west);
        
        assertEquals(Direction.west, goAction.getDirection());
        assertTrue(goAction instanceof Action);
    }

    // ==========================================
    // FinishAction und PositionAction Tests
    // ==========================================

    @Test
    @DisplayName("Test 13: FinishAction ist eine Action")
    void test_13_finishActionIsAction() {
        FinishAction finishAction = new FinishAction();
        
        assertTrue(finishAction instanceof Action);
        assertNotNull(finishAction);
    }

    @Test
    @DisplayName("Test 14: PositionAction ist eine Action")
    void test_14_positionActionIsAction() {
        PositionAction positionAction = new PositionAction();
        
        assertTrue(positionAction instanceof Action);
        assertNotNull(positionAction);
    }
}
