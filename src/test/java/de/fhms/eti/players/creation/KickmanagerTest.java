package de.fhms.eti.players.creation;

import org.junit.jupiter.api.Test;

import de.fhms.eti.players.domain.Action;
import de.fhms.eti.players.domain.Direction;
import de.fhms.eti.players.domain.KickAction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für KickAction (Level 4 Funktionalität).
 * 
 * Testet die Kick-Aktion, mit der Formulare weggeschoben werden können.
 * Die KickAction speichert nur die Richtung, in die gekickt werden soll.
 */
class KickmanagerTest {

    /**
     * Test 1: KickAction mit Richtung NORD erstellen und abrufen.
     */
    @Test
    void test_01_kickAction_storesDirection_north() {
        // Eine KickAction mit Richtung Nord erstellen
        Direction expectedDirection = Direction.north;
        KickAction kickAction = new KickAction(expectedDirection);

        // Die Direction wieder abrufen
        Direction actualDirection = kickAction.getDirection();

        // Prüfen: Direction sollte korrekt gespeichert sein
        assertEquals(expectedDirection, actualDirection,
                "KickAction sollte Direction.north korrekt speichern");
        assertNotNull(actualDirection, "Direction sollte nicht null sein");
    }

    /**
     * Test 2: KickAction mit Richtung OST erstellen und abrufen.
     */
    @Test
    void test_02_kickAction_storesDirection_east() {
        // KickAction mit Richtung Ost erstellen
        Direction expectedDirection = Direction.east;
        KickAction kickAction = new KickAction(expectedDirection);

        // Direction abrufen und prüfen
        Direction actualDirection = kickAction.getDirection();
        assertEquals(expectedDirection, actualDirection,
                "KickAction sollte Direction.east korrekt speichern");
    }

    /**
     * Test 3: KickAction mit Richtung SÜD erstellen und abrufen.
     */
    @Test
    void test_03_kickAction_storesDirection_south() {
        // KickAction mit Richtung Süd erstellen
        Direction expectedDirection = Direction.south;
        KickAction kickAction = new KickAction(expectedDirection);

        // Direction abrufen und prüfen
        Direction actualDirection = kickAction.getDirection();
        assertEquals(expectedDirection, actualDirection,
                "KickAction sollte Direction.south korrekt speichern");
    }

    /**
     * Test 4: KickAction mit Richtung WEST erstellen und abrufen.
     */
    @Test
    void test_04_kickAction_storesDirection_west() {
        // KickAction mit Richtung West erstellen
        Direction expectedDirection = Direction.west;
        KickAction kickAction = new KickAction(expectedDirection);

        // Direction abrufen und prüfen
        Direction actualDirection = kickAction.getDirection();
        assertEquals(expectedDirection, actualDirection,
                "KickAction sollte Direction.west korrekt speichern");
    }

    /**
     * Test 5: Prüfen, ob KickAction korrekt von Action erbt.
     * 
     * Wichtig für polymorphe Verwendung im GameController.
     */
    @Test
    void test_05_kickAction_extendsAction() {
        // KickAction erstellen
        KickAction kickAction = new KickAction(Direction.north);

        // Vererbung prüfen
        assertTrue(kickAction instanceof Action,
                "KickAction muss von Action erben");
        assertNotNull(kickAction, "KickAction-Instanz darf nicht null sein");
    }

    /**
     * Test 6: Direction bleibt unveränderlich (Immutability).
     * 
     * Mehrfache Aufrufe von getDirection() sollten immer
     * dieselbe Direction zurückgeben.
     */
    @Test
    void test_06_kickAction_directionIsImmutable() {
        // KickAction erstellen
        Direction originalDirection = Direction.north;
        KickAction kickAction = new KickAction(originalDirection);

        // Direction mehrfach abrufen
        Direction firstCall = kickAction.getDirection();
        Direction secondCall = kickAction.getDirection();
        Direction thirdCall = kickAction.getDirection();

        // Alle Aufrufe sollten die gleiche Direction liefern
        assertEquals(originalDirection, firstCall,
                "Erste Abfrage sollte ursprüngliche Direction liefern");
        assertEquals(originalDirection, secondCall,
                "Zweite Abfrage sollte ursprüngliche Direction liefern");
        assertEquals(originalDirection, thirdCall,
                "Dritte Abfrage sollte ursprüngliche Direction liefern");
    }

    /**
     * Test 7: Verschiedene KickActions sind unabhängig voneinander.
     * 
     * Jede KickAction sollte ihre eigene Direction behalten.
     */
    @Test
    void test_07_multipleKickActions_areIndependent() {
        // Zwei verschiedene KickActions erstellen
        KickAction kickNorth = new KickAction(Direction.north);
        KickAction kickSouth = new KickAction(Direction.south);

        // Directions abrufen
        Direction directionNorth = kickNorth.getDirection();
        Direction directionSouth = kickSouth.getDirection();

        // Jede KickAction hat ihre eigene Direction
        assertEquals(Direction.north, directionNorth,
                "Erste KickAction sollte Direction.north haben");
        assertEquals(Direction.south, directionSouth,
                "Zweite KickAction sollte Direction.south haben");
        assertNotEquals(directionNorth, directionSouth,
                "Beide KickActions sollten unterschiedliche Directions haben");
    }

    /**
     * Test 8: Verhalten bei null-Direction (Grenzfall-Test).
     * 
     * Testet, was passiert wenn KickAction mit null erstellt wird.
     * Aktuell: null wird akzeptiert (keine Exception).
     */
    @Test
    void test_08_kickAction_withNullDirection() {
        // Versuch, KickAction mit null zu erstellen
        Direction nullDirection = null;
        KickAction kickAction = new KickAction(nullDirection);

        // getDirection() gibt null zurück
        assertNull(kickAction.getDirection(),
                "KickAction akzeptiert null als Direction (aktuelles Verhalten)");
        
        // Hinweis: In produktivem Code würde man hier wahrscheinlich
        // eine Exception erwarten. Wir dokumentieren aber das aktuelle Verhalten.
    }
}