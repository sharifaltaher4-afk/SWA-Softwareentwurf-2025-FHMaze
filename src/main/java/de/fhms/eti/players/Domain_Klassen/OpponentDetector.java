package main.java.de.fhms.eti.players.Domain_Klassen;

import java.util.HashMap;
import java.util.Map;

/**
 * OpponentDetector - Erkennt und verwaltet Gegnerpositionen für Level 3
 * 
 * Verantwortung (SRP):
 * - Parst Gegner-Marker ("!") aus Zellstatus
 * - Speichert erkannte Gegner-IDs und Distanzen
 * - Bietet Abfragemethoden für Deadlock-Vermeidung
 * 
 * Level 3 Protokoll:
 * - "FLOOR !" = Gegner auf diesem Feld
 * - "FLOOR !2" = Gegner 2 Felder entfernt in dieser Richtung
 * - "FORM 0 1 !3" = Formular + Gegner 3 Felder entfernt
 */
public class OpponentDetector {
    // Speichert für jede Richtung: Gegner vorhanden + Distanz
    private Map<String, OpponentInfo> opponentsByDirection;
    
    // Gegner auf aktuellem Feld?
    private boolean opponentOnCurrentField;
    
    /**
     * Konstruktor
     * @param myPlayerId Eigene Spieler-ID
     */
    public OpponentDetector(int myPlayerId) {
        //this.myPlayerId = myPlayerId;
        this.opponentsByDirection = new HashMap<>();
        this.opponentOnCurrentField = false;
    }
    
    /**
     * Scannt alle Nachbarzellen auf Gegner
     * WICHTIG: Muss in JEDER Runde aufgerufen werden!
     * 
     * @param currentCell Status des aktuellen Feldes
     * @param north Status Nord-Nachbar
     * @param east Status Ost-Nachbar  
     * @param south Status Süd-Nachbar
     * @param west Status West-Nachbar
     */
    public void scanForOpponents(String currentCell, String north, String east, 
                                 String south, String west) {
        // Reset für neue Runde
        opponentsByDirection.clear();
        opponentOnCurrentField = false;
        
        // Aktuelles Feld prüfen
        if (hasOpponentMarker(currentCell)) {
            opponentOnCurrentField = true;
        }
        
        // Alle Richtungen scannen
        scanDirection("north", north);
        scanDirection("east", east);
        scanDirection("south", south);
        scanDirection("west", west);
    }
    
    /**
     * Scannt eine einzelne Richtung
     */
    private void scanDirection(String direction, String cellStatus) {
        if (cellStatus == null) return;
        
        if (hasOpponentMarker(cellStatus)) {
            OpponentInfo info = parseOpponentInfo(cellStatus);
            if (info != null) {
                opponentsByDirection.put(direction, info);
            }
        }
    }
    
    /**
     * Prüft ob Zellstatus einen Gegner-Marker enthält
     */
    private boolean hasOpponentMarker(String cellStatus) {
        return cellStatus != null && cellStatus.contains("!");
    }
    
    /**
     * Parst Gegner-Informationen aus Zellstatus
     * 
     * Beispiele:
     * - "FLOOR !" ==> OpponentInfo(distance=0, playerId=-1)
     * - "FLOOR !2" ==> OpponentInfo(distance=2, playerId=-1)
     * - "FORM 0 1 !3" ==> OpponentInfo(distance=3, playerId=-1)
     */
    private OpponentInfo parseOpponentInfo(String cellStatus) {
        try {
            // Finde "!" Position
            int exclIndex = cellStatus.indexOf("!");
            if (exclIndex == -1) return null;
            
            String afterExcl = cellStatus.substring(exclIndex + 1).trim();
            
            // Kein Gegner auf Nachbarfeld (nur Marker)
            if (afterExcl.isEmpty()) {
                return new OpponentInfo(0, -1);
            }
            
            // Parse Distanz
            int distance = Integer.parseInt(afterExcl);
            return new OpponentInfo(distance, -1);
            
        } catch (NumberFormatException e) {
            System.err.println(" Fehler beim Parsen von Gegner-Info: " + cellStatus);
            return null;
        }
    }
    
    /**
     * Prüft ob in einer Richtung ein Gegner ist
     * 
     * @param direction "north", "east", "south", "west"
     * @return true wenn Gegner in dieser Richtung
     */
    public boolean hasOpponentInDirection(String direction) {
        return opponentsByDirection.containsKey(direction);
    }
    
    /**
     * Gibt Distanz zum Gegner in einer Richtung zurück
     * 
     * @param direction "north", "east", "south", "west"
     * @return Distanz (0 = direkt benachbart, -1 = kein Gegner)
     */
    public int getOpponentDistance(String direction) {
        OpponentInfo info = opponentsByDirection.get(direction);
        return info != null ? info.distance : -1;
    }
    
    /**
     * Prüft ob Gegner auf aktuellem Feld ist
     */
    public boolean hasOpponentOnCurrentField() {
        return opponentOnCurrentField;
    }
    
    /**
     * Gibt Anzahl erkannter Gegner zurück
     */
    public int getOpponentCount() {
        return opponentsByDirection.size() + (opponentOnCurrentField ? 1 : 0);
    }
    
    /**
     * Entfernt Gegner-Marker aus Zellstatus
     * 
     * "FLOOR !2" ==> "FLOOR"
     * "FORM 0 1 !" ==> "FORM 0 1"
     * 
     * @param cellStatus Original-Status mit möglichem Marker
     * @return Bereinigter Status ohne Marker
     */
    public static String removeOpponentMarker(String cellStatus) {
        if (cellStatus == null) return null;
        
        int exclIndex = cellStatus.indexOf("!");
        if (exclIndex == -1) return cellStatus;
        
        // Alles vor "!" zurückgeben (und trimmen)
        return cellStatus.substring(0, exclIndex).trim();
    }
    
    /**
     * Debug-Ausgabe
     */
    public void printStatus() {
        System.err.println("  OpponentDetector Status:");
        System.err.println("   - Auf aktuellem Feld: " + opponentOnCurrentField);
        for (Map.Entry<String, OpponentInfo> entry : opponentsByDirection.entrySet()) {
            System.err.println("   - " + entry.getKey() + ": Distanz " + entry.getValue().distance);
        }
    }
    
    /**
     * Innere Klasse für Gegner-Informationen
     */
    private static class OpponentInfo {
        final int distance;
        //final int playerId;
        
        OpponentInfo(int distance, int playerId) {
            this.distance = distance;
            //this.playerId = playerId;
        }
    }
}
