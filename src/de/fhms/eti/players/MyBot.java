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