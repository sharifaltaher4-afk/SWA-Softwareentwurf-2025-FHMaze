package de.fhms.eti.players.Domain_Klassen;

/**
 * A player action.
 */
abstract sealed public class Action permits GoAction, PositionAction, FinishAction, TakeAction { }

