package de.fhms.eti.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FormManager Verwaltet das Sammeln von formularen in Level 2
 * 
 * Verantwortung (SRP):
 * - PrÃ¼ft ob Formulare aufgenommen werden kÃ¶nnen
 * - Verwaltet die Reihenfolge der Formulare (A=0, B=1, C=2, ...)
 * - ZÃ¤hlt gesammelte Formulare
 * - PrÃ¼ft ob alle benÃ¶tigten Formulare gesammelt wurden
 * 
 * FORM-Format: "FORM <playerId> <formId>"
 * Beispiel: "FORM 0 1" ==> Formular B fÃ¼r Spieler 0
 */
public class FormManager implements IFormManager {
    private int myPlayerId;           // Eigene Spieler-ID
    private List<Integer> collectedForms;  // Gesammelte Formular-IDs
    private int nextFormId;           // NÃ¤chste erwartete Formular-ID ""Reihenfolge
    private int totalFormsNeeded;     // Anzahl benÃ¶tigter Formulare
    private Map<Integer, Position> foundFormPositions;// Formular Positionen
    /**
     * Konstruktor Initialisiert den FormManager
     * 
     * @param playerId Die eigene Spieler-ID
     */
    public FormManager(int playerId) {
        this.myPlayerId = playerId;
        this.collectedForms = new ArrayList<>();
        this.nextFormId = 1;          // Start mit Formular A (ID 1)
        this.totalFormsNeeded = -1;   // Noch unbekannt
        this.foundFormPositions = new HashMap<>();
        System.err.println("FormManager initialisiert fÃ¼r Spieler " + playerId);
    }
    
    /**
     * PrÃ¼ft ob ein Formular auf der aktuellen Zelle aufgenommen werden kann
     * 
     * Bedingungen fÃ¼r take:
     * 1. Zelle muss FORM-String enthalten
     * 2. Formular muss fÃ¼r diesen Spieler sein
     * 3. Formular muss in der richtigen Reihenfolge sein (nÃ¤chste erwartete ID)
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
        
        // PRÃœFUNG 1: GehÃ¶rt das Formular diesem Spieler?
        if (!formInfo.isValidForPlayer(myPlayerId)) {
            System.err.println("Formular gehÃ¶rt Spieler " + formInfo.getPlayerId() + "  nicht mir!");
            return false;
        }
        
        // PRÃœFUNG 2: Ist es das nÃ¤chste Formular in der Reihenfolge?
        if (!isValidFormOrder(formInfo.getFormId())) {
            System.err.println("Falsche Reihenfolge! Erwartet: " + nextFormId + ", Gefunden: " + formInfo.getFormId());
            return false;
        }
        
        System.err.println("Kann Formular " + formInfo.getFormId() + " aufnehmen!");
        return true;
    }
    
    /**
     * Nimmt ein Formular auf und fÃ¼gt es zur Sammlung hinzu
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
            // Formular zur Sammlung hinzufÃ¼gen
            collectedForms.add(formInfo.getFormId());
            nextFormId++;  // NÃ¤chstes Formular in der Reihenfolge erwarten
            
            System.err.println("Formular " + formInfo.getFormId() + " aufgenommen! " +
                             "Gesammelt: " + collectedForms + ", NÃ¤chstes: " + nextFormId);
            return true;
        }
        
        return false;
    }
    
    /**
     * PrÃ¼ft ob alle benÃ¶tigten Formulare gesammelt wurden
     * 
     * @return true wenn alle Formulare gesammelt, sonst false
     */
    @Override
    public boolean hasAllForms() {
        if (totalFormsNeeded == -1) {
            // Anzahl benÃ¶tigter Formulare noch unbekannt
            return false;
        }
        
        boolean hasAll = (collectedForms.size() >= totalFormsNeeded);
        if (hasAll) {
            System.err.println("Alle " + totalFormsNeeded + " Formulare gesammelt!");
        }
        return hasAll;
    }
    
    /**
     * Gibt die ID des nÃ¤chsten erwarteten Formulars zurÃ¼ck
     * 
     * @return NÃ¤chste Formular ID (A=0, B=1, C=2, ...)
     */
    @Override
    public int getNextFormId() {
        return nextFormId;
    }
    
    /**
     * Gibt die Liste der gesammelten Formulare zurÃ¼ck
     * 
     * @return Liste mit Formular-IDs
     */
    @Override
    public List<Integer> getCollectedForms() {
        return new ArrayList<>(collectedForms);  // Kopie zurÃ¼ckgeben
    }
    
    /**
     * Setzt die Anzahl der benÃ¶tigten Formulare (vom FINISH-String)
     * 
     * @param formCount Anzahl der benÃ¶tigten Formulare
     */
    public void setTotalFormsNeeded(int formCount) {
        this.totalFormsNeeded = formCount;
        System.err.println("BenÃ¶tigte Formulare: " + formCount);
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
     * PrÃ¼ft ob die Formular-ID der richtigen Reihenfolge entspricht
     * 
     * @param formId Die zu prÃ¼fende Formular-ID
     * @return true wenn Reihenfolge korrekt, sonst false
     */
    private boolean isValidFormOrder(int formId) {
        return formId == nextFormId;
    }

    /**
     * Merkt sich die Position eines Formulars
     * @param formId
     * @param position
     */
    public void rememberFormPosition(int formId, Position position){
        foundFormPositions.put(formId, position);
        System.err.println("Position von Formular"+ formId + "gemerkt" + position);
    }

    /**
     * Gibt die gemerkte Position eines Formulars zurÃ¼ck
     * @param formId
     * @return
     */
    public Position getRememberedFormPosition(int formId){
        return  foundFormPositions.get(formId);
    }


    /**
     * prÃ¼ft ob die Anzahl benÃ¶tigter Formulare bekannt ist
     * @return (totalFormsNeeded != -1)
     */
    public boolean knowsTotalForms(){
        return totalFormsNeeded != -1;
    }



    
}
