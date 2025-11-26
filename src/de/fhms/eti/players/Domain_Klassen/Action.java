package de.fhms.eti.players.Domain_Klassen;

/**
 * A player action.
 */
sealed public class Action permits GoAction, PositionAction, FinishAction { }

