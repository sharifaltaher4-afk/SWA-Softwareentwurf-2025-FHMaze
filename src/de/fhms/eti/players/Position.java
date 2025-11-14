package de.fhms.eti.players;
/**
 * Position stellt eine Koordinate im Labyrinth
 * 
 * Verantwortung(SRP):
 *  - Speichert x und y Koordinaten
 *  - Berechnet neue Positionen nach Bewegung
 *  - Stellt eine Vergleichbarkeit sicher (fÃ¼r Hashset/HashMap)
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
     * Diese Methode Ã¤ndert die aktuelle Position nicht (immutable)
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
            default: return this; // Bei ungÃ¼ltige Richtung gibt die aktuelle Pos. zÃ¼ruck
        }
    }
    
    /**
     * Verhleicht zwei Posi. auf Gleichheit.
     * notwendig fÃ¼r Hashset und HashMap
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
     * Erzeugt einen Hash-Code fÃ¼r die Pos.
     * Notwendig fÃ¼r HashSet und HashMap
     * 
     * Positionen und den gleichen Koordinaten mÃ¼ssen den gleichen Hash-Code haben
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