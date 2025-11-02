package de.fhms.eti.players;

/**
 * Interface für verschiedene Navigationsstrategien (ISP ) 
 * Ermöglicht verschiedene Algorithmen für die Navigation:
 * - SimpleNavigationStrategy (bevorzugt unbesuchte Felder)
 * - RightHandRuleStrategy (folgt der rechten Wand)
 * 
 * Erfüllt:
 * - OCP (Open/Closed): Neue Strategien ohne Änderung bestehenden Codes
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
}
