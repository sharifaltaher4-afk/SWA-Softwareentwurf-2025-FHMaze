package de.fhms.eti.players.domain;

/**
 * LEVEL 3: WallCell kann auch Gegner Info haben
 * z.B. "WALL !2" = Wand, aber Gegner 2 Felder dahinter sichtbar
 */
final public class WallCell extends Cell {
    public WallCell() {
        super();
    }

    public WallCell(boolean hasOpponent, int opponentDistance) {
        super(hasOpponent, opponentDistance);
    }

    @Override
    public boolean isAccessable() {
        return false;
    }
}
