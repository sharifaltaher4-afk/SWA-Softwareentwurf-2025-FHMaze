package de.fhms.eti.players.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für Position (Record-Klasse).
 * 
 * Testet die Position-Klasse, die einen Punkt im Labyrinth repräsentiert.
 * Position ist unveränderlich (immutable) und wird als Value Object verwendet.
 * 
 * Koordinatensystem:
 * - X-Achse: horizontal (positiv = rechts/Ost)
 * - Y-Achse: vertikal (positiv = unten/Süd)
 * - Nord: y-1, Süd: y+1, Ost: x+1, West: x-1
 */
class PositionTest {

    /**
     * Test 1: Position mit positiven Koordinaten erstellen.
     */
    @Test
    void test_01_position_creation_positiveCoordinates() {
        // Position erstellen
        int x = 5;
        int y = 10;
        Position pos = new Position(x, y);

        // Koordinaten sollten korrekt gespeichert sein
        assertEquals(x, pos.x(), "X-Koordinate sollte 5 sein");
        assertEquals(y, pos.y(), "Y-Koordinate sollte 10 sein");
        assertNotNull(pos, "Position sollte nicht null sein");
    }

    /**
     * Test 2: Position mit negativen Koordinaten erstellen.
     * 
     * Negative Koordinaten sind erlaubt (für relative Berechnungen).
     */
    @Test
    void test_02_position_creation_negativeCoordinates() {
        // Position mit negativen Werten
        int x = -3;
        int y = -7;
        Position pos = new Position(x, y);

        // Negative Werte sollten akzeptiert werden
        assertEquals(x, pos.x(), "X-Koordinate sollte -3 sein");
        assertEquals(y, pos.y(), "Y-Koordinate sollte -7 sein");
    }

    /**
     * Test 3: Ursprungsposition (0,0) erstellen.
     * 
     * Die Position (0,0) wird oft als Startpunkt verwendet.
     */
    @Test
    void test_03_position_creation_origin() {
        // Ursprungsposition erstellen
        Position origin = new Position(0, 0);

        // Beide Koordinaten sollten 0 sein
        assertEquals(0, origin.x(), "X sollte 0 sein");
        assertEquals(0, origin.y(), "Y sollte 0 sein");
    }

    /**
     * Test 4: Bewegung nach Norden.
     * 
     * Norden = y wird um 1 reduziert.
     * Original-Position bleibt unverändert (Immutability).
     */
    @Test
    void test_04_move_north() {
        // Start-Position
        Position start = new Position(5, 5);

        // Nach Norden bewegen
        Position result = start.move(Direction.north);

        // Y sollte dekrementiert sein, X unverändert
        assertEquals(5, result.x(), "X sollte unverändert bleiben");
        assertEquals(4, result.y(), "Y sollte um 1 reduziert werden (5-1=4)");

        // Original-Position sollte unverändert sein
        assertEquals(5, start.x(), "Original X unverändert");
        assertEquals(5, start.y(), "Original Y unverändert");
    }

    /**
     * Test 5: Bewegung nach Süden.
     * 
     * Süden = y wird um 1 erhöht.
     */
    @Test
    void test_05_move_south() {
        // Start-Position
        Position start = new Position(5, 5);

        // Nach Süden bewegen
        Position result = start.move(Direction.south);

        // Y sollte inkrementiert sein
        assertEquals(5, result.x(), "X sollte unverändert bleiben");
        assertEquals(6, result.y(), "Y sollte um 1 erhöht werden (5+1=6)");
    }

    /**
     * Test 6: Bewegung nach Osten.
     * 
     * Osten = x wird um 1 erhöht.
     */
    @Test
    void test_06_move_east() {
        // Start-Position
        Position start = new Position(5, 5);

        // Nach Osten bewegen
        Position result = start.move(Direction.east);

        // X sollte inkrementiert sein
        assertEquals(6, result.x(), "X sollte um 1 erhöht werden (5+1=6)");
        assertEquals(5, result.y(), "Y sollte unverändert bleiben");
    }

    /**
     * Test 7: Bewegung nach Westen.
     * 
     * Westen = x wird um 1 reduziert.
     */
    @Test
    void test_07_move_west() {
        // Start-Position
        Position start = new Position(5, 5);

        // Nach Westen bewegen
        Position result = start.move(Direction.west);

        // X sollte dekrementiert sein
        assertEquals(4, result.x(), "X sollte um 1 reduziert werden (5-1=4)");
        assertEquals(5, result.y(), "Y sollte unverändert bleiben");
    }

    /**
     * Test 8: Mehrfache Bewegungen verketten.
     * 
     * move() kann mehrfach hintereinander aufgerufen werden,
     * um komplexe Pfade zu berechnen.
     */
    @Test
    void test_08_move_chaining() {
        // Start am Ursprung
        Position start = new Position(0, 0);

        // Mehrfache Bewegungen verketten: erst Nord, dann Ost
        Position result = start
                .move(Direction.north)  // (0, -1)
                .move(Direction.east);  // (1, -1)

        // Finale Position prüfen
        assertEquals(1, result.x(), "X sollte 1 sein nach Ost");
        assertEquals(-1, result.y(), "Y sollte -1 sein nach Nord");
    }

    /**
     * Test 9: Equals-Methode mit identischen Positionen.
     * 
     * Zwei Positionen mit gleichen Koordinaten sollten
     * als gleich erkannt werden (Value Object Semantik).
     */
    @Test
    void test_09_equals_identicalPositions() {
        // Zwei Positionen mit gleichen Koordinaten
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);

        // Sollten gleich sein
        assertEquals(pos1, pos2,
                "Positionen mit gleichen Koordinaten sollten gleich sein");
        assertTrue(pos1.equals(pos2), "equals() sollte true zurückgeben");
        assertTrue(pos2.equals(pos1), "equals() sollte symmetrisch sein");
    }

    /**
     * Test 10: Equals-Methode mit unterschiedlichen Positionen.
     * 
     * Positionen mit verschiedenen Koordinaten sollten
     * als ungleich erkannt werden.
     */
    @Test
    void test_10_equals_differentPositions() {
        // Drei verschiedene Positionen
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 6);  // Anderes Y
        Position pos3 = new Position(6, 5);  // Anderes X

        // Sollten NICHT gleich sein
        assertNotEquals(pos1, pos2,
                "Positionen mit unterschiedlichem Y sollten ungleich sein");
        assertNotEquals(pos1, pos3,
                "Positionen mit unterschiedlichem X sollten ungleich sein");
    }

    /**
     * Test 11: HashCode-Konsistenz.
     * 
     * Gleiche Positionen müssen denselben HashCode haben.
     * Wichtig für HashMaps und HashSets.
     * 
     * Java Contract: Wenn a.equals(b), dann muss a.hashCode() == b.hashCode()
     */
    @Test
    void test_11_hashCode_consistency() {
        // Zwei gleiche Positionen
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);

        // HashCode vergleichen
        int hash1 = pos1.hashCode();
        int hash2 = pos2.hashCode();

        // Gleiche Objekte müssen gleichen HashCode haben
        assertEquals(hash1, hash2,
                "Gleiche Positionen müssen gleichen HashCode haben");
    }

    /**
     * Test 12: Position in HashSet verwenden.
     * 
     * Testet, dass Position korrekt in HashSet funktioniert.
     * Duplikate sollten automatisch erkannt werden.
     * 
     * Anwendungsfall: Visited-Set in BFS-Algorithmus.
     */
    @Test
    void test_12_position_inHashSet() {
        // HashSet erstellen
        java.util.Set<Position> visited = new java.util.HashSet<>();
        
        // Positionen erstellen
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);  // Duplikat von pos1
        Position pos3 = new Position(6, 6);  // Unterschiedlich

        // Zum Set hinzufügen
        visited.add(pos1);
        visited.add(pos2);  // Sollte nicht hinzugefügt werden (Duplikat)
        visited.add(pos3);

        // Set sollte nur 2 eindeutige Positionen enthalten
        assertEquals(2, visited.size(),
                "HashSet sollte nur 2 eindeutige Positionen enthalten");
        assertTrue(visited.contains(pos1), "Set sollte pos1 enthalten");
        assertTrue(visited.contains(pos2), "Set sollte pos2 enthalten (gleich wie pos1)");
        assertTrue(visited.contains(pos3), "Set sollte pos3 enthalten");
    }

    /**
     * Test 13: ToString-Methode für Debugging.
     * 
     * toString() sollte eine lesbare String-Repräsentation
     * im Format "(x,y)" liefern.
     */
    @Test
    void test_13_toString_readableFormat() {
        // Position erstellen
        Position pos = new Position(5, 10);

        // toString() aufrufen
        String str = pos.toString();

        // Format sollte "(5,10)" sein
        assertEquals("(5,10)", str, "toString() sollte Format '(x,y)' haben");
        assertTrue(str.contains("5"), "String sollte X-Koordinate enthalten");
        assertTrue(str.contains("10"), "String sollte Y-Koordinate enthalten");
    }

    /**
     * Test 14: Immutability (Unveränderlichkeit).
     * 
     * Position sollte nach Erstellung nicht verändert werden können.
     * move() gibt eine NEUE Position zurück, die Original bleibt unverändert.
     * 
     * Vorteil: Thread-Sicherheit, keine Seiteneffekte.
     */
    @Test
    void test_14_immutability() {
        // Original-Position
        Position original = new Position(5, 5);
        int originalX = original.x();
        int originalY = original.y();

        // Mehrere Bewegungen durchführen
        Position moved1 = original.move(Direction.north);
        Position moved2 = original.move(Direction.east);
        Position moved3 = original.move(Direction.south);
        Position moved4 = original.move(Direction.west);

        // Original-Position sollte UNVERÄNDERT sein
        assertEquals(originalX, original.x(),
                "Original X sollte unverändert sein");
        assertEquals(originalY, original.y(),
                "Original Y sollte unverändert sein");

        // Alle Bewegungen sollten NEUE Objekte zurückgeben
        assertNotSame(original, moved1,
                "move() sollte neue Position-Instanz erstellen");
        assertNotSame(original, moved2,
                "move() sollte neue Position-Instanz erstellen");
    }

    /**
     * Test 15: Hin-und-zurück Bewegung.
     * 
     * Entgegengesetzte Bewegungen sollten sich aufheben
     * und zur Ausgangsposition zurückführen.
     */
    @Test
    void test_15_move_backAndForth() {
        // Start-Position
        Position start = new Position(5, 5);

        // Nord dann Süd (sollten sich aufheben)
        Position afterNorthSouth = start
                .move(Direction.north)
                .move(Direction.south);

        // Ost dann West (sollten sich aufheben)
        Position afterEastWest = start
                .move(Direction.east)
                .move(Direction.west);

        // Sollten zurück bei Start-Position sein
        assertEquals(start, afterNorthSouth,
                "Nord-Süd-Bewegung sollte zur Startposition zurückführen");
        assertEquals(start, afterEastWest,
                "Ost-West-Bewegung sollte zur Startposition zurückführen");
    }
}