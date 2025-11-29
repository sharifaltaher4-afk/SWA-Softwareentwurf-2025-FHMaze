package de.fhms.eti.players.Domain_Klassen;

/**
 * A finish cell of some player.
 * LEVEL 3: Kann auch Gegner Info haben
 */
final public class FinishCell extends Cell {
    private final int playerId;
    private final int formCount;

    /**
     * Default-Konstruktor (kein Gegner)
     */
    public FinishCell(int playerId, int formCount) {
        super();
        this.playerId = playerId;
        this.formCount = formCount;
    }

    /**
     * LEVEL 3: Konstruktor mit Gegner Informationen
     */
    public FinishCell(int playerId, int formCount, boolean hasOpponent, int opponentDistance) {
        super(hasOpponent, opponentDistance);
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
