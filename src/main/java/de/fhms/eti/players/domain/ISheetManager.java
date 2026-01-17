package de.fhms.eti.players.domain;

/**
 * Verwaltet den Blaettervorrat des Bots (Level 5).
 */
public interface ISheetManager {
    int getSheetCount();

    boolean hasSheets();

    void addSheet();

    boolean consumeSheet();
}
