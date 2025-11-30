package de.fhms.eti.players.Kommunikations_Klassen;
import de.fhms.eti.players.Domain_Klassen.Action;
import de.fhms.eti.players.Domain_Klassen.FinishAction;
import de.fhms.eti.players.Domain_Klassen.GoAction;
import de.fhms.eti.players.Domain_Klassen.PositionAction;
import de.fhms.eti.players.Domain_Klassen.TakeAction;

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