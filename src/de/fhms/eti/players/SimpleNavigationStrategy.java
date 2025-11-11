package de.fhms.eti.players;

/**
 * SimpleNavigationStrategy Eine intelligente Navigationsstrategie
 * die andere Spieler-Ziele ignoriert und systematisch das Labyrinth erkundet.
 * 
 * ERWEITERT: Ignoriert FINISH-Zellen anderer Spieler, um zu verhindern,
 * dass der Bot zu fremden Zielen läuft.
 */
public class SimpleNavigationStrategy implements INavigationStrategy {
    private Maze maze;          // Labyrinth-Gedächtnis (besuchte Positionen)
    private int myPlayerId;     // Meine eigene Spieler-ID
    
    /**
     * Konstruktor: Initialisiert die Strategie mit Labyrinth und Spieler-ID
     * 
     * @param maze Das Labyrinth-Gedächtnis (für Besuchszähler)
     * @param playerId Die ID dieses Bots (um eigene vs. fremde Ziele zu unterscheiden)
     */
    public SimpleNavigationStrategy(Maze maze, int playerId) {
        this.maze = maze;
        this.myPlayerId = playerId;  // Speichere MEINE ID für Ziel-Erkennung
    }
    
    /**
     * Entscheidet die nächste Bewegung, ignoriert dabei Ziele anderer Spieler
     * 
     * Algorithmus:
     * 1. Prüfe alle 4 Richtungen (West, Nord, Ost, Süd)
     * 2. Überspringe Richtungen, die zu fremden Spieler-Zielen führen
     * 3. Wähle die Richtung mit den wenigsten Besuchen
     * 4. Falls alle Richtungen zu fremden Zielen führen: Nimm trotzdem eine freie Richtung
     * 
     * @param currentPos Aktuelle Position des Bots
     * @param northCell Status der nördlichen Zelle
     * @param eastCell Status der östlichen Zelle  
     * @param southCell Status der südlichen Zelle
     * @param westCell Status der westlichen Zelle
     * @return Bewegungsbefehl, z.B. "go north"
     */
    @Override
    public String decideNextMove(Position currentPos, String northCell, String eastCell, 
                                String southCell, String westCell) {
        
        // Arrays für systematisches Durchgehen aller Richtungen
        // Reihenfolge: West ==> Nord ==> Ost ==> Süd (Priorität)
        String[] directions = {"west", "north", "east", "south"};
        String[] cells = {westCell, northCell, eastCell, southCell};
        String finishDir = checkFinishPriority(northCell, eastCell, southCell, westCell);
        if (finishDir != null) return finishDir;
        
        String bestDirection = null;      // Bisher beste gefundene Richtung
        int minVisits = Integer.MAX_VALUE; // Minimale Besuchszahl (Start: sehr groß)
        
        // PHASE 1: Durchlaufe alle 4 Richtungen und finde die beste
        for (int i = 0; i < 4; i++) {
            // WICHTIG: Prüfe zuerst ob diese Zelle ein FINISH für einen ANDEREN Spieler ist
            if (isOtherPlayersFinish(cells[i])) {
                // Diese Richtung führt zu einem fremden Ziel ==> ÜBERSPRINGEN!
                // Der Bot wird diese Richtung komplett ignorieren und nicht dorthin gehen
                continue;  
            }
            
            // Ist diese Richtung frei (keine Wand)?
            if (!cells[i].equals("WALL")) {
                // Berechne die Position in dieser Richtung
                Position nextPos = currentPos.move(directions[i]);
                
                // Wie oft wurde diese Position bereits besucht?
                int visits = maze.getVisitCount(nextPos);
                
                // Ist diese Position weniger besucht als bisherige beste?
                if (visits < minVisits) {
                    minVisits = visits;  // Neue minimale Besuchszahl
                    bestDirection = "go " + directions[i];  // Neue beste Richtung
                }
            }
        }
        
        // PHASE 2: Fallback-Logik falls alle Richtungen zu fremden Zielen führen
        if (bestDirection == null) {
            System.err.println("Alle Richtungen führen zu fremden Zielen! Verwende Fallback.");
            
            // Durchlaufe nochmal alle Richtungen, aber jetzt OHNE Ziel-Prüfung
            for (int i = 0; i < 4; i++) {
                if (!cells[i].equals("WALL")) {
                    // Nimm die erste freie Richtung (auch wenn sie zu fremdem Ziel führt)
                    bestDirection = "go " + directions[i];
                    break;
                }
            }
        }
        
        return bestDirection;
    }
    
    /**
     *  Prüft ob eine Zelle ein FINISH für einen ANDEREN Spieler ist
     * 
     * Problem: In Multiplayer-Szenarien hat jeder Spieler sein eigenes FINISH-Ziel.
     * Wenn unser Bot zu einem fremden FINISH läuft, bleibt er dort stehen und verliert!
     * 
     * FINISH-Format im Spiel: "FINISH playerId x y"
     * Beispiel: 
     *   - "FINISH 0 5 3" → Ziel für Spieler 0
     *   - "FINISH 1 8 2" → Ziel für Spieler 1  
     *   - "FINISH 2 1 7" → Ziel für Spieler 2
     * 
     * @param cellStatus Der Zellstatus-String vom Spiel
     * @return true wenn die Zelle ein FINISH für einen ANDEREN Spieler ist, sonst false
     */
    private boolean isOtherPlayersFinish(String cellStatus) {
        // Prüfe ob die Zelle überhaupt ein FINISH ist
        if (cellStatus.startsWith("FINISH")) {
            // Teile den String in Teile: "FINISH", "playerId", "x", "y"
            String[] parts = cellStatus.split(" ");
            
            // Stelle sicher dass der String das erwartete Format hat
            if (parts.length >= 2) {
                try {
                    // Parse die Spieler-ID aus dem zweiten Teil
                    int finishPlayerId = Integer.parseInt(parts[1]);
                    
                    // ENTSCHEIDEND: Ist es NICHT mein eigenes Finish?
                    // - finishPlayerId != myPlayerId → fremdes Ziel ==> Ignorieren (return true)
                    // - finishPlayerId == myPlayerId → eigenes Ziel ==> OK (return false)
                    boolean isOtherPlayer = (finishPlayerId != myPlayerId);
                    
                    if (isOtherPlayer) {
                        System.err.println("Ignoriere fremdes Ziel für Spieler " + finishPlayerId);
                    }
                    
                    return isOtherPlayer;
                    
                } catch (NumberFormatException e) {
                    // Falls die Player-ID keine Zahl ist (sollte nicht passieren)
                    // Behandle es als normale Zelle, nicht als fremdes Ziel
                    System.err.println("Konnte Player-ID nicht parsen: " + cellStatus);
                    return false;
                }
            }
        }
        
        // Kein FINISH oder ungültiges Format → normale Zelle
        return false;
    }

    /**
     * Prüft, ob sich eine eigene FINISH-Zelle in Sichtweite befindet
     * und gibt deren Richtung als Bewegungsbefehl zurück.
     */
    private String checkFinishPriority(String northCell, String eastCell, String southCell, String westCell) {
        String target = "FINISH " + myPlayerId;
        if (northCell.startsWith(target)) return "go north";
        if (eastCell.startsWith(target))  return "go east";
        if (southCell.startsWith(target)) return "go south";
        if (westCell.startsWith(target))  return "go west";
        return null;
    }

}