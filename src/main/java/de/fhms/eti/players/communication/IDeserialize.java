package main.java.de.fhms.eti.players.communication;

import main.java.de.fhms.eti.players.domain.Action;

/**
 * Interface providing methods to deserialize data which can be sent to the maze game.
 */
public interface IDeserialize {
    String deserializeAction(Action action);
}
