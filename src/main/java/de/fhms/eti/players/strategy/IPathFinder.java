package main.java.de.fhms.eti.players.strategy;

import main.java.de.fhms.eti.players.domain.Cell;
import main.java.de.fhms.eti.players.domain.GoAction;
import main.java.de.fhms.eti.players.domain.Position;

import java.util.Deque;

/**
 * Abstraktion für Pfadfindungs-Algorithmen.
 * <p>
 * Ermöglicht es Strategien, unterschiedliche Suchverfahren zu nutzen, ohne
 * konkrete Implementierungen zu kennen (DIP / OCP).
 */
public interface IPathFinder {
    /**
     * Aktualisiert das bekannte Umfeld. Muss pro Runde mit den sichtbaren Zellen
     * aufgerufen werden, damit der Suchalgorithmus eine Karte aufbauen kann.
     */
    void updateKnowledge(Position currentPos, Cell northCell, Cell eastCell,
                         Cell southCell, Cell westCell);

    /**
     * Berechnet den nächsten Pfad zwischen zwei Positionen.
     *
     * @return Queue aus Bewegungsbefehlen (kann leer sein wenn kein Pfad bekannt)
     */
    Deque<GoAction> buildPath(Position start, Position target, int myPlayerId);

    /**
     * Sucht nach dem nächsten bekannten Feld, das an unbekannte Nachbarn grenzt.
     * Dadurch kann die Strategie gezielt neue Bereiche erkunden, ohne in Schleifen
     * zu geraten.
     */
    Deque<GoAction> buildPathToFrontier(Position start, int myPlayerId);
}
