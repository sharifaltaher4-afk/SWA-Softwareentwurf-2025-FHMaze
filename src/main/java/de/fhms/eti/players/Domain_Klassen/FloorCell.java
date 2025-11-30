package main.java.de.fhms.eti.players.Domain_Klassen;

/**
 * A floor cell on which a player can walk.
 * 
 * LEVEL 3: Kann jetzt Gegner Informationen enthalten
 */
public final class FloorCell extends Cell {
    /**
     * Default Konstruktor (kein Gegner)
     */
    public FloorCell() {
        super();
    }
    
    /**
     * LEVEL 3: Konstruktor mit Gegner Informationen
     */
    public FloorCell(boolean hasOpponent, int opponentDistance) {
        super(hasOpponent, opponentDistance);
    }

    @Override
    public boolean isAccessable() {
        return true;
    }
}
