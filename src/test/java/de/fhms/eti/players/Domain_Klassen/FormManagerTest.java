package test.java.de.fhms.eti.players.Domain_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.FormCell;
import main.java.de.fhms.eti.players.Domain_Klassen.FormManager;
import main.java.de.fhms.eti.players.Domain_Klassen.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormManagerTest {

    @Test
    void test_01_acceptsOwnFormInOrder() {
        FormManager manager = new FormManager(0);
        manager.setTotalFormsNeeded(2);

        FormCell formA = new FormCell(0, 1);
        assertTrue(manager.canTakeForm(formA));
        assertTrue(manager.takeForm(formA));
        assertEquals(2, manager.getNextFormId());
        assertEquals(1, manager.getCollectedForms().size());
        assertFalse(manager.hasAllForms()); // still need second form
    }

    @Test
    void test_02_rejectsWrongOrder() {
        FormManager manager = new FormManager(0);
        FormCell formB = new FormCell(0, 2);
        assertFalse(manager.canTakeForm(formB));
        assertFalse(manager.takeForm(formB));
        assertEquals(1, manager.getNextFormId()); // unchanged
    }

    @Test
    void test_03_rejectsForeignForm() {
        FormManager manager = new FormManager(0);
        FormCell foreign = new FormCell(1, 1);
        assertFalse(manager.canTakeForm(foreign));
    }

    @Test
    void test_04_hasAllFormsWhenCountKnown() {
        FormManager manager = new FormManager(0);
        manager.setTotalFormsNeeded(1);
        FormCell formA = new FormCell(0, 1);

        assertFalse(manager.hasAllForms());
        assertTrue(manager.takeForm(formA));
        assertTrue(manager.hasAllForms());
    }

    @Test
    void test_05_remembersFormPosition() {
        FormManager manager = new FormManager(0);
        Position pos = new Position(3, 4);
        manager.rememberFormPosition(1, pos);

        assertEquals(pos, manager.getRememberedFormPosition(1));
    }
}
