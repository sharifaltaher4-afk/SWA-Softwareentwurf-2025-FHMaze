package de.fhms.eti.players;

import java.util.HashMap;
import java.util.Map;

public class FormCollectionStrategy implements INavigationStrategy{
    private Maze maze;
    int myPlayerId;
    private IFormManager iFormManager;
    private Position targetPosition;
    private Map<Integer, Position> formPositions;
    private SimpleNavigationStrategy simpleNa;
    private RightHandRuleStrategy rightHandRuleStrategy;

    public FormCollectionStrategy(Maze maze, IFormManager iFormManager){
        this.maze = maze;
        this.iFormManager = iFormManager;
        this.formPositions = new HashMap<>();
        this.myPlayerId = -1;
        this.simpleNa = new SimpleNavigationStrategy(maze);
        this.rightHandRuleStrategy = new RightHandRuleStrategy();
        System.err.println("FormCollectionStrategy initialisiert");
    }

    @Override
    public void setPlayerId(int playerId){
        this.myPlayerId=playerId;
        this.simpleNa.setPlayerId(playerId);
        this.rightHandRuleStrategy.setPlayerId(playerId);
        System.err.println("FormCollectionStrategy: PlayerId " + playerId + " gesetzt");
    }

    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, String southCell,String westCell) {

        // PHASE 1: Merke dir alle sichtbaren Formulare
        rememberFormPositions(currentPos, northCell, eastCell, southCell, westCell);
        System.err.println("rememberFormPositions");
        // PHASE 2: Aktualisiere Ziel Position
        updateTargetPosition(currentPos);
        System.err.println("updateTargetPosition");
        //PHASE 3: Entscheide Navigation
        return navigateToTarget(currentPos, northCell, eastCell, southCell, westCell);
    }

    public void setTargetPosition(Position position){
        this.targetPosition = position;
        System.err.println("Ziel Position gesetzt: " + position);
    }

    public Position getTargetPosition(){
        return targetPosition;
    }

    //Merkt sich alle sichtbaren Formulare
    private void rememberFormPositions(Position currentPos, String northCell, String eastCell, String southCell, String westCell){
        rememberFormPositionInDirection(currentPos, northCell, "north");
        rememberFormPositionInDirection(currentPos, eastCell,  "east");
        rememberFormPositionInDirection(currentPos, southCell, "south");
        rememberFormPositionInDirection(currentPos, westCell,  "west");
    }

    private void rememberFormPositionInDirection(Position currentPos, String cellStatus, String direction){
        if (cellStatus == null || !cellStatus.startsWith("FORM")) {
            return;
        }
        if (cellStatus.startsWith("FORM")) {
            try {
                String[] parts = cellStatus.split(" ");
                if (parts.length < 3) {
                    return;
                }
                int formPlayerId = Integer.parseInt(parts[1]);
                int formId = Integer.parseInt(parts[2]);

                // Nur eigene Formulare merken
                if (formPlayerId == myPlayerId) {
                    Position formPos = calculateFormPosition(currentPos, direction);
                    //Nur merken wenn noch nicht gespeichert
                    if (!formPositions.containsKey(formId)) {
                        formPositions.put(formId, formPos);
                        System.err.println("Formular " + formId + " gemerkt bei Position: " + formPos);
                    }
                }
            } catch (NumberFormatException e) {
                //Ingno.
            }
        }
    }

    private Position calculateFormPosition(Position currentPos, String direction){
        switch (direction) {
            case "north": return new Position(currentPos.getX(), currentPos.getY()-1);
            case "east": return new Position(currentPos.getX()+1, currentPos.getY());
            case "south": return new Position(currentPos.getX(), currentPos.getY() + 1);
            case "west":  return new Position(currentPos.getX() - 1, currentPos.getY());
            default:
            return currentPos;
        }
    }

    private void updateTargetPosition(Position currentPos){
        //PRIORITÄT 1: Nächstes benötigtes Formular
        int nextFormId = iFormManager.getNextFormId();
        System.err.println("nextFormId: ==> " +nextFormId );
        Position nextFormPos = formPositions.get(nextFormId);
        System.err.println("nextFormPos: ==> " +nextFormPos );
        System.err.println("targetPosition ==> " + targetPosition );

        if (nextFormPos != null && !nextFormPos.equals(currentPos)) {
            targetPosition = nextFormPos;
            System.err.println("targetPosition:" + targetPosition + "= nextFormPos:"  +nextFormPos );
            System.err.println("Navigiere zu Formular"+ nextFormId + "bei" + targetPosition);
            return;
        }
        if (targetPosition != nextFormPos) {
            //PRIORITÄT 2: Irgendein bekanntes Formular (für Exploration)
            for (Map.Entry<Integer,Position> entry : formPositions.entrySet()) {
                if (!entry.getValue().equals(currentPos)) {
                    targetPosition = entry.getValue();
                    System.err.println("Mavigiere zu bekannten Formular" + entry.getKey() + "bei " + targetPosition);
                    return;
                }
            }
        }
        
        //PRIORITÄT 3: Kein Formular bekannt also Exploration-Modus
        targetPosition = null;
        System.err.println("Kein Formular bekannt ==> Exploration-Modus");
    }

    // Navigation mit bessere Exploration
    private String navigateToTarget(Position currentPos, String northCell, String eastCell, String southCell, String westCell){
        //Wenn kein Ziel bekannt: Nutze SimpleNavigation für systematische Exploration
        if (targetPosition == null) {
            String move = simpleNa.decideNextMove(currentPos, northCell, eastCell, southCell, westCell);
            String move2 = rightHandRuleStrategy.decideNextMove(currentPos, northCell, eastCell, southCell, westCell);
            if (move != null) {
                return move;
            }
            if (move2 != null) {
                return move2;
            }
        }
        //Wenn Ziel bekannt: Navigiere dorthin (Manhattan-Distanz)
        int dx = targetPosition.getX() - currentPos.getX();
        int dy = targetPosition.getY() - currentPos.getY();
        
        ///Versuche zuerst die längere Distanz
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal bewegen
            if (dx > 0 && !eastCell.equals("WALL") && 
                !INavigationStrategy.isOtherPlayersFinish(eastCell, myPlayerId)) {
                return "go east";
            } else if (dx < 0 && !westCell.equals("WALL") && !INavigationStrategy.isOtherPlayersFinish(westCell, myPlayerId)) {
                return "go west";
            }
        }
        
        // Vertikal bewegen
        if (dy > 0 && !southCell.equals("WALL") && 
            !INavigationStrategy.isOtherPlayersFinish(southCell, myPlayerId)) {
            return "go south";
        } else if (dy < 0 && !northCell.equals("WALL") && !INavigationStrategy.isOtherPlayersFinish(northCell, myPlayerId)) {
            return "go north";
        }
        // Fallback: Nutze SimpleNavigation
        System.err.println("Direkter Weg blockiert  nutze SimpleNavigation");
        return /*rightHandRuleStrategy.decideNextMove(currentPos, northCell, eastCell, southCell, westCell);*/ /*null*/simpleNa.decideNextMove(currentPos, northCell, eastCell, southCell, westCell);
        //return useSimpleNavigation(currentPos, northCell, eastCell, southCell, westCell);
    }


    // KOMPLETT NEU - Intelligente Navigation mit Endlosschleifen-Erkennung
    /*private String useSimpleNavigation(Position currentPos, String northCell, String eastCell, String southCell, String westCell){
        String[] directions = {"west", "north", "east", "south"};
        String[] cells = {westCell, northCell, eastCell, southCell};
        
        String bestDirection = null;
        int minVisits = Integer.MAX_VALUE;
        
        // Phase 1: Suche nach unbesuchten oder wenig besuchten Positionen
        for (int i = 0; i < 4; i++) {
            if (INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                continue;
            }
            
            if (!cells[i].equals("WALL")) {
                Position nextPos = currentPos.move(directions[i]);
                int visits = maze.getVisitCount(nextPos);
                
                // STARK bevorzuge unbesuchte Felder
                if (visits == 0) {
                    System.err.println("âœ¨ Unbesuchtes Feld gefunden: " + directions[i]);
                    return "go " + directions[i];
                }
                
                if (visits < minVisits) {
                    minVisits = visits;
                    bestDirection = "go " + directions[i];
                }
            }
        }
        
        // Phase 2: Endlosschleifen-Erkennung - Wenn alle Richtungen stark besucht
        if (minVisits >= 4) {
            System.err.println("âš ï¸ Endlosschleife erkannt (min " + minVisits + " Besuche) - Suche Ausbruch");
            
            // WÃ¤hle jetzt gezielt eine andere Strategie: 
            // Nimm die Richtung die am WEITESTEN vom bisherigen Pfad abweicht
            for (int i = 0; i < 4; i++) {
                if (!cells[i].equals("WALL") && !INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                    Position nextPos = currentPos.move(directions[i]);
                    int visits = maze.getVisitCount(nextPos);
                    
                    // Akzeptiere auch stark besuchte Felder um rauszukommen
                    if (visits <= minVisits + 2) {
                        System.err.println("ðŸš€ Escape-Route: " + directions[i] + " (" + visits + " Besuche)");
                        return "go " + directions[i];
                    }
                }
            }
        }
        
        // Phase 3: Fallback
        if (bestDirection == null) {
            System.err.println("âš ï¸ Kein optimaler Pfad - nehme erste freie Richtung");
            for (int i = 0; i < 4; i++) {
                if (!cells[i].equals("WALL") && !INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                    return "go " + directions[i];
                }
            }
        }
        
        return bestDirection;
    }*/

}