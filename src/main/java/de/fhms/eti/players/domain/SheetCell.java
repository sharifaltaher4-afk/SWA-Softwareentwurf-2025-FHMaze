package de.fhms.eti.players.domain;

/**
 * Cell mit Blatt (Level 5).
 */
final public class SheetCell extends Cell {
    public SheetCell(boolean hasOpponent, int opponentDistance) {
        super(hasOpponent, opponentDistance);
    }

    public SheetCell() {
        super();
    }

    @Override
    public boolean isAccessable() {
        return true;
    }
}
