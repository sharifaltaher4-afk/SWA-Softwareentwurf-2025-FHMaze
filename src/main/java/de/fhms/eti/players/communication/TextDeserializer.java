package de.fhms.eti.players.communication;

import de.fhms.eti.players.domain.*;

public class TextDeserializer implements IDeserialize {
    @Override
    public String deserializeAction(Action action) {
        return switch (action) {
            case GoAction goAction -> "go " + goAction.getDirection().toString();
            case FinishAction f -> "finish";
            case PositionAction p -> "position";
            case TakeAction t -> "take";
            case KickAction kickAction -> "kick " + kickAction.getDirection().toString();
            case PutAction p -> "put";
        };
    }
}
