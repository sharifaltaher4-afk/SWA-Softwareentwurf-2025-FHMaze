package de.fhms.eti.players;

public class MyBot {
    
    public static void main(String[] args) {
		// GameCommunication nutzen
        GameCommunication comm = new GameCommunication();
        
        // INIT
        InitData initData = comm.readInit();
        Maze maze = new Maze(initData.sizeX, initData.sizeY, initData.level);
        
        // Strategie auswählen
        INavigationStrategy strategy = new SimpleNavigationStrategy(maze);
        
        Navigator navigator = new Navigator(maze, initData.startPosition, strategy);
        
        // TURN-Schleife
        TurnData turnData;
        while ((turnData = comm.readTurn()) != null) {
            
            // Ziel gefunden?
            if (turnData.currentCell.startsWith("FINISH")) {
                comm.sendAction("finish");
                break;
            }
            
            // Entscheidung treffen
            String action = navigator.decideNextMove(
                turnData.northCell,
                turnData.eastCell,
                turnData.southCell,
                turnData.westCell
            );
            
            // Aktion senden
            comm.sendAction(action);
            
            // Position aktualisieren
            navigator.updatePosition(action);
        }
        
        comm.close();
    }
}

/* 
package de.fhms.eti.players;

import java.util.HashSet;
import java.util.Scanner;

public class MyBot {
    

	
	 // Hauptmethode zum Ausf�hren des Bots
	 //   @param args
	 
	public static void main(String[] args) {
		// Scanner zum Auslesen der Standardeingabe, welche Initialisierungs- und Rundendaten liefert
		Scanner input = new Scanner(System.in);


		// INIT - Auslesen der Initialdaten
		// 1. Zeile: Maze Infos
		int sizeX = input.nextInt(); // X-Groesse des Spielfeldes (Breite)
		int sizeY = input.nextInt(); // Y-Groesse des Spielfeldes (Hoehe)
		int level = input.nextInt(); // Level des Matches
		input.nextLine(); // Beenden der ersten Zeile
		// 2. Zeile: Player Infos
		int playerId = input.nextInt(); // id dieses Players / Bots
		int startX = input.nextInt(); // X-Koordinate der Startposition dieses Player
		int startY = input.nextInt(); // Y-Koordinate der Startposition dieses Players
		input.nextLine(); // Beenden der zweiten Zeile

		// Aktuelle Position initialisieren
		int currentX = startX;
		int currentY = startY;
		//Gedächtnis: Welche Positionen habe ich schom besuchet ?
		HashSet<String> visited = new HashSet<>();
		visited.add(currentX + "," + currentY); 
		
		// TURN (Wiederholung je Runde notwendig)
		while(input.hasNext()) {
			// Rundeninformationen auslesen
			String lastActionsResult = input.nextLine();
			String currentCellStatus = input.nextLine();
			String northCellStatus = input.nextLine();
			String eastCellStatus = input.nextLine();
			String southCellStatus = input.nextLine();
			String westCellStatus = input.nextLine();

			// Habe ich das Ziel gefunden ?
			if (currentCellStatus.startsWith("FINISH")) {
				System.out.println("finish");
				break;
			}
			
			// Prüfe alle Richtungen und wähle die beste
			String bestDirection = null;
			
			// west prüfen
			if (!(westCellStatus.equals("WALL"))) {
				String nextPos = (currentX-1) + "," + currentY;
				if (!(visited.contains(nextPos))) {
					bestDirection = "go west";
					currentX--;
				}
			}
			// Nord prüfen (falls West nicht gut war)
            if (bestDirection == null && !northCellStatus.equals("WALL")) {
                String nextPos = currentX + "," + (currentY - 1);
                if (!visited.contains(nextPos)) {
                    bestDirection = "go north";
                    currentY--;
                }
            }
            
            // Ost prüfen
            if (bestDirection == null && !eastCellStatus.equals("WALL")) {
                String nextPos = (currentX + 1) + "," + currentY;
                if (!visited.contains(nextPos)) {
                    bestDirection = "go east";
                    currentX++;
                }
            }
            
            // Süd prüfen
            if (bestDirection == null && !southCellStatus.equals("WALL")) {
                String nextPos = currentX + "," + (currentY + 1);
                if (!visited.contains(nextPos)) {
                    bestDirection = "go south";
                    currentY++;
                }
            }

	
			// Debug Information ausgeben (optional m�glich)
			System.err.println("Ergebnis Vorrunde: " + lastActionsResult);

            // Rundenaktion ausgeben
            //System.out.println("go west");// Immer Nach Westen gehen !!!

            // Wenn alle Richtungen besucht: nimm irgendeine freie
			if (bestDirection == null) {
				if(!(westCellStatus.equals("WALL"))){
					bestDirection = "go west";
					currentX--;
				}else if (!(northCellStatus.equals("WALL"))) {
					bestDirection = "go north";
					currentY--;
				}else if (!(eastCellStatus.equals("WALL"))) {
					bestDirection = "go east";
					currentX++;
				}else if (!(southCellStatus.equals("WALL"))){
					bestDirection = "go south";
					currentY++;
				}
			}


			
			// merken: Wo ware ich schon? (besuchte Felder).
			 
			visited.add(currentX + "," + currentY);
            System.out.println(bestDirection);
		}
		
		// Eingabe schliessen (letzte Aktion)
		input.close();
	}
}
*/