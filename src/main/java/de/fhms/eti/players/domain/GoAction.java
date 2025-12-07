package main.java.de.fhms.eti.players.domain;

public final class GoAction extends Action {
    private final Direction direction;

    public GoAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
