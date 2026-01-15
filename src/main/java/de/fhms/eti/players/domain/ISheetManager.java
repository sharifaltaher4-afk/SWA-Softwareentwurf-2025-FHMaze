package de.fhms.eti.players.domain;

public interface ISheetManager {
    /**
     * Prüft ob ein Blatt aufgenommen werden kann
     *
     * @param cellStatus Der Zellstatus
     * @return true wenn Blatt aufgenommen werden kann
     */
    boolean canTakeSheet(SheetCell cellStatus);
}
