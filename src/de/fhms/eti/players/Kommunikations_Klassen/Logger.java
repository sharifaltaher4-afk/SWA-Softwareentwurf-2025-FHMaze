package de.fhms.eti.players.Kommunikations_Klassen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhms.eti.players.Domain_Klassen.Action;
import de.fhms.eti.players.Domain_Klassen.Cell;
import de.fhms.eti.players.Domain_Klassen.Position;

public class Logger {

    private static Logger instance;
    private FileWriter logWriter;
    private SimpleDateFormat dateFormat;

    private String mazeName;
    private int playerId;
    private int level;

    private static final int MAX_LINES = 2000;
    private int lineCount = 0;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void initialize(String mazeName, int playerId, int level) {
        this.mazeName = mazeName;
        this.playerId = playerId;
        this.level = level;

        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            File file = new File("bot.log");

            if (file.exists()) {
                file.delete();
            }

            logWriter = new FileWriter(file, false);

            log("BOT GESTARTET - Labyrinth: " + mazeName +
                ", Spieler: " + playerId + ", Level: " + level);

        } catch (IOException e) {
            System.err.println("Logger-Error: " + e.getMessage());
        }
    }

    public void log(String message) {
        if (lineCount > MAX_LINES) return;
        lineCount++;

        String timestamp = dateFormat.format(new Date());
        String logMessage = "[" + timestamp + "] " + message + "\n";

        // WICHTIG: nur stderr!
        System.err.print(logMessage);

        if (logWriter != null) {
            try {
                logWriter.write(logMessage);
                logWriter.flush();
            } catch (IOException e) {
                System.err.println("Logger-Error: " + e.getMessage());
            }
        }
    }

    public void logMovement(Position from, Position to, Action action, String reason) {
        log("BEWEGUNG: " + from + " -> " + to + " | Aktion: " + action + " | Grund: " + reason);
    }

    public void logCellStatus(Position pos, Cell current, Cell north,
                              Cell east, Cell south, Cell west) {
        log("ZELLSTATUS bei " + pos + ":");
        log("Aktuell: " + current);
        log("Nord: " + north + " | Ost: " + east + " | Süd: " + south + " | West: " + west);
    }

    public void logFormInfo(Cell cellStatus, boolean canTake, String reason) {
        log("FORMULAR: " + cellStatus + " | Kann nehmen: " + canTake + " | Grund: " + reason);
    }

    public void logLevelDetection(Cell finishCell, int detectedLevel) {
        log("LEVEL-ERKENNUNG: FINISH='" + finishCell + "' -> Level " + detectedLevel);
    }

    public void logStrategyChange(String fromStrategy, String toStrategy) {
        log("STRATEGIE-WECHSEL: " + fromStrategy + " -> " + toStrategy);
    }

    public void logCollectedForms(java.util.List<Integer> forms, int nextExpected) {
        log("GESAMMELTE FORMULARE: " + forms + " | Nächstes erwartet: " + nextExpected);
    }

    public void close() {
        log("BOT BEENDET - Labyrinth: " + mazeName +
            ", Spieler: " + playerId + ", Level: " + level);

        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                System.err.println("Logger-Error: " + e.getMessage());
            }
        }
    }
}
