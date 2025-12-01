package main.java.de.fhms.eti.players.Strategie_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.Cell;
import main.java.de.fhms.eti.players.Domain_Klassen.FinishCell;
import main.java.de.fhms.eti.players.Domain_Klassen.GoAction;
import main.java.de.fhms.eti.players.Domain_Klassen.Position;

/**
 * Interface für verschiedene Navigationsstrategien (ISP )
 * Ermöglicht verschiedene Algorithmen für die Navigation:
 * - SimpleNavigationStrategy (bevorzugt unbesuchte Felder)
 * <p>
 * Erfüllt:
 * - OCP (Open/Closed): Neue Strategien ohne Ã„nderung bestehenden Codes
 * - LSP (Liskov): Alle Implementierungen sind austauschbar
 * - ISP: Nur eine Methode, die wirklich gebraucht wird
 */
public interface INavigationStrategy {
    /**
     * STATISCHE Methode: Prüft ob eine Zelle ein FINISH für einen anderen Spieler ist
     * Kann von allen Implementierungen verwendet werden ohne Instanz
     *
     * @param cell       Der Zellstatus-String
     * @param myPlayerId Die eigene Spieler-ID
     * @return true wenn die Zelle ein fremdes Ziel ist
     */
    static boolean isOtherPlayersFinish(Cell cell, int myPlayerId) {
        if (!(cell instanceof FinishCell finishCell)) {
            return false;
        }
        int foreignPlayerId = finishCell.getPlayerId();
        boolean isOtherPlayer = foreignPlayerId != myPlayerId;
        if (isOtherPlayer) {
            System.err.println("Ignoriere fremdes Ziel für Spieler " + foreignPlayerId);
        }
        return isOtherPlayer;
    }

    /**
     * Entscheidet die nächste Bewegung basierend auf der Umgebung(Zellen)
     *
     * @param currentPos Aktuelle Position vom Bot
     * @param northCell  Status der Zelle im Norden ("WALL", "FLOOR", "FINISH...")
     * @param eastCell   Status der Zelle im Osten
     * @param southCell  Status der Zelle im Süden
     * @param westCell   Status der Zelle im Westen
     * @return Befehl für die nächste Bewegung Z.B "go north"
     */
    GoAction decideNextMove(Position currentPos, Cell northCell, Cell eastCell,
                            Cell southCell, Cell westCell);

    /**
     * Setzt die Spieler-ID für diese Strategie
     * Wichtig für Multiplayer: Um fremde Ziele zu erkennen und zu ignorieren
     *
     * @param playerId Die ID dieses Bots
     */
    void setPlayerId(int playerId);
}
