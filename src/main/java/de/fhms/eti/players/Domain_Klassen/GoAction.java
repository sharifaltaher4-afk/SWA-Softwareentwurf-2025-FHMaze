package de.fhms.eti.players.Domain_Klassen;

public final class GoAction extends Action {
    private final Direction direction;

    public GoAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
