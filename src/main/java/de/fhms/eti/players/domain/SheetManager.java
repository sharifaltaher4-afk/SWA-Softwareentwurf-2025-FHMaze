package de.fhms.eti.players.domain;

/**
 * Einfache Verwaltung des Blaettervorrats (Level 5).
 */
public class SheetManager implements ISheetManager {
    private int sheetCount;

    public SheetManager(int sheetCount) {
        this.sheetCount = sheetCount;
    }

    @Override
    public int getSheetCount() {
        return sheetCount;
    }

    @Override
    public boolean hasSheets() {
        return sheetCount > 0;
    }

    @Override
    public void addSheet() {
        sheetCount++;
    }

    @Override
    public boolean consumeSheet() {
        if (sheetCount <= 0) {
            return false;
        }
        sheetCount--;
        return true;
    }
}
