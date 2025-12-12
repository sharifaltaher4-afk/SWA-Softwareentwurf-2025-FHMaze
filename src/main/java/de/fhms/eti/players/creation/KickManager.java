package main.java.de.fhms.eti.players.creation;

import main.java.de.fhms.eti.players.communication.TurnData;
import main.java.de.fhms.eti.players.domain.*;

/**
 * Verwaltet die Kick-Entscheidung für Level 4, damit der GameController klein bleibt.
 * Kicken verschiebt ein Formular von der aktuellen Zelle auf eine freie Nachbarzelle.
 */
public class KickManager {

    private final ILevelDetector levelDetector;
    private final BotConfig config;
    private Position lastKickPosition;
    private Direction lastKickDirection;
    private int lastKickFormId = -1;
    private int lastKickPlayerId = -1;

    public KickManager(BotConfig config) {
        this.config = config;
        this.levelDetector = config.levelDetector();
    }

    /**
     * Liefert eine KickAction, wenn Level 4 aktiv ist, wir auf einem Formular stehen,
     * es nicht genommen werden kann und ein freies Ziel existiert. Sonst null.
     */
    public KickAction decideKick(TurnData turnData, Position currentPosition) {
        if (!levelDetector.isLevel4()) {
            return null;
        }

        if (!(turnData.currentCell instanceof FormCell formCell)) {
            return null;
        }

        // Eigene Formulare nicht kicken
        if (formCell.getPlayerId() == config.playerId()) {
            return null;
        }

        // Wenn das Formular direkt genommen werden kann, nicht kicken.
        if (config.formManager().canTakeForm(formCell)) {
            return null;
        }

        Direction freeDirection = findFirstFreeDirection(turnData, null);
        if (freeDirection == null) {
            return null;
        }

        // Wiederholtes Kicken derselben Form aus gleicher Position/Richtung vermeiden
        if (isRepeatedKick(currentPosition, freeDirection, formCell)) {
            Direction alt = findFirstFreeDirection(turnData, freeDirection);
            if (alt == null || isRepeatedKick(currentPosition, alt, formCell)) {
                return null;
            }
            freeDirection = alt;
        }

        rememberLastKick(currentPosition, freeDirection, formCell);
        return new KickAction(freeDirection);
    }

    private boolean isRepeatedKick(Position pos, Direction dir, FormCell formCell) {
        return lastKickPosition != null
                && lastKickPosition.equals(pos)
                && lastKickDirection == dir
                && lastKickFormId == formCell.getFormId()
                && lastKickPlayerId == formCell.getPlayerId();
    }

    private void rememberLastKick(Position pos, Direction dir, FormCell formCell) {
        this.lastKickPosition = pos;
        this.lastKickDirection = dir;
        this.lastKickFormId = formCell.getFormId();
        this.lastKickPlayerId = formCell.getPlayerId();
    }

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

    private boolean isFreeTargetCell(Cell cell) {
        // Frei falls keine Wand, kein Formular, kein Ziel (Finish), kein Gegner
        if (cell instanceof WallCell) {
            return false;
        }
        if (cell instanceof FormCell) {
            return false;
        }
        if (cell instanceof FinishCell) {
            return false;
        }
        return !cell.hasOpponent();
    }
}
