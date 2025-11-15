package de.fhms.eti.players.Kommunikations_Klassen;

import java.util.Scanner;

/**
 * GameCommunication verwaltet die Kommunikation mit der Spielumgebung
 * 
 * Verantwortung (SRP):
 * - Einlesen von Daten (System.in)
 * - Ausgeben von Befehlen (System.out)
 * - Parsen der Protokoll-Daten
 * 
 * Entscheidet nicht über Spiellogik (das machen andere Klassen)
 */
public class GameCommunication {
    private Scanner input;
    
    public GameCommunication() {
        this.input = new Scanner(System.in);
    }
    
    // INIT-Daten einlesen
    /**
     * Liest die INIT-"Phase" vom spielprotokoll
     * 
     * Protokoll:
     * Zeile 1: <sizeX> <sizeY> <level>
     * Zeile 2: <playerId> <startX> <startY>
     * 
     * @return InitData objekt mit allen Initialisierungsdaten
     */
    public InitData readInit() {
        // Zeile 1: Labyrinth Informationen
        int sizeX = input.nextInt();
        int sizeY = input.nextInt();
        int level = input.nextInt();
        input.nextLine();
        
        // Zeile 2: Spieler Informationen
        int playerId = input.nextInt();
        int startX = input.nextInt();
        int startY = input.nextInt();
        input.nextLine();
        
        return new InitData(sizeX, sizeY, level, playerId, startX, startY);
    }
    
    // Runden-Daten einlesen
    /**
     * Liest die daten einer einzelnen Runde (TURN)
     * 
     * Protokoll (6 Zeilen pro Runde):
     * Zeile 1: <lastActionResult>
     * Zeile 2: <currentCell>
     * Zeile 3: <northCell>
     * Zeile 4: <eastCell>
     * Zeile 5: <southCell>
     * Zeile 6: <westCell>
     * 
     * @return TurnData Objekt mit Rundendaten. null wenn Spiel vorbei
     */
    public TurnData readTurn() {
        if (!input.hasNext()) return null;
        
        String lastActionResult = input.nextLine();
        String currentCell = input.nextLine();
        String northCell = input.nextLine();
        String eastCell = input.nextLine();
        String southCell = input.nextLine();
        String westCell = input.nextLine();
        
        return new TurnData(lastActionResult, currentCell, northCell, eastCell, southCell, westCell);
    }
    
    // Aktion ausgeben
    /**
     * Sendet eine aktion zu die Spielumgebung
     * 
     * Mögliche Aktionen:
     * - "go north"
     * - "go south"
     * - "go east"
     * - "go west"
     * - "finish" (beim Zielerreichung)
     * 
     * @param action Der zu sendende Befehl
     */
    public void sendAction(String action) {
        System.out.println(action);
    }
    
    public void close() {
        input.close();
    }
}
