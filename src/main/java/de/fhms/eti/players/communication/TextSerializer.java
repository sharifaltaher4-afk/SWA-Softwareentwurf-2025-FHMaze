package main.java.de.fhms.eti.players.communication;

import main.java.de.fhms.eti.players.domain.*;

/**
 * LEVEL 3 REFACTORING:
 * - Parsed Gegner nformationen direkt in Cell Objekte
 * - Keine RAW-Strings mehr noetig
 * - Sauberere Architektur
 */
public class TextSerializer implements ISerialize {
    @Override
    public Cell serializeCellStatus(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        // LEVEL 3: Parse Gegner-Informationen
        OpponentInfo opponentInfo = parseOpponentInfo(input);
        boolean hasOpponent = opponentInfo.hasOpponent;
        int opponentDistance = opponentInfo.distance;

        // LEVEL 3: Entferne Gegner-Marker BEVOR Parsing
        // "FLOOR !" ==> "FLOOR"
        // "FORM 0 1 !2" ==> "FORM 0 1"
        String cleanedInput = OpponentDetector.removeOpponentMarker(input);
        //String[] parts = input.split(" ");
        String[] parts = cleanedInput.split(" ");
        return switch (parts[0]) {
            case "WALL" -> new WallCell(hasOpponent, opponentDistance);
            case "FLOOR" -> new FloorCell(hasOpponent, opponentDistance);
            case "FINISH" -> {
                int playerId = Integer.parseInt(parts[1]);
                int formCount = Integer.parseInt(parts[2]);
                yield new FinishCell(playerId, formCount, hasOpponent, opponentDistance);
            }
            case "FORM" -> {
                int playerId = Integer.parseInt(parts[1]);
                int formCount = Integer.parseInt(parts[2]);
                yield new FormCell(playerId, formCount, hasOpponent, opponentDistance);
            }
            default -> null;
        };
    }

    /**
     * LEVEL 3: Parsed Gegner Informationen aus Cell-Status
     * <p>
     * Beispiele:
     * - "FLOOR" ==> OpponentInfo(false, -1)
     * - "FLOOR !" ==> OpponentInfo(true, 0)
     * - "FLOOR !2" ==> OpponentInfo(true, 2)
     * - "FORM 0 1 !3" ==> OpponentInfo(true, 3)
     */
    private OpponentInfo parseOpponentInfo(String cellStatus) {
        if (cellStatus == null || !cellStatus.contains("!")) {
            return new OpponentInfo(false, -1);
        }

        try {
            int exclIndex = cellStatus.indexOf("!");
            String afterExcl = cellStatus.substring(exclIndex + 1).trim();

            // Kein Gegner direkt hier (nur Marker)
            if (afterExcl.isEmpty()) {
                return new OpponentInfo(true, 0);
            }

            // Parse Distanz
            int distance = Integer.parseInt(afterExcl);
            return new OpponentInfo(true, distance);

        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Parsen von Gegner-Info: " + cellStatus);
            return new OpponentInfo(false, -1);
        }
    }

    /**
     * Hilfklasse für Gegner Informationen
     */
    private static class OpponentInfo {
        final boolean hasOpponent;
        final int distance;

        OpponentInfo(boolean hasOpponent, int distance) {
            this.hasOpponent = hasOpponent;
            this.distance = distance;
        }
    }
}
