package main.java.de.fhms.eti.players.domain;

/**
 * Position stellt eine Koordinate im Labyrinth
 * <p>
 * Verantwortung(SRP):
 * - Speichert x und y Koordinaten
 * - Berechnet neue Positionen nach Bewegung
 * - Stellt eine Vergleichbarkeit sicher (für Hashset/HashMap)
 *
 * @param x Horizontal
 * @param y Vertikal
 */

public record Position(int x, int y) {
    /**
     * Konstruktor erstellt eine neue Position
     *
     * @param x X Koordinate
     * @param y Y Koordinate
     */
    public Position {
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
        if (!(obj instanceof Position(int x1, int y1))) return false;
        return this.x == x1 && this.y == y1;
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
