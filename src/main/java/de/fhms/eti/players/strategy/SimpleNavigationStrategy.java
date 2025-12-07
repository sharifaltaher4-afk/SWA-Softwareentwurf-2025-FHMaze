package main.java.de.fhms.eti.players.strategy;
//SimpleNavigationStrategy - Intelligente Navigationsstrategie
//mit KORRIGIERTER FINISH-Priorität und Formular-Suche

import main.java.de.fhms.eti.players.domain.*;

public class SimpleNavigationStrategy implements INavigationStrategy {
    private final Maze maze;
    private int myPlayerId;

    public SimpleNavigationStrategy(Maze maze) {
        this.maze = maze;
        this.myPlayerId = -1;  // Initial -1, wird später gesetzt
    }

    @Override
    public GoAction decideNextMove(Position currentPos, Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {
        // KRITISCH: FINISH-Priorität MUSS ZUERST kommen
        GoAction finishDir = checkFinishPriority(northCell, eastCell, southCell, westCell);
        if (finishDir != null) {
            System.err.println("FINISH in Sichtweite: " + finishDir);
            return finishDir;
        }

        // Arrays für systematisches Durchgehen
        Direction[] directions = {Direction.west, Direction.north, Direction.east, Direction.south};
        Cell[] cells = {westCell, northCell, eastCell, southCell};

        Direction bestDirection = null;
        int minVisits = Integer.MAX_VALUE;

        // Phase 1: Finde beste Richtung (ignoriere fremde FINISH)
        for (int i = 0; i < 4; i++) {
            // Überspringe fremde Ziele
            if (INavigationStrategy.isOtherPlayersFinish(cells[i], myPlayerId)) {
                continue;
            }

            // Ist Richtung frei? (prüfe ob nicht Wand)
            if (!(cells[i] instanceof WallCell)) {
                Position nextPos = currentPos.move(directions[i]);
                int visits = maze.getVisitCount(nextPos);

                // Wähle Richtung mit wenigsten Besuchen
                if (visits < minVisits) {
                    minVisits = visits;
                    bestDirection = directions[i];
                }
            }
        }

        // Phase 2: Fallback falls keine gute Richtung gefunden
        if (bestDirection == null) {
            System.err.println("Keine optimale Richtung - verwende Fallback");
            for (int i = 0; i < 4; i++) {
                if (!(cells[i] instanceof WallCell)) {
                    bestDirection = directions[i];
                    break;
                }
            }
        }

        return new GoAction(bestDirection);
    }


    private GoAction checkFinishPriority(Cell northCell, Cell eastCell, Cell southCell, Cell westCell) {

        // Prüfe ob myPlayerId gesetzt ist
        if (myPlayerId == -1) {
            System.err.println("myPlayerId noch nicht gesetzt!");
            return null;
        }

        // Prüfe jede Richtung auf eigenes FINISH
        if (isMyFinish(northCell)) {
            System.err.println("Eigenes FINISH im Norden gefunden!");
            return new GoAction(Direction.north);
        }
        if (isMyFinish(eastCell)) {
            System.err.println("Eigenes FINISH im Osten gefunden!");
            return new GoAction(Direction.east);
        }
        if (isMyFinish(southCell)) {
            System.err.println("Eigenes FINISH im Süden gefunden!");
            return new GoAction(Direction.south);
        }
        if (isMyFinish(westCell)) {
            System.err.println("Eigenes FINISH im Westen gefunden!");
            return new GoAction(Direction.west);
        }

        return null;//Kein eigenes FINISH in Sichtweite

    }


    //Hilfsmethode um zu prüfen ob eine Zelle mein FINISH ist
    private boolean isMyFinish(Cell cellStatus) {
        if (!(cellStatus instanceof FinishCell finishCell)) {
            return false;
        }

        int foreignPlayerId = finishCell.getPlayerId();
        return foreignPlayerId == myPlayerId;
    }


    @Override
    public void setPlayerId(int playerId) {
        this.myPlayerId = playerId;
        System.err.println("impleNavigationStrategy: PlayerId gesetzt auf " + playerId);
    }
} 

