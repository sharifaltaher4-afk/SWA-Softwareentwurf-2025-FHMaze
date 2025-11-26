package de.fhms.eti.players.Domain_Klassen;

final public class FormCell extends Cell {
    private final int playerId;
    private final int formId;

    public FormCell(int playerId, int formId) {
        this.playerId = playerId;
        this.formId = formId;
    }

    /**
     * The ID of the player this form belongs to.
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * The ID of the form.
     */
    public int getFormId() {
        return formId;
    }

    @Override
    public boolean isAccessable() {
        return true;
    }
}
