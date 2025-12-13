package de.fhms.eti.players.strategy;

import de.fhms.eti.players.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Deque;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für BFSPathFinderStrategy.
 * 
 * Testet die Breitensuche (BFS) zur Pfadfindung im Labyrinth.
 * BFS findet immer den kürzesten Weg zwischen zwei Punkten.
 */
class BFSPathFinderStrategyTest {

    private BFSPathFinderStrategy pathFinder;
    private static final int MY_PLAYER_ID = 0;

    /**
     * Vor jedem Test: Neue PathFinder-Instanz erstellen.
     * So beeinflussen sich Tests nicht gegenseitig.
     */
    @BeforeEach
    void setUp() {
        pathFinder = new BFSPathFinderStrategy();
    }

    /**
     * Test 1: Gerader Pfad ohne Hindernisse.
     * 
     * Von (0,0) nach (2,0) - einfach zwei Schritte nach Osten.
     */
    @Test
    void test_01_buildPath_straightLine_noObstacles() {
        // Start- und Zielposition
        Position start = new Position(0, 0);
        Position target = new Position(2, 0);

        // PathFinder mit Wissen über den Korridor füttern
        pathFinder.updateKnowledge(start,
                new WallCell(),     // Nord: Wand
                new FloorCell(),    // Ost: FREI
                new WallCell(),     // Süd: Wand
                new WallCell());    // West: Wand

        pathFinder.updateKnowledge(new Position(1, 0),
                new WallCell(),     // Nord: Wand
                new FloorCell(),    // Ost: FREI (Ziel!)
                new WallCell(),     // Süd: Wand
                new FloorCell());   // West: Zurück

        // Pfad berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, target, MY_PLAYER_ID);

        // Pfad sollte 2 Schritte nach Osten enthalten
        assertNotNull(path, "Pfad sollte nicht null sein");
        assertEquals(2, path.size(), "Pfad sollte 2 Schritte enthalten");
        assertEquals(Direction.east, path.pollFirst().getDirection(),
                "Erster Schritt sollte nach Osten gehen");
        assertEquals(Direction.east, path.pollFirst().getDirection(),
                "Zweiter Schritt sollte nach Osten gehen");
    }

    /**
     * Test 2: Pfad mit einer Kurve (L-Form).
     * 
     * Von (0,0) erst nach Osten zu (1,0), dann nach Süden zu (1,1).
     */
    @Test
    void test_02_buildPath_withTurn() {
        // Positionen definieren
        Position start = new Position(0, 0);
        Position corner = new Position(1, 0);
        Position target = new Position(1, 1);

        // Wissen über Start-Position
        pathFinder.updateKnowledge(start,
                new WallCell(),     // Nord: Wand
                new FloorCell(),    // Ost: FREI zur Ecke
                new WallCell(),     // Süd: Wand
                new WallCell());    // West: Wand

        // Wissen über Ecke
        pathFinder.updateKnowledge(corner,
                new WallCell(),     // Nord: Wand
                new WallCell(),     // Ost: Wand
                new FloorCell(),    // Süd: FREI zum Ziel
                new FloorCell());   // West: Zurück

        // Pfad berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, target, MY_PLAYER_ID);

        // Pfad sollte erst Ost, dann Süd gehen
        assertNotNull(path, "Pfad sollte nicht null sein");
        assertEquals(2, path.size(), "Pfad sollte 2 Schritte enthalten");
        assertEquals(Direction.east, path.pollFirst().getDirection(),
                "Erster Schritt: nach Osten zur Ecke");
        assertEquals(Direction.south, path.pollFirst().getDirection(),
                "Zweiter Schritt: nach Süden zum Ziel");
    }

    /**
     * Test 3: Kein Pfad möglich - Wand blockiert.
     * 
     * Wenn eine Wand zwischen Start und Ziel ist,
     * sollte ein leerer Pfad zurückkommen.
     */
    @Test
    void test_03_buildPath_noPathAvailable_wallBlocks() {
        // Wand blockiert den direkten Weg
        Position start = new Position(0, 0);
        Position target = new Position(2, 0);

        pathFinder.updateKnowledge(start,
                new WallCell(),     // Nord: Wand
                new WallCell(),     // Ost: BLOCKIERT!
                new WallCell(),     // Süd: Wand
                new WallCell());    // West: Wand

        // Pfad berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, target, MY_PLAYER_ID);

        // Kein Pfad möglich
        assertNotNull(path, "Pfad-Objekt sollte nicht null sein");
        assertTrue(path.isEmpty(),
                "Pfad sollte leer sein, wenn Ziel nicht erreichbar ist");
    }

    /**
     * Test 4: Null-Positionen führen zu leerem Pfad.
     * 
     * Testet robustes Fehlerhandling - keine Exception,
     * sondern einfach ein leerer Pfad.
     */
    @Test
    void test_04_buildPath_nullPositions_returnsEmptyPath() {
        Position validPos = new Position(0, 0);

        // Verschiedene null-Kombinationen testen
        Deque<GoAction> pathNullStart = pathFinder.buildPath(null, validPos, MY_PLAYER_ID);
        Deque<GoAction> pathNullTarget = pathFinder.buildPath(validPos, null, MY_PLAYER_ID);
        Deque<GoAction> pathBothNull = pathFinder.buildPath(null, null, MY_PLAYER_ID);

        // Alle sollten leere Pfade zurückgeben
        assertTrue(pathNullStart.isEmpty(), "Pfad sollte leer sein bei null Start");
        assertTrue(pathNullTarget.isEmpty(), "Pfad sollte leer sein bei null Target");
        assertTrue(pathBothNull.isEmpty(), "Pfad sollte leer sein bei beiden null");
    }

    /**
     * Test 5: Unbekanntes Ziel führt zu leerem Pfad.
     * 
     * Wenn das Ziel noch nicht erkundet wurde,
     * kann kein Pfad berechnet werden.
     */
    @Test
    void test_05_buildPath_unknownTarget_returnsEmptyPath() {
        // Start ist bekannt, Ziel nicht
        Position start = new Position(0, 0);
        Position unknownTarget = new Position(10, 10);

        pathFinder.updateKnowledge(start,
                new FloorCell(),
                new FloorCell(),
                new FloorCell(),
                new FloorCell());

        // Pfad zu unbekanntem Ziel berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, unknownTarget, MY_PLAYER_ID);

        // Kein Pfad möglich
        assertTrue(path.isEmpty(),
                "Pfad sollte leer sein für unbekanntes Ziel");
    }

    /**
     * Test 6: Pfad zur Frontier (unerforschte Grenze) finden.
     * 
     * BFS kann automatisch den nächsten unerforschten Bereich finden.
     * Wichtig für autonome Exploration.
     */
    @Test
    void test_06_buildPathToFrontier_findsUnexploredCell() {
        // Teilweise erkundeter Bereich
        Position start = new Position(0, 0);
        Position explored = new Position(1, 0);

        // Start komplett erkundet
        pathFinder.updateKnowledge(start,
                new WallCell(),     // Nord: bekannt
                new FloorCell(),    // Ost: bekannt
                new WallCell(),     // Süd: bekannt
                new WallCell());    // West: bekannt

        // Position (1,0) hat unbekannten Nachbarn nach Osten
        pathFinder.updateKnowledge(explored,
                new WallCell(),     // Nord: bekannt
                null,               // Ost: UNBEKANNT! <- Frontier
                new WallCell(),     // Süd: bekannt
                new FloorCell());   // West: bekannt

        // Pfad zur Frontier suchen
        Deque<GoAction> path = pathFinder.buildPathToFrontier(start, MY_PLAYER_ID);

        // Pfad sollte zu (1,0) führen (hat unbekannten Nachbarn)
        assertNotNull(path, "Pfad sollte nicht null sein");
        assertFalse(path.isEmpty(), "Pfad zur Frontier sollte existieren");
        assertEquals(Direction.east, path.peekFirst().getDirection(),
                "Pfad sollte nach Osten zur Frontier führen");
    }

    /**
     * Test 7: Keine Frontier mehr verfügbar.
     * 
     * Wenn alles erkundet ist, gibt es keinen Pfad zur Frontier.
     */
    @Test
    void test_07_buildPathToFrontier_noFrontier_returnsEmpty() {
        // Komplett erkundeter Raum (alle Nachbarn bekannt)
        Position start = new Position(0, 0);

        pathFinder.updateKnowledge(start,
                new WallCell(),     // Nord: bekannt
                new WallCell(),     // Ost: bekannt
                new WallCell(),     // Süd: bekannt
                new WallCell());    // West: bekannt

        // Frontier suchen
        Deque<GoAction> path = pathFinder.buildPathToFrontier(start, MY_PLAYER_ID);

        // Kein Pfad (keine unerforschten Bereiche)
        assertTrue(path.isEmpty(),
                "Pfad sollte leer sein wenn keine Frontier existiert");
    }

    /**
     * Test 8: Fremde Finish-Zellen werden als Wand behandelt.
     * 
     * Der Bot darf nur sein eigenes Finish betreten.
     * Fremde Finish-Zellen blockieren den Weg.
     */
    @Test
    void test_08_buildPath_respectsForeignFinish() {
        // Fremde Finish-Zelle blockiert direkten Weg
        Position start = new Position(0, 0);
        Position target = new Position(2, 0);
        int foreignPlayerId = 1; // Nicht MY_PLAYER_ID (0)

        pathFinder.updateKnowledge(start,
                new WallCell(),                         // Nord
                new FinishCell(foreignPlayerId, 3),     // Ost: FREMDES FINISH!
                new WallCell(),                         // Süd
                new WallCell());                        // West

        // Pfad berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, target, MY_PLAYER_ID);

        // Kein Pfad durch fremdes Finish möglich
        assertTrue(path.isEmpty(),
                "Pfad sollte leer sein, da fremdes Finish blockiert");
    }

    /**
     * Test 9: Eigene Finish-Zelle ist als Ziel erlaubt.
     * 
     * Der Bot darf sein eigenes Finish betreten und
     * einen Pfad dorthin berechnen.
     */
    @Test
    void test_09_buildPath_allowsOwnFinish() {
        // Eigene Finish-Zelle als Ziel
        Position start = new Position(0, 0);
        Position ownFinish = new Position(1, 0);

        pathFinder.updateKnowledge(start,
                new WallCell(),                      // Nord
                new FinishCell(MY_PLAYER_ID, 3),     // Ost: EIGENES FINISH!
                new WallCell(),                      // Süd
                new WallCell());                     // West

        // Pfad zum eigenen Finish berechnen
        Deque<GoAction> path = pathFinder.buildPath(start, ownFinish, MY_PLAYER_ID);

        // Pfad sollte existieren
        assertFalse(path.isEmpty(),
                "Pfad zum eigenen Finish sollte möglich sein");
        assertEquals(1, path.size(), "Pfad sollte 1 Schritt enthalten");
        assertEquals(Direction.east, path.peekFirst().getDirection(),
                "Pfad sollte nach Osten zum eigenen Finish führen");
    }

    /**
     * Test 10: Wissen akkumuliert über mehrere updateKnowledge-Aufrufe.
     * 
     * Der PathFinder sollte sich alle erkundeten Zellen merken
     * und das Wissen nicht überschreiben.
     */
    @Test
    void test_10_updateKnowledge_accumulatesInformation() {
        // Schrittweise Exploration
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 0);

        // Erste Position erkunden
        pathFinder.updateKnowledge(pos1,
                new WallCell(),
                new FloorCell(),
                new WallCell(),
                new WallCell());

        // Zweite Position erkunden
        pathFinder.updateKnowledge(pos2,
                new WallCell(),
                new FloorCell(),
                new WallCell(),
                new FloorCell());

        // Pfad von pos1 nach pos2 berechnen
        Deque<GoAction> path = pathFinder.buildPath(pos1, pos2, MY_PLAYER_ID);

        // Pfad sollte möglich sein (Wissen wurde gespeichert)
        assertFalse(path.isEmpty(),
                "Pfad sollte möglich sein - Wissen wurde akkumuliert");
        assertEquals(1, path.size(), "Pfad sollte 1 Schritt enthalten");
    }
}