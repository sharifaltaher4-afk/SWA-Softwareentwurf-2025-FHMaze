package main.java.de.fhms.eti.players.communication;

import main.java.de.fhms.eti.players.domain.Cell;

/**
 * Interface providing methods to serialize messages coming from the maze game.
 */
public interface ISerialize {
    /**
     * Converts cell status input into the cell class.
     *
     * @param input The raw input from the maze game.
     */
    Cell serializeCellStatus(String input);
}
