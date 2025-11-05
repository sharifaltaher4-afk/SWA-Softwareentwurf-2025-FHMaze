package de.fhms.eti.players;

/**
 * MyBot Der intelligente Bot für FHMaze mit Multiplayer-Unterstützung
 * 
 * ERWEITERT: Erkennt und ignoriert Ziele anderer Spieler,
 * navigiert nur zum eigenen FINISH-Ziel.
 */
public class MyBot {
    
    /**
     * Hauptmethode - Startet den Bot und verwaltet den Spielablauf
     * 
     * @param args Kommandozeilenargumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        // Initialisiere Kommunikation mit der Spielumgebung
        GameCommunication comm = new GameCommunication();
        
        // PHASE 1: INITIALISIERUNG - Hole Startinformationen vom Spiel
        InitData initData = comm.readInit();
        
        // Erstelle Labyrinth-Gedächtnis mit den empfangenen Daten
        Maze maze = new Maze(initData.sizeX, initData.sizeY, initData.level);
        
        // Merke dir deine eigene Spieler-ID
        // Diese wird benötigt um eigene vs. fremde Ziele zu unterscheiden
        int myPlayerId = initData.playerId;
        System.err.println("Bot initialisiert - ID: " + myPlayerId + 
                          ", Startposition: " + initData.startPosition);
        
        // ERWEITERT: Strategie mit Player-ID erstellen
        // Die Strategy braucht unsere ID um fremde Ziele zu erkennen
        INavigationStrategy strategy = new SimpleNavigationStrategy(maze, myPlayerId);
        Navigator navigator = new Navigator(maze, initData.startPosition, strategy);
        
        // PHASE 2: HAUPTSPIELSCHLEIFE - Verarbeite Runde für Runde
        TurnData turnData;
        while ((turnData = comm.readTurn()) != null) {
            
            // ENTSCHEIDEND: Prüfe ob ich auf MEINEM eigenen Ziel stehe
            if (turnData.currentCell.startsWith("FINISH")) {
                // Teile den FINISH-String: "FINISH playerId x y"
                String[] parts = turnData.currentCell.split(" ");
                
                if (parts.length >= 2) {
                    try {
                        // Extrahiere die Player-ID aus dem FINISH-String
                        int finishPlayerId = Integer.parseInt(parts[1]);
                        
                        // Nur wenn es MEIN eigenes Ziel ist!
                        if (finishPlayerId == myPlayerId) {
                            // GEWONNEN! Sende finish-Befehl und beende das Spiel
                            comm.sendAction("finish");
                            System.err.println("ZIEL ERREICHT! Bot " + myPlayerId + " hat gewonnen!");
                            break;
                        } else {
                            // Das ist ein fremdes Ziel - IGNORIEREN und weiterspielen
                            System.err.println("Stehe auf fremdem Ziel von Spieler " + finishPlayerId + " - ignoriere!");
                        }
                    } catch (NumberFormatException e) {
                        // Falls Parsing fehlschlägt (sollte nicht passieren)
                        System.err.println("Konnte FINISH-Format nicht parsen: " + turnData.currentCell);
                    }
                }
            }
            
            // PHASE 3: BEWEGUNGSENTSCHEIDUNG - Strategie wählt nächsten Schritt
            // Die SimpleNavigationStrategy ignoriert automatisch fremde Ziele
            String action = navigator.decideNextMove(
                turnData.northCell,  // Was ist nördlich?
                turnData.eastCell,   // Was ist östlich?
                turnData.southCell,  // Was ist südlich?
                turnData.westCell    // Was ist westlich?
            );
            
            // SICHERHEIT: Stelle sicher, dass action nicht null ist
            // (kann passieren wenn alle Richtungen blockiert sind)
            if (action == null) {
                System.err.println("Keine optimale Richtung gefunden! Verwende Fallback.");
                
                // Fallback-Strategie: Nimm irgendeine freie Richtung
                if (!turnData.northCell.equals("WALL")) {
                    action = "go north";
                } else if (!turnData.eastCell.equals("WALL")) {
                    action = "go east";
                } else if (!turnData.southCell.equals("WALL")) {
                    action = "go south";
                } else if (!turnData.westCell.equals("WALL")) {
                    action = "go west";
                } else {
                    action = "go north"; // Letzter Fallback (sollte nie passieren)
                }
            }
            
            // Sende den Bewegungsbefehl an die Spielumgebung
            comm.sendAction(action);
            
            // Aktualisiere unsere interne Position
            navigator.updatePosition(action);
            
            // Debug-Ausgabe für bessere Nachverfolgung
            System.err.println("Bot " + myPlayerId + " bewegt: " + action + 
                             ", Position: " + navigator.getCurrentPosition());
        }
        
        // PHASE 4: SPIELENDE - Räume Ressourcen auf
        comm.close();
        System.err.println("Bot " + myPlayerId + " beendet.");
    }
}