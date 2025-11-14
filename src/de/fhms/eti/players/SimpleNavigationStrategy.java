package de.fhms.eti.players;

/**
 * SimpleNavigationStrategy - Intelligente Navigationsstrategie
 * mit KORRIGIERTER FINISH-Priorität und Formular-Suche
 */
public class SimpleNavigationStrategy implements INavigationStrategy {
    private Maze maze;
    private int myPlayerId;
    
    public SimpleNavigationStrategy(Maze maze) {
        this.maze = maze;
        this.myPlayerId = -1;  // Initial -1, wird später gesetzt
    }
    
    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, 
                                String southCell, String westCell) {
        
        // KRITISCH: FINISH-Priorität MUSS ZUERST kommen!
        String finishDir = checkFinishPriority(northCell, eastCell, southCell, westCell);
        if (finishDir != null) {
            System.err.println("FINISH in Sichtweite: " + finishDir);
            return finishDir;
        }
        
        // Arrays für systematisches Durchgehen
        String[] directions = {"west", "north", "east", "south"};
        String[] cells = {westCell, northCell, eastCell, southCell};
        
        String bestDirection = null;
        int minVisits = Integer.MAX_VALUE;
        
        // Phase 1: Finde beste Richtung (ignoriere fremde FINISH)
        for (int i = 0; i < 4; i++) {
            // Überspringe fremde Ziele
            if (INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                continue;
            }
            
            // Ist Richtung frei?
            if (!cells[i].equals("WALL")) {
                Position nextPos = currentPos.move(directions[i]);
                int visits = maze.getVisitCount(nextPos);
                
                // Wähle Richtung mit wenigsten Besuchen
                if (visits < minVisits) {
                    minVisits = visits;
                    bestDirection = "go " + directions[i];
                }
            }
        }
        
        // Phase 2: Fallback falls keine gute Richtung gefunden
        if (bestDirection == null) {
            System.err.println("Keine optimale Richtung - verwende Fallback");
            for (int i = 0; i < 4; i++) {
                if (!cells[i].equals("WALL")) {
                    bestDirection = "go " + directions[i];
                    break;
                }
            }
        }
        
        return bestDirection;
    }
    
    /**
     * Prüft ob eigenes FINISH in Sichtweite ist
     * 
     * WICHTIG: Format ist "FINISH playerId formCount" z.B. "FINISH 1 2"
     * Wir müssen nur auf "FINISH 1" (playerId) prüfen, nicht auf exakten String!
     */
    private String checkFinishPriority(String northCell, String eastCell, 
                                      String southCell, String westCell) {
        // Prüfe ob myPlayerId gesetzt ist
        if (myPlayerId == -1) {
            System.err.println("myPlayerId noch nicht gesetzt!");
            return null;
        }
        
        // Prüfe jede Richtung auf eigenes FINISH
        if (isMyFinish(northCell)) {
            System.err.println("Eigenes FINISH im Norden gefunden!");
            return "go north";
        }
        if (isMyFinish(eastCell)) {
            System.err.println("Eigenes FINISH im Osten gefunden!");
            return "go east";
        }
        if (isMyFinish(southCell)) {
            System.err.println("Eigenes FINISH im Süden gefunden!");
            return "go south";
        }
        if (isMyFinish(westCell)) {
            System.err.println("Eigenes FINISH im Westen gefunden!");
            return "go west";
        }
        
        return null;  // Kein eigenes FINISH in Sichtweite
    }
    
    /**
     * Hilfsmethode um zu prüfen ob eine Zelle mein FINISH ist
     */
    private boolean isMyFinish(String cellStatus) {
        if (cellStatus == null || !cellStatus.startsWith("FINISH")) {
            return false;
        }
        
        try {
            String[] parts = cellStatus.split(" ");
            if (parts.length >= 2) {
                int finishPlayerId = Integer.parseInt(parts[1]);
                return (finishPlayerId == myPlayerId);
            }
        } catch (NumberFormatException e) {
            System.err.println("Fehler beim Parsen: " + cellStatus);
        }
        
        return false;
    }


    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
        System.err.println("impleNavigationStrategy: PlayerId gesetzt auf " + playerId);
    }
}