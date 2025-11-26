package de.fhms.eti.players.Domain_Klassen;

/**
 * A finish cell of some player.
 */
final public class FinishCell extends Cell {
    private final int playerId;
    private final int formCount;

    public FinishCell(int playerId, int formCount) {
        this.playerId = playerId;
        this.formCount = formCount;
    }

    /**
     * The player ID associated with the finish.
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * The amount of forms required to collect before finishing.
     */
    public int getFormCount() {
        return formCount;
    }

    @Override
    public boolean isAccessable() {
        return true;
    }
}
