package de.fhms.eti.players;

import java.util.ArrayList;
import java.util.List;

/**
 * FormManager Verwaltet das Sammeln von formularen in Level 2
 * 
 * Verantwortung (SRP):
 * - Prüft ob Formulare aufgenommen werden können
 * - Verwaltet die Reihenfolge der Formulare (A=0, B=1, C=2, ...)
 * - Zählt gesammelte Formulare
 * - Prüft ob alle benötigten Formulare gesammelt wurden
 * 
 * FORM-Format: "FORM <playerId> <formId>"
 * Beispiel: "FORM 0 1" ==> Formular B für Spieler 0
 */
public class FormManager implements IFormManager {
    private int myPlayerId;           // Eigene Spieler-ID
    private List<Integer> collectedForms;  // Gesammelte Formular-IDs
    private int nextFormId;           // Nächste erwartete Formular-ID ""Reihenfolge
    private int totalFormsNeeded;     // Anzahl benötigter Formulare
    
    /**
     * Konstruktor Initialisiert den FormManager
     * 
     * @param playerId Die eigene Spieler-ID
     */
    public FormManager(int playerId) {
        this.myPlayerId = playerId;
        this.collectedForms = new ArrayList<>();
        this.nextFormId = 0;          // Start mit Formular A (ID 0)
        this.totalFormsNeeded = -1;   // Noch unbekannt
        System.err.println("FormManager initialisiert für Spieler " + playerId);
    }
    
    /**
     * Prüft ob ein Formular auf der aktuellen Zelle aufgenommen werden kann
     * 
     * Bedingungen für take:
     * 1. Zelle muss FORM-String enthalten
     * 2. Formular muss für diesen Spieler sein
     * 3. Formular muss in der richtigen Reihenfolge sein (nächste erwartete ID)
     * 
     * @param cellStatus Der Zellstatus-String
     * @return true wenn Formular aufgenommen werden kann sonst false
     */
    @Override
    public boolean canTakeForm(String cellStatus) {
        if (cellStatus == null || !cellStatus.startsWith("FORM")) {
            return false;  // Kein FORM-String
        }
        
        FormInfo formInfo = parseFormCell(cellStatus);
        if (formInfo == null) {
            return false;  // Parsing fehlgeschlagen
        }
        
        // PRÜFUNG 1: Gehört das Formular diesem Spieler?
        if (!formInfo.isValidForPlayer(myPlayerId)) {
            System.err.println("Formular gehört Spieler " + formInfo.getPlayerId() + " - nicht mir!");
            return false;
        }
        
        // PRÜFUNG 2: Ist es das nächste Formular in der Reihenfolge?
        if (!isValidFormOrder(formInfo.getFormId())) {
            System.err.println("Falsche Reihenfolge! Erwartet: " + nextFormId + ", Gefunden: " + formInfo.getFormId());
            return false;
        }
        
        System.err.println("Kann Formular " + formInfo.getFormId() + " aufnehmen!");
        return true;
    }
    
    /**
     * Nimmt ein Formular auf und fügt es zur Sammlung hinzu
     * 
     * @param cellStatus Der Zellstatus-String mit FORM-Information
     * @return true wenn Formular erfolgreich aufgenommen wurde, sonst false
     */
    @Override
    public boolean takeForm(String cellStatus) {
        if (!canTakeForm(cellStatus)) {
            return false;  // Formular kann nicht aufgenommen werden
        }
        
        FormInfo formInfo = parseFormCell(cellStatus);
        if (formInfo != null) {
            // Formular zur Sammlung hinzufügen
            collectedForms.add(formInfo.getFormId());
            nextFormId++;  // Nächstes Formular in der Reihenfolge erwarten
            
            System.err.println("Formular " + formInfo.getFormId() + " aufgenommen! " +
                             "Gesammelt: " + collectedForms + ", Nächstes: " + nextFormId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Prüft ob alle benötigten Formulare gesammelt wurden
     * 
     * @return true wenn alle Formulare gesammelt, sonst false
     */
    @Override
    public boolean hasAllForms() {
        if (totalFormsNeeded == -1) {
            // Anzahl benötigter Formulare noch unbekannt
            return false;
        }
        
        boolean hasAll = (collectedForms.size() >= totalFormsNeeded);
        if (hasAll) {
            System.err.println("Alle " + totalFormsNeeded + " Formulare gesammelt!");
        }
        return hasAll;
    }
    
    /**
     * Gibt die ID des nächsten erwarteten Formulars zurück
     * 
     * @return Nächste Formular-ID (A=0, B=1, C=2, ...)
     */
    @Override
    public int getNextFormId() {
        return nextFormId;
    }
    
    /**
     * Gibt die Liste der gesammelten Formulare zurück
     * 
     * @return Liste mit Formular-IDs
     */
    @Override
    public List<Integer> getCollectedForms() {
        return new ArrayList<>(collectedForms);  // Kopie zurückgeben
    }
    
    /**
     * Setzt die Anzahl der benötigten Formulare (vom FINISH-String)
     * 
     * @param formCount Anzahl der benötigten Formulare
     */
    public void setTotalFormsNeeded(int formCount) {
        this.totalFormsNeeded = formCount;
        System.err.println("Benötigte Formulare: " + formCount);
    }
    
    /**
     * Parst einen FORM-String und extrahiert die Informationen
     * 
     * @param cellStatus FORM-String z.B. "FORM 0 1"
     * @return FormInfo-Objekt oder null bei Fehler
     */
    private FormInfo parseFormCell(String cellStatus) {
        try {
            String[] parts = cellStatus.split(" ");
            if (parts.length >= 3) {
                int playerId = Integer.parseInt(parts[1]);
                int formId = Integer.parseInt(parts[2]);
                return new FormInfo(playerId, formId);
            }
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Parsen des FORM-Strings: " + cellStatus);
        }
        return null;
    }
    
    /**
     * Prüft ob die Formular-ID der richtigen Reihenfolge entspricht
     * 
     * @param formId Die zu prüfende Formular-ID
     * @return true wenn Reihenfolge korrekt, sonst false
     */
    private boolean isValidFormOrder(int formId) {
        return formId == nextFormId;
    }
}