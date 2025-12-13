package de.fhms.eti.players.domain;

/**
 * FormInfo Datenklasse für Formular Informationen
 * <p>
 * Einfache Datenhaltung für FORM-Zellen.
 */
public class FormInfo {
    private final int playerId;
    private final int formId;

    public FormInfo(int playerId, int formId) {
        this.playerId = playerId;
        this.formId = formId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getFormId() {
        return formId;
    }

    /**
     * prüft ob dieses Formular für ein bestimmten Spieler gültig ist
     *
     * @param myPlayerId Die Spieler-ID des Bots
     * @return true wenn das Formular für diesen Spieler ist
     */
    public boolean isValidForPlayer(int myPlayerId) {
        return this.playerId == myPlayerId;
    }

    @Override
    public String toString() {
        return "FormInfo[player=" + playerId + ", form=" + formId + "]";
    }
}
