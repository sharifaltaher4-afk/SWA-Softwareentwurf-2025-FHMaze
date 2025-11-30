package main.java.de.fhms.eti.players.Domain_Klassen;
/**
 * LEVEL 3: FormCell kann auch Gegner-Info haben
 * Z.B "FORM 0 1 !2" = Formular + Gegner 2 Felder entfernt
 */
final public class FormCell extends Cell {
    private final int playerId;
    private final int formId;

    public FormCell(int playerId, int formId) {
        super();
        this.playerId = playerId;
        this.formId = formId;
    }

    /**
     * LEVEL 3: Konstruktor mit Gegner Informationen
     */
    public FormCell(int playerId, int formId, boolean hasOpponent, int opponentDistance) {
        super(hasOpponent, opponentDistance);
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
