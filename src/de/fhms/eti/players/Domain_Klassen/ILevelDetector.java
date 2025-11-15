package de.fhms.eti.players.Domain_Klassen;
import java.lang.String;

public interface ILevelDetector {
    /**
     * Analysiert ein FINISH-String um das Level zu erkennen
     * 
     * @param finishCell Der FINISH-Zellstatus String
     * @return Erkanntes Level (1 oder 2)
     */
    int detectLevel(String finishCell);

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
     * Gibt das aktuell erkannte Level zurück
     * 
     * @return 1 oder 2
     */
    int getCurrentLevel();
}
