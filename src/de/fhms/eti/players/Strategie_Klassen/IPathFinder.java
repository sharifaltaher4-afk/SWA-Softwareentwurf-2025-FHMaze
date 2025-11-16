package de.fhms.eti.players.Strategie_Klassen;

import java.util.Deque;

import de.fhms.eti.players.Domain_Klassen.Position;

/**
 * Abstraktion f&uuml;r Pfadfindungs-Algorithmen.
 * <p>
 * Erm&ouml;glicht es Strategien, unterschiedliche Suchverfahren zu nutzen, ohne
 * konkrete Implementierungen zu kennen (DIP / OCP).
 */
public interface IPathFinder {
    /**
     * Aktualisiert das bekannte Umfeld. Muss pro Runde mit den sichtbaren Zellen
     * aufgerufen werden, damit der Suchalgorithmus eine Karte aufbauen kann.
     */
    void updateKnowledge(Position currentPos, String northCell, String eastCell,
                         String southCell, String westCell);

    /**
     * Berechnet den n&auml;chsten Pfad zwischen zwei Positionen.
     *
     * @return Queue aus Bewegungsbefehlen (kann leer sein wenn kein Pfad bekannt)
     */
    Deque<String> buildPath(Position start, Position target, int myPlayerId);

    /**
     * Sucht nach dem n&auml;chsten bekannten Feld, das an unbekannte Nachbarn grenzt.
     * Dadurch kann die Strategie gezielt neue Bereiche erkunden, ohne in Schleifen
     * zu geraten.
     */
    Deque<String> buildPathToFrontier(Position start, int myPlayerId);
}
