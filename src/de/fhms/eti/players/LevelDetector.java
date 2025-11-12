package de.fhms.eti.players;

/**
 * Der LevelDetector erkennt und verwaltet das aktuelle Spieölevel
 * 
 * Verantwortung(SRP):
 *  Analysiert Zellstatus um Level 1 und zwei von eonerander zu unterscheiden
 *  verwaltet den aktuellen Level Zustand
 * 
 * Level Eerkennung basiert auf dem FINISH-String:
 * Level 1: "FINISH 0" (nur playerId)
 * Level 2: "FINISH 0 3" (playerId + formCount)
 * 
 */
public class LevelDetector implements ILevelDetector {
    private int currentLevel; // 1 oder 2

    /**
     * Konstruktor Startet mit Level 1
     */
    public LevelDetector(){
        this.currentLevel=1;// Default: Level 1
    }

    /**
     * Analysiert ein FINISH-String um das Level zu erkennen
     * 
     * FINISH-Formate:
     * - Level 1: "FINISH 0"                    ==> 2 Teile
     * - Level 2: "FINISH 0 3"                  ==> 3 Teile  
     * - Level 2 mit Position: "FINISH 0 3 5 7" ==> 5 Teile (kann vorkommen)
     * 
     * @param finishCell Der FINISH-Zellstatus String
     * @return Erkanntes Level (1 oder 2)
     */
    @Override
    public int detectLevel(String finishCell) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detectLevel'");
    }

    /**
     * Gibt zurück ob aktuell Level 1 gespielt wird
     * 
     * @return true wenn Level 1, sonst false
     */
    @Override
    public boolean isLevel1() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isLevel1'");
    }

    /**
     * Gibt zurück ob aktuell Level 2 gespielt wird  
     * 
     * @return true wenn Level 2, sonst false
     */
    @Override
    public boolean isLevel2() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isLevel2'");
    }

    /**
     * Gibt das aktuell erkannte Level zurück
     * 
     * @return 1 oder 2
     */
    @Override
    public int getCurrentLevel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentLevel'");
    }
}

