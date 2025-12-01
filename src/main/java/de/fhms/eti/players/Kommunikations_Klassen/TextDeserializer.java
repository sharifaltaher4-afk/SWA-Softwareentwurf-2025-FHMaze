package main.java.de.fhms.eti.players.Kommunikations_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.*;

public class TextDeserializer implements IDeserialize {
    @Override
    public String deserializeAction(Action action) {
        return switch (action) {
            case GoAction goAction -> "go " + goAction.getDirection().toString();
            case FinishAction finishAction -> "finish";
            case PositionAction positionAction -> "position";
            case TakeAction takeAction -> "take";
            default -> "position"; // Fallback statt null
        };
    }
}
