package de.fhms.eti.players.Kommunikations_Klassen;

import de.fhms.eti.players.Domain_Klassen.*;

public class TextSerializer implements ISerialize {
    @Override
    public Cell serializeCellStatus(String input) {
        String[] parts = input.split(" ");
        return switch (parts[0]) {
            case "WALL" -> new WallCell();
            case "FLOOR" -> new FloorCell();
            case "FINISH" -> {
                int playerId = Integer.parseInt(parts[1]);
                int formCount = Integer.parseInt(parts[2]);
                yield new FinishCell(playerId, formCount);
            }
            case "FORM" -> {
                int playerId = Integer.parseInt(parts[1]);
                int formCount = Integer.parseInt(parts[2]);
                yield new FormCell(playerId, formCount);
            }
            default -> null;
        };
    }
}
