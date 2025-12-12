package main.java.de.fhms.eti.players.domain;

/**
 * Der LevelDetector erkennt und verwaltet das aktuelle Spieoelevel
 * <p>
 * Verantwortung(SRP):
 * Analysiert Zellstatus um Level 1 und zwei von eonerander zu unterscheiden
 * verwaltet den aktuellen Level Zustand
 * <p>
 * Level Eerkennung basiert auf dem FINISH-String:
 * Level 1: "FINISH 0" (nur playerId)
 * Level 2: "FINISH 0 3" (playerId + formCount)
 */
public class LevelDetector implements ILevelDetector {
    private int currentLevel; // 1, 2, 3 oder 4

    /**
     * Konstruktor Startet mit Level 1
     */
    public LevelDetector() {
        this.currentLevel = 1;// Default: Level 1
    }

    /**
     * @param finishCell Der FINISH-Zellstatus String
     * @return Erkanntes Level (1 oder 2)
     */
    @Override
    public int detectLevel(Cell finishCell) {
        if (currentLevel == 4) {
            return currentLevel;
        }

        if (!(finishCell instanceof FinishCell finish)) {
            return currentLevel;
        }

        // Level-Erkennung basierend auf FinishCell
        if (finish.getFormCount() > 0) {
            // Hat FormCount ==> Level 2 oder höher
            if (currentLevel < 2) {
                this.currentLevel = 2;
                System.err.println("Level 2+ erkannt (Formulare benötigt)");
            }
        } else {
            // Kein FormCount ==> Level 1
            if (currentLevel != 1) {
                this.currentLevel = 1;
                System.err.println("Level 1 erkannt (einfaches Labyrinth)");
            }
        }

        return currentLevel;
    }

    /**
     * Gibt zurueck ob aktuell Level 1 gespielt wird
     *
     * @return true wenn Level 1, sonst false
     */
    @Override
    public boolean isLevel1() {
        return currentLevel == 1;
    }

    /**
     * Gibt zurueck ob aktuell Level 2 gespielt wird
     *
     * @return true wenn Level 2, sonst false
     */
    @Override
    public boolean isLevel2() {
        return currentLevel == 2;
    }

    /**
     * Gibt zurueck ob aktuell Level 3 gespielt wird
     *
     * @return true wenn Level 3, sonst false
     */
    @Override
    public boolean isLevel3() {
        return currentLevel == 3;
    }

    /**
     * Gibt zurueck ob aktuell Level 4 gespielt wird
     *
     * @return true wenn Level 4, sonst false
     */
    @Override
    public boolean isLevel4() {
        return currentLevel == 4;
    }

    /**
     * Gibt das aktuell erkannte Level zurueck
     *
     * @return 1 oder 2
     */
    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public void setCurrentLevel(int level) {
        if (level == 1 || level == 2 || level == 3 || level == 4) {
            this.currentLevel = level;
            System.err.println("Level manuell gesetzt auf: " + level);
        }
    }
}
