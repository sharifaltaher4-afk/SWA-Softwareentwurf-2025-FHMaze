package main.java.de.fhms.eti.players.domain;

/**
 * A player action.
 */
abstract sealed public class Action permits GoAction, PositionAction, FinishAction, TakeAction {
}

