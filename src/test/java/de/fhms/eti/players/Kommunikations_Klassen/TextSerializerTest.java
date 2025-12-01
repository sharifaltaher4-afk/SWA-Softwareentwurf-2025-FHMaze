package test.java.de.fhms.eti.players.Kommunikations_Klassen;

import main.java.de.fhms.eti.players.Domain_Klassen.*;
import main.java.de.fhms.eti.players.Kommunikations_Klassen.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class TextSerializerTest {
    private TextSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new TextSerializer();
    }

    @Test
    void testWallSerialization() {
        Cell cell = serializer.serializeCellStatus("WALL");
        assertInstanceOf(WallCell.class, cell);
        assertFalse(cell.isAccessable());
    }

    @Test
    void testFloorSerialization() {
        Cell cell = serializer.serializeCellStatus("FLOOR");
        assertInstanceOf(FloorCell.class, cell);
        assertTrue(cell.isAccessable());
    }

    @Test
    void testFinishSerialization() {
        Cell cell = serializer.serializeCellStatus("FINISH 1024 55");
        assertInstanceOf(FinishCell.class, cell);
        assertTrue(cell.isAccessable());
        assertEquals(1024, ((FinishCell) cell).getPlayerId());
        assertEquals(55, ((FinishCell) cell).getFormCount());
    }

    @Test
    void testFormSerialization() {
        Cell cell = serializer.serializeCellStatus("FORM 1024 3");
        assertInstanceOf(FormCell.class, cell);
        assertTrue(cell.isAccessable());
        assertEquals(1024, ((FormCell) cell).getPlayerId());
        assertEquals(3, ((FormCell) cell).getFormId());
    }
}
