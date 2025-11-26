package de.fhms.eti.players.Domain_Klassen;

final public class WallCell extends Cell {
    @Override
    public boolean isAccessable() {
        return false;
    }
}
