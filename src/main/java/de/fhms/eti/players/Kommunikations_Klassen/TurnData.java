package main.java.de.fhms.eti.players.Kommunikations_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.Cell;

/**
 * Data Transfer Object (DTO) für Rundendaten
 * <p>
 * Speichert alle Informationen die der Bot in jede Runde erhält:
 * - Ergebnis der letzten Aktion
 * - Status der aktuellen Zelle
 * - Status der umgebenden Zellen (Nord, Ost, Süd, West)
 * LEVEL 3 ERWEITERUNG:
 * - Speichert zusaetzlich RAW-Strings fuer Gegner-Erkennung
 * - Raw-Strings enthalten Gegner-Marker ("!")
 * <p>
 * Wird in jeder Runde neu erstellt.
 */
public class TurnData {
    public final String lastActionResult;// z.B. "ok", "failed", "invalid"
    public final Cell currentCell;// Status wo Bot steht: "FLOOR", "FINISH 0 1"
    public final Cell northCell;
    public final Cell eastCell;
    public final Cell southCell;
    public final Cell westCell;


    public TurnData(String lastActionResult, Cell currentCell, Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {
        this.lastActionResult = lastActionResult;
        this.currentCell = currentCell;
        this.northCell = northCell;
        this.eastCell = eastCell;
        this.southCell = southCell;
        this.westCell = westCell;
    }
}
