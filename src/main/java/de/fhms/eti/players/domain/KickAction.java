package main.java.de.fhms.eti.players.domain;

/**
 * Level-4 Aktion: Kicken eines Formulars in eine Richtung.
 * Bewegt nur das Formular, nicht den Spieler.
 */
public final class KickAction extends Action {
    private final Direction direction;

    public KickAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
