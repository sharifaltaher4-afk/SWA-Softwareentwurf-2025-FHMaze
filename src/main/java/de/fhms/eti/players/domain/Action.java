package de.fhms.eti.players.domain;

/**
 * A player action.
 */
abstract sealed public class Action permits FinishAction, GoAction, KickAction, PositionAction, PutAction, TakeAction {
}

