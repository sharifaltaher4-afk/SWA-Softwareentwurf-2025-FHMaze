package de.fhms.eti.players;


/**
 * RightHandRuleStrategy folgt immer der rechten Wand
 * 
 * Implementiert INavigationStrategy (LSP - austauschbar mit SimpleNavigationStrategy!)
 * 
 * Algorithmus "Rechte-Hand-Regel":
 * 1. Merke dir die letzte Bewegungsrichtung
 * 2. Versuche nach rechts zu drehen
 * 3. Falls rechts Wand: Gehe geradeaus
 * 4. Falls auch vorne Wand: Drehe links
 * 5. Falls alle Seiten Wand: Drehe um
 * 
 * Garantiert: Findet IMMER einen Ausgang 
 * Nachteil: Nicht der kürzeste Weg
 */
public class RightHandRuleStrategy implements INavigationStrategy {
    private String lastDirection;
    
    public RightHandRuleStrategy() {
        this.lastDirection = "north"; // Standardrichtung beim Start
    }
    
    /**
     * Entscheidet die nächste Bewegung nach Rechte-Hand-Regel
     * 
     * Implementiert INavigationStrategy.decideNextMove()
     * 
     * @param currentPos Aktuelle Position (wird hier nicht genutzt)
     * @param northCell Status nördliche Zelle
     * @param eastCell Status östliche Zelle
     * @param southCell Status südliche Zelle
     * @param westCell Status westliche Zelle
     * @return Befehl für nächste Bewegung
     */
    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, 
                                String southCell, String westCell) {
        // Rechte-Hand-Regel implementieren
        // Hole die Richtungen in rechte Hand Priorität(abhängig von der letzten Richtung)
        String[] directions = getDirectionsInRightHandOrder(lastDirection);

        // Hole die zugehörigen Zellstatus in der gleichen Reihenfolge
        String[] cells = {getCellInDirection(directions[0], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[1], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[2], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[3], northCell, eastCell, southCell, westCell)
                        };
        // Wähle die erste freie Richtung (in rechte Hand Priorität)
        for (int i = 0; i < 4; i++) {
            if (!cells[i].equals("WALL")) {
                lastDirection = directions[i]; // Merke diese Richtung
                return "go " + directions[i];
            }
        }
        // Sollte eigentlich nie passieren weil immer mindestens eine freie Richtung 
        return null;
    }
    
    /**
     * Gibt die Richtungen in echte Hand Priorität zurück
     * 
     * Logik:
     * - Wenn ich nach Norden schon ging: Versuche Ost, Nord, West, Süd (rechts, gerade, links, zurück)
     * - Wenn ich nach Osten schon ging: Versuche Süd, Ost, Nord, West
     * - usw.
     * 
     * @param currentDirection Aktuelle/letzte Richtung
     * @return Array mit 4 Richtungen in Prioritätsreihenfolge
     */
    private String[] getDirectionsInRightHandOrder(String currentDirection) {
        switch(currentDirection) {
            // Ging schon nach Norden ==> Rechts ist Osten 
            case "north": return new String[]{"east", "north", "west", "south"};
            // Ging schoni nach Osten ==> Rechts ist Süden
            case "east":  return new String[]{"south", "east", "north", "west"};
            // Ging schon nach Süden ==> Rechts ist Westen
            case "south": return new String[]{"west", "south", "east", "north"};
            // Ging nach Westen ==> Rechts ist Norden
            case "west":  return new String[]{"north", "west", "south", "east"};
            // Fallback: Normale Reihenfolge
            default: return new String[]{"north", "east", "south", "west"};
        }
    }
    
    /**
     * gibt den Zellstatus für eine bestimmte richtung zurück
     * 
     * Hilfsmethod um aus den vier Zellstatus den passenden auszuwählen
     * 
     * @param direction Gewünschte Richtung
     * @param north Status Nord
     * @param east Status Ost
     * @param south Status Süd
     * @param west Status West
     * @return Zellstatus in der gewünschten Richtung
     */
    private String getCellInDirection(String direction, String north, String east, String south, String west) {
        switch(direction) {
            case "north": return north;
            case "east": return east;
            case "south": return south;
            case "west": return west;
            default: return "WALL";
        }
    }
}