package de.fhms.eti.players.domain;

/**
 * A singular cell on a field.
 * LEVEL 3 ERWEITERUNG:
 * - hasOpponent: Gegner auf diesem Feld?
 * - opponentDistance: Wie weit ist der Gegner entfernt?
 */
abstract sealed public class Cell permits WallCell, FinishCell, FormCell, FloorCell {
    // LEVEL 3: Gegner-Informationen
    private final boolean hasOpponent;
    private final int opponentDistance; // 0 = direkt hier, 1+ = Felder entfernt in dieser Richtung

    /**
     * Konstruktor mit Gegner-Informationen (Level 3)
     */
    protected Cell(boolean hasOpponent, int opponentDistance) {
        this.hasOpponent = hasOpponent;
        this.opponentDistance = opponentDistance;
    }

    /**
     * Default-Konstruktor (kein Gegner)
     */
    protected Cell() {
        this(false, -1);
    }

    /**
     * Returns whether a player can access (walk) on the cell.
     *
     * @return `true` if accessable else `false`.
     */
    abstract public boolean isAccessable();

    // LEVEL 3: Getter
    public boolean hasOpponent() {
        return hasOpponent;
    }

    public int getOpponentDistance() {
        return opponentDistance;
    }
}
