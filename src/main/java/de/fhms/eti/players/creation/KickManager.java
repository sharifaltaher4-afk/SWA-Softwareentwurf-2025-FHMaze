package de.fhms.eti.players.creation;

import de.fhms.eti.players.communication.TurnData;
import de.fhms.eti.players.domain.*;

/**
 * Verwaltet die Kick-Entscheidung für Level 4 und 5.
 * 
 * Level 4: Kicken von Formularen
 * Level 5: Kicken von Formularen UND Sheets (Blättern)
 * 
 * Kicken verschiebt ein Formular oder Sheet von der aktuellen Zelle 
 * auf eine freie Nachbarzelle.
 */
public class KickManager {

    private final ILevelDetector levelDetector;
    private final BotConfig config;
    private Position lastKickPosition;
    private Direction lastKickDirection;
    private int lastKickFormId = -1;
    private int lastKickPlayerId = -1;
    private boolean lastKickWasSheet = false;

    public KickManager(BotConfig config) {
        this.config = config;
        this.levelDetector = config.levelDetector();
    }

    /**
     * Liefert eine KickAction, wenn:
     * - Level 4: Wir auf einem fremden Formular stehen, das nicht genommen werden kann
     * - Level 5: Zusätzlich auch wenn wir auf einem Sheet stehen
     * 
     * @param turnData Aktuelle Rundendaten
     * @param currentPosition Aktuelle Position des Bots
     * @return KickAction oder null wenn nicht gekickt werden soll
     */
    public KickAction decideKick(TurnData turnData, Position currentPosition) {
        // Nur in Level 4 und 5 aktiv
        if (!levelDetector.isLevel4() && !levelDetector.isLevel5()) {
            return null;
        }

        // Level 5: Prüfe zuerst ob wir auf einem Sheet stehen
        if (levelDetector.isLevel5() && turnData.currentCell instanceof SheetCell) {
            return decideSheetKick(turnData, currentPosition);
        }

        // Level 4 & 5: Prüfe ob wir auf einem Formular stehen
        if (turnData.currentCell instanceof FormCell formCell) {
            return decideFormKick(turnData, currentPosition, formCell);
        }

        return null;
    }

    /**
     * Entscheidet ob ein Sheet gekickt werden soll (Level 5).
     * 
     * Ein Sheet sollte gekickt werden wenn:
     * - Es im Weg liegt (z.B. verdeckt potentiell ein eigenes Formular)
     * - Oder wir es wegkicken wollen um weiterzugehen
     */
    private KickAction decideSheetKick(TurnData turnData, Position currentPosition) {
        // Finde freie Richtung zum Kicken
        Direction freeDirection = findFirstFreeDirection(turnData, null);
        if (freeDirection == null) {
            return null; // Kein freies Ziel - kann nicht kicken
        }

        // Wiederholtes Kicken desselben Sheets vermeiden
        if (isRepeatedSheetKick(currentPosition, freeDirection)) {
            Direction alt = findFirstFreeDirection(turnData, freeDirection);
            if (alt == null || isRepeatedSheetKick(currentPosition, alt)) {
                return null;
            }
            freeDirection = alt;
        }

        rememberLastSheetKick(currentPosition, freeDirection);
        return new KickAction(freeDirection);
    }

    /**
     * Entscheidet ob ein Formular gekickt werden soll (Level 4 & 5).
     */
    private KickAction decideFormKick(TurnData turnData, Position currentPosition, FormCell formCell) {
        // Eigene Formulare nicht kicken
        if (formCell.getPlayerId() == config.playerId()) {
            return null;
        }

        // Wenn das Formular direkt genommen werden kann, nicht kicken
        if (config.formManager().canTakeForm(formCell)) {
            return null;
        }

        // Nächste Zelle nach dem Kicken muss frei sein
        Direction freeDirection = findFirstFreeDirection(turnData, null);
        if (freeDirection == null) {
            return null;
        }

        // Wiederholtes Kicken derselben Form aus gleicher Position/Richtung vermeiden
        if (isRepeatedFormKick(currentPosition, freeDirection, formCell)) {
            Direction alt = findFirstFreeDirection(turnData, freeDirection);
            if (alt == null || isRepeatedFormKick(currentPosition, alt, formCell)) {
                return null;
            }
            freeDirection = alt;
        }

        rememberLastFormKick(currentPosition, freeDirection, formCell);
        return new KickAction(freeDirection);
    }

    /**
     * Prüft ob es ein wiederholter Kick desselben Formulars ist.
     */
    private boolean isRepeatedFormKick(Position pos, Direction dir, FormCell formCell) {
        return !lastKickWasSheet
                && lastKickPosition != null
                && lastKickPosition.equals(pos)
                && lastKickDirection == dir
                && lastKickFormId == formCell.getFormId()
                && lastKickPlayerId == formCell.getPlayerId();
    }

    /**
     * Prüft ob es ein wiederholter Kick desselben Sheets ist.
     */
    private boolean isRepeatedSheetKick(Position pos, Direction dir) {
        return lastKickWasSheet
                && lastKickPosition != null
                && lastKickPosition.equals(pos)
                && lastKickDirection == dir;
    }

    /**
     * Merkt sich den letzten Formular-Kick.
     */
    private void rememberLastFormKick(Position pos, Direction dir, FormCell formCell) {
        this.lastKickPosition = pos;
        this.lastKickDirection = dir;
        this.lastKickFormId = formCell.getFormId();
        this.lastKickPlayerId = formCell.getPlayerId();
        this.lastKickWasSheet = false;
    }

    /**
     * Merkt sich den letzten Sheet-Kick.
     */
    private void rememberLastSheetKick(Position pos, Direction dir) {
        this.lastKickPosition = pos;
        this.lastKickDirection = dir;
        this.lastKickFormId = -1;
        this.lastKickPlayerId = -1;
        this.lastKickWasSheet = true;
    }

    /**
     * Findet die erste freie Richtung zum Kicken.
     */
    private Direction findFirstFreeDirection(TurnData turnData, Direction avoidDirection) {
        Direction[] directions = {Direction.north, Direction.east, Direction.south, Direction.west};
        Cell[] cells = {turnData.northCell, turnData.eastCell, turnData.southCell, turnData.westCell};

        for (int i = 0; i < directions.length; i++) {
            if (avoidDirection != null && directions[i] == avoidDirection) {
                continue;
            }
            if (isFreeTargetCell(cells[i])) {
                return directions[i];
            }
        }
        return null;
    }

    /**
     * Prüft ob eine Zelle ein gültiges Ziel für einen Kick ist.
     * Ein Kick-Ziel muss FREI sein (Floor ohne Hindernisse).
     */
    private boolean isFreeTargetCell(Cell cell) {
        if (cell instanceof WallCell) {
            return false;
        }
        if (cell instanceof FormCell) {
            return false;
        }
        if (cell instanceof SheetCell) {
            return false; // Auf ein Sheet kann man nicht kicken
        }
        if (cell instanceof FinishCell) {
            return false;
        }
        return !cell.hasOpponent();
    }
}