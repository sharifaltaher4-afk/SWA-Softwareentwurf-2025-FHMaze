package de.fhms.eti.players.creation;

import de.fhms.eti.players.communication.GameCommunication;
import de.fhms.eti.players.communication.TurnData;
import de.fhms.eti.players.domain.*;

/**
 * Verarbeitet Aktions-Ergebnisse und verwaltet Zusände für Folgeaktionen.
 */
public class ActionResultTracker {
    private Action lastSentAction;
    private Position lastSentPosition;
    private Position lastPutPosition;
    private boolean sheetEmptyKnown;

    public void recordAndSend(Action action, Position currentPosition, GameCommunication comm) {
        lastSentAction = action;
        lastSentPosition = currentPosition;
        comm.sendAction(action);
    }

    public boolean isSheetEmptyKnown() {
        return sheetEmptyKnown;
    }

    public boolean isSameAsLastPut(Position currentPosition) {
        return lastPutPosition != null && lastPutPosition.equals(currentPosition);
    }

    public boolean canTakeSheet(Position currentPosition, ISheetManager sheetManager) {
        return sheetManager.getSheetCount() == 0
                && (lastPutPosition == null || !lastPutPosition.equals(currentPosition));
    }

    public boolean isTakingOrTalking(String lastActionResult) {
        ActionResult result = parseActionResult(lastActionResult);
        return result == ActionResult.NOK_TAKING || result == ActionResult.NOK_TALKING;
    }

    public void reconcile(TurnData turnData, Navigator navigator, ISheetManager sheetManager) {
        if (lastSentAction == null) {
            return;
        }

        handleSheetResult(turnData.lastActionResult, sheetManager);

        if (lastSentAction instanceof GoAction && isActionFailed(turnData.lastActionResult)) {
            if (lastSentPosition != null) {
                navigator.setCurrentPosition(lastSentPosition);
            }
        }

        lastSentAction = null;
        lastSentPosition = null;
    }

    private void handleSheetResult(String lastActionResult, ISheetManager sheetManager) {
        ActionResult result = parseActionResult(lastActionResult);

        if (lastSentAction instanceof PutAction) {
            if (result == ActionResult.OK) {
                sheetManager.consumeSheet();
                sheetEmptyKnown = false;
                lastPutPosition = lastSentPosition;
            } else if (result == ActionResult.NOK_EMPTY) {
                sheetEmptyKnown = true;
            }
            return;
        }

        if (lastSentAction instanceof TakeAction) {
            if (result == ActionResult.OK_SHEET) {
                sheetManager.addSheet();
                sheetEmptyKnown = false;
            }
        }
    }

    private boolean isActionFailed(String lastActionResult) {
        return resultIsFailure(parseActionResult(lastActionResult));
    }

    private boolean resultIsFailure(ActionResult result) {
        return result == ActionResult.NOK_BLOCKED
                || result == ActionResult.NOK_EMPTY
                || result == ActionResult.NOK_WRONGORDER
                || result == ActionResult.NOK_NOTYOURS
                || result == ActionResult.NOK_TAKING
                || result == ActionResult.NOK_TALKING
                || result == ActionResult.NOK_OTHER
                || result == ActionResult.INVALID;
    }

    private ActionResult parseActionResult(String lastActionResult) {
        if (lastActionResult == null) {
            return ActionResult.NONE;
        }
        String value = lastActionResult.trim().toUpperCase();
        if (value.startsWith("OK FORM")) {
            return ActionResult.OK_FORM;
        }
        if (value.startsWith("OK SHEET")) {
            return ActionResult.OK_SHEET;
        }
        if (value.startsWith("OK")) {
            return ActionResult.OK;
        }
        if (value.startsWith("NOK BLOCKED")) {
            return ActionResult.NOK_BLOCKED;
        }
        if (value.startsWith("NOK EMPTY")) {
            return ActionResult.NOK_EMPTY;
        }
        if (value.startsWith("NOK WRONGORDER")) {
            return ActionResult.NOK_WRONGORDER;
        }
        if (value.startsWith("NOK NOTYOURS")) {
            return ActionResult.NOK_NOTYOURS;
        }
        if (value.startsWith("NOK TAKING")) {
            return ActionResult.NOK_TAKING;
        }
        if (value.startsWith("NOK TALKING")) {
            return ActionResult.NOK_TALKING;
        }
        if (value.startsWith("INVALID")) {
            return ActionResult.INVALID;
        }
        if (value.startsWith("NOK")) {
            return ActionResult.NOK_OTHER;
        }
        return ActionResult.OTHER;
    }

    private enum ActionResult {
        NONE,
        OK,
        OK_FORM,
        OK_SHEET,
        NOK_BLOCKED,
        NOK_EMPTY,
        NOK_WRONGORDER,
        NOK_NOTYOURS,
        NOK_TAKING,
        NOK_TALKING,
        NOK_OTHER,
        INVALID,
        OTHER
    }
}
