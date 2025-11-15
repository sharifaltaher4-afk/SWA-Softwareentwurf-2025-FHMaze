package de.fhms.eti.players.Domain_Klassen;
/**
 * Position stellt eine Koordinate im Labyrinth
 * 
 * Verantwortung(SRP):
 *  - Speichert x und y Koordinaten
 *  - Berechnet neue Positionen nach Bewegung
 *  - Stellt eine Vergleichbarkeit sicher (für Hashset/HashMap)
 */

public class Position {
    private int x; // Horizontal
    private int y; // Vertikal
    
    /**
     * Konstruktor erstellt eine neue Position
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
     * @param direction Richtungen
     * @return Neue Position nch der Bewegung
     */
    public Position move(String direction) {
        switch(direction) {
            // Vorstellung als Koordinaten System...
            case "north": return new Position(x, y - 1);
            case "south": return new Position(x, y + 1);
            case "east":  return new Position(x + 1, y);
            case "west":  return new Position(x - 1, y);
            default: return this; // Bei ungültige Richtung gibt die aktuelle Pos. züruck
        }
    }
    
    /**
     * Verhleicht zwei Posi. auf Gleichheit.
     * notwendig für Hashset und HashMap
     * 
     * zwei Posi. sind nur dann gleich, wenn x und y identisch sind.
     * @param obj Objekt zum Vergleichen
     * @param true wenn Positionen gleich sonst false.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }
    
    /**
     * Erzeugt einen Hash-Code für die Pos.
     * Notwendig für HashSet und HashMap
     * 
     * Positionen und den gleichen Koordinaten müssen den gleichen Hash-Code haben
     * @return Hash-Code
     */
    @Override
    public int hashCode() {
        //Voraussetzung Das Labyrinth ist maximal 1000 breit (sizeX â‰¤ 1000)
        return x * 1000 + y;
    }

    /**
     * String-Representation for Debugging
     * @return Position als String, z.B. "(5,3)"
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}