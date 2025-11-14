package de.fhms.eti.players;

import java.util.List;

/**
 * IFormManager - Interface für Formular-Verwaltung
 * 
 * Definiert die Methoden zum Sammeln und Verwalten von Formularen.
 */
public interface IFormManager {
    
    /**
     * Prüft ob ein Formular aufgenommen werden kann
     * 
     * @param cellStatus Der Zellstatus-String
     * @return true wenn Formular aufgenommen werden kann
     */
    boolean canTakeForm(String cellStatus);
    
    /**
     * Nimmt ein Formular auf
     * 
     * @param cellStatus Der Zellstatus-String mit FORM-Information  
     * @return true wenn Formular erfolgreich aufgenommen wurde
     */
    boolean takeForm(String cellStatus);
    
    /**
     * Prüft ob alle benötigten Formulare gesammelt wurden
     * 
     * @return true wenn alle Formulare gesammelt
     */
    boolean hasAllForms();
    
    /**
     * Gibt die ID des nächsten erwarteten Formulars zurück
     * 
     * @return Nächste Formular-ID
     */
    int getNextFormId();
    
    /**
     * Gibt die gesammelten Formulare zurück
     * 
     * @return Liste der Formular-IDs
     */
    List<Integer> getCollectedForms();
}
