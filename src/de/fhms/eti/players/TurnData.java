package de.fhms.eti.players;

/**
 * Data Transfer Object (DTO) für Rundendaten
 * 
 * Speichert alle Informationen die der Bot in jede Runde erhält:
 * - Ergebnis der letzten Aktion
 * - Status der aktuellen Zelle
 * - Status der umgebenden Zellen (Nord, Ost, Süd, West)
 * 
 * Wird in jeder Runde neu erstellt.
 */
public class TurnData {
    public final String lastActionResult;// z.B. "ok", "failed", "invalid"
    public final String currentCell;// Status wo Bot steht: "FLOOR", "FINISH 0 1"
    public final String northCell;
    public final String eastCell;
    public final String southCell;
    public final String westCell;
    
    public TurnData(String lastActionResult, String currentCell, 
                   String northCell, String eastCell, String southCell, String westCell) {
        this.lastActionResult = lastActionResult;
        this.currentCell = currentCell;
        this.northCell = northCell;
        this.eastCell = eastCell;
        this.southCell = southCell;
        this.westCell = westCell;
    }
}