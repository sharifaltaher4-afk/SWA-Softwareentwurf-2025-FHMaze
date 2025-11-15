package de.fhms.eti.players.Strategie_Klassen;

import de.fhms.eti.players.Domain_Klassen.Position;

/**
 * Interface für verschiedene Navigationsstrategien (ISP ) 
 * Ermöglicht verschiedene Algorithmen für die Navigation:
 * - SimpleNavigationStrategy (bevorzugt unbesuchte Felder)
 * - RightHandRuleStrategy (folgt der rechten Wand)
 * 
 * Erfüllt:
 * - OCP (Open/Closed): Neue Strategien ohne Ã„nderung bestehenden Codes
 * - LSP (Liskov): Alle Implementierungen sind austauschbar
 * - ISP: Nur eine Methode, die wirklich gebraucht wird
 */
public interface INavigationStrategy {
    /**
     * Entscheidet die nächste Bewegung basierend auf der Umgebung(Zellen)
     * 
     * @param currentPos Aktuelle Position vom Bot
     * @param northCell Status der Zelle im Norden ("WALL", "FLOOR", "FINISH...")
     * @param eastCell Status der Zelle im Osten
     * @param southCell Status der Zelle im Süden
     * @param westCell Status der Zelle im Westen
     * @return Befehl für die nächste Bewegung Z.B "go north"
     */
    String decideNextMove(Position currentPos, String northCell, String eastCell, 
                         String southCell, String westCell);
    
    /**
     * Setzt die Spieler-ID für diese Strategie
     * Wichtig für Multiplayer: Um fremde Ziele zu erkennen und zu ignorieren
     * 
     * @param playerId Die ID dieses Bots
     */
    void setPlayerId(int playerId);



    /**
     *STATISCHE Methode: Prüft ob eine Zelle ein FINISH für einen anderen Spieler ist
     * Kann von allen Implementierungen verwendet werden ohne Instanz
     * 
     * @param cellStatus Der Zellstatus-String
     * @param myPlayerId Die eigene Spieler-ID
     * @return true wenn die Zelle ein fremdes Ziel ist
     */
    static boolean isOtherPlayersFinish(String cellStatus, int myPlayerId) {
        if (cellStatus == null || !cellStatus.startsWith("FINISH")) {
            return false;  // Kein FINISH-String
        }
        
        try {
            String[] parts = cellStatus.split(" ");
            if (parts.length >= 2) {
                int finishPlayerId = Integer.parseInt(parts[1]);
                boolean isOtherPlayer = (finishPlayerId != myPlayerId);
                if (isOtherPlayer) {
                    System.err.println("Ignoriere fremdes Ziel für Spieler " + finishPlayerId);
                }
                return isOtherPlayer;
            }
        } catch (NumberFormatException e) {
            // Ignorieren falls Parsing fehlschlägt
        }
        
        return false;
    }
}
