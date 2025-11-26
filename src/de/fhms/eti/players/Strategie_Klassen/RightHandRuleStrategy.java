package de.fhms.eti.players.Strategie_Klassen;

import de.fhms.eti.players.Domain_Klassen.*;

import java.nio.file.DirectoryStream;

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
    private Direction lastDirection;
    private int myPlayerId;

    public RightHandRuleStrategy() {
        this.lastDirection = Direction.north; // Standardrichtung beim Start
        this.myPlayerId = -1;
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
    public GoAction decideNextMove(Position currentPos, Cell northCell, Cell eastCell,
                                   Cell southCell, Cell westCell) {
        // Rechte-Hand-Regel implementieren
        // Hole die Richtungen in rechte Hand Priorität(abhängig von der letzten Richtung)
        Direction[] directions = getDirectionsInRightHandOrder(lastDirection);

        // Hole die zugehörigen Zellstatus in der gleichen Reihenfolge
        Cell[] cells = {getCellInDirection(directions[0], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[1], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[2], northCell, eastCell, southCell, westCell),
                         getCellInDirection(directions[3], northCell, eastCell, southCell, westCell)
                        };
        // Wählle die erste freie Richtung (in rechte Hand Priorität)
        for (int i = 0; i < 4; i++) {
            if (INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                continue;  // äberspringe fremde Ziele
            }
            if (!(cells[i] instanceof WallCell)) {
                lastDirection = directions[i]; // Merke diese Richtung
                return new GoAction(directions[i]);
            }
        }
        // Fallback
        for (int i = 0; i < 4; i++) {
            if (!(cells[i] instanceof WallCell)) {
                lastDirection = directions[i];
                return new GoAction(directions[i]);
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
    private Direction[] getDirectionsInRightHandOrder(Direction currentDirection) {
        return switch (currentDirection) {
            // Ging schon nach Norden ==> Rechts ist Osten
            case Direction.north -> new Direction[]{Direction.east, Direction.north, Direction.west, Direction.south};
            // Ging schoni nach Osten ==> Rechts ist Süden
            case Direction.east -> new Direction[]{Direction.south, Direction.east, Direction.north, Direction.west};
            // Ging schon nach Süden ==> Rechts ist Westen
            case Direction.south -> new Direction[]{Direction.west, Direction.south, Direction.east, Direction.north};
            // Ging nach Westen ==> Rechts ist Norden
            case Direction.west -> new Direction[]{Direction.north, Direction.west, Direction.south, Direction.east};
        };
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
    private Cell getCellInDirection(Direction direction, Cell north, Cell east, Cell south, Cell west) {
        return switch (direction) {
            case Direction.north -> north;
            case Direction.east -> east;
            case Direction.south -> south;
            case Direction.west -> west;
        };
    }

    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
    }
}