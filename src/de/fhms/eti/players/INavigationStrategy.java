package de.fhms.eti.players;

/**
 * Interface fÃ¼r verschiedene Navigationsstrategien (ISP ) 
 * ErmÃ¶glicht verschiedene Algorithmen fÃ¼r die Navigation:
 * - SimpleNavigationStrategy (bevorzugt unbesuchte Felder)
 * - RightHandRuleStrategy (folgt der rechten Wand)
 * 
 * ErfÃ¼llt:
 * - OCP (Open/Closed): Neue Strategien ohne Ã„nderung bestehenden Codes
 * - LSP (Liskov): Alle Implementierungen sind austauschbar
 * - ISP: Nur eine Methode, die wirklich gebraucht wird
 */
public interface INavigationStrategy {
    /**
     * Entscheidet die nÃ¤chste Bewegung basierend auf der Umgebung(Zellen)
     * 
     * @param currentPos Aktuelle Position vom Bot
     * @param northCell Status der Zelle im Norden ("WALL", "FLOOR", "FINISH...")
     * @param eastCell Status der Zelle im Osten
     * @param southCell Status der Zelle im SÃ¼den
     * @param westCell Status der Zelle im Westen
     * @return Befehl fÃ¼r die nÃ¤chste Bewegung Z.B "go north"
     */
    String decideNextMove(Position currentPos, String northCell, String eastCell, 
                         String southCell, String westCell);
    
    /**
     * Setzt die Spieler-ID fÃ¼r diese Strategie
     * Wichtig fÃ¼r Multiplayer: Um fremde Ziele zu erkennen und zu ignorieren
     * 
     * @param playerId Die ID dieses Bots
     */
    void setPlayerId(int playerId);



    /**
     *STATISCHE Methode: PrÃ¼ft ob eine Zelle ein FINISH fÃ¼r einen anderen Spieler ist
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
                    System.err.println("Ignoriere fremdes Ziel fÃ¼r Spieler " + finishPlayerId);
                }
                return isOtherPlayer;
            }
        } catch (NumberFormatException e) {
            // Ignorieren falls Parsing fehlschlÃ¤gt
        }
        
        return false;
    }
}
