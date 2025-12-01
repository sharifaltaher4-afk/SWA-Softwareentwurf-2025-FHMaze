package test.java.de.fhms.eti.players.Erzeugungsmuster_Klassen;

import main.java.de.fhms.eti.players.Erzeugungsmuster_Klassen.GameController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class GameControllerTest {

    @Test
    void test_01_resetCreatesNewInstance() {
        GameController first = GameController.getInstance();
        assertNotNull(first);

        GameController.reset();
        GameController second = GameController.getInstance();

        assertNotNull(second);
        assertNotSame(first, second);
    }

    @Test
    void test_02_getInstanceReturnsSameUntilReset() {
        GameController first = GameController.getInstance();
        GameController again = GameController.getInstance();
        assertNotNull(first);
        assertNotNull(again);
        // Without reset, the singleton reference should be identical
        assertSame(first, again);
    }

    @Test
    void test_03_multipleResetsStillProduceInstances() {
        GameController.reset();
        GameController a = GameController.getInstance();
        GameController.reset();
        GameController b = GameController.getInstance();

        assertNotNull(a);
        assertNotNull(b);
        assertNotSame(a, b);
    }
}
