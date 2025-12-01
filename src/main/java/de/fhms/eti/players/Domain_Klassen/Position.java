package main.java.de.fhms.eti.players.Domain_Klassen;

/**
 * Position stellt eine Koordinate im Labyrinth
 * <p>
 * Verantwortung(SRP):
 * - Speichert x und y Koordinaten
 * - Berechnet neue Positionen nach Bewegung
 * - Stellt eine Vergleichbarkeit sicher (für Hashset/HashMap)
 */

public class Position {
    private final int x; // Horizontal
    private final int y; // Vertikal

    /**
     * Konstruktor erstellt eine neue Position
     *
     * @param x X Koordinate
     * @param y Y Koordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    // Neue Position nach Bewegung berechnen

    /**
     * Berechnet eine neue Position basierend auf einer Bewegungsrichtung.
     * Diese Methode ändert die aktuelle Position nicht (immutable)
     * sondern erstellt eine neue Position
     *
     * @param direction Richtungen
     * @return Neue Position nch der Bewegung
     */
    public Position move(Direction direction) {
        return switch (direction) {
            // Vorstellung als Koordinaten System...
            case north -> new Position(x, y - 1);
            case south -> new Position(x, y + 1);
            case east -> new Position(x + 1, y);
            case west -> new Position(x - 1, y);
            default -> this; // Bei ungültige Richtung gibt die aktuelle Pos. züruck
        };
    }

    /**
     * Verhleicht zwei Posi. auf Gleichheit.
     * notwendig für Hashset und HashMap
     * <p>
     * zwei Posi. sind nur dann gleich, wenn x und y identisch sind.
     *
     * @param obj Objekt zum Vergleichen
     * @return true wenn Positionen gleich sonst false.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position other)) return false;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Erzeugt einen Hash-Code für die Pos.
     * Notwendig für HashSet und HashMap
     * <p>
     * Positionen und den gleichen Koordinaten müssen den gleichen Hash-Code haben
     *
     * @return Hash-Code
     */
    @Override
    public int hashCode() {
        //Voraussetzung Das Labyrinth ist maximal 1000 breit (sizeX â‰¤ 1000)
        return x * 1000 + y;
    }

    /**
     * String-Representation for Debugging
     *
     * @return Position als String, z.B. "(5,3)"
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
