package de.fhms.eti.players.Kommunikations_Klassen;

import de.fhms.eti.players.Domain_Klassen.Action;

/**
 * Interface providing methods to deserialize data which can be sent to the maze game.
 */
public interface IDeserialize {
    String deserializeAction(Action action);
}
