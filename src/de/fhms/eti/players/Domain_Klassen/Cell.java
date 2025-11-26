package de.fhms.eti.players.Domain_Klassen;

/**
 * A singular cell on a field.
 */
abstract sealed public class Cell permits WallCell, FinishCell, FormCell, FloorCell {
    /**
     * Returns whether a player can access (walk) on the cell.
     * @return `true` if accessable else `false`.
     */
    abstract public boolean isAccessable();
}
