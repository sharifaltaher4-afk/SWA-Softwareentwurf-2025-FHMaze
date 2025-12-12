package main.java.de.fhms.eti.players.domain;

public interface ILevelDetector {
    /**
     * Analysiert ein FINISH-String um das Level zu erkennen
     *
     * @param finishCell Der FINISH-Zellstatus String
     * @return Erkanntes Level (1 oder 2)
     */
    int detectLevel(Cell finishCell);

    /**
     * Gibt zurück ob aktuell Level 1 gespielt wird
     *
     * @return true wenn Level 1, sonst false
     */
    boolean isLevel1();

    /**
     * Gibt zurück ob aktuell Level 2 gespielt wird
     *
     * @return true wenn Level 2, sonst false
     */
    boolean isLevel2();

    /**
     * Gibt zurück ob aktuell Level 3 gespielt wird
     *
     * @return true wenn Level 3, sonst false
     */
    boolean isLevel3();

    /**
     * Gibt zurück ob aktuell Level 4 gespielt wird
     *
     * @return true wenn Level 4, sonst false
     */
    boolean isLevel4();

    /**
     * Gibt das aktuell erkannte Level zurück
     *
     * @return 1 oder 2
     */
    int getCurrentLevel();

    /**
     * Setzt das aktuelle Level manuell
     *
     * @param level Level-ID
     */
    void setCurrentLevel(int level);
}
