package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class MarinTest {

    Marin sailor;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 3, 2, "Tom Pouce");
    }

    @Test
    void getId() {
        assertEquals(1, sailor.getId());
    }

    @Test
    void getX() {
        assertEquals(3, sailor.getX());
    }

    @Test
    void getY() {
        assertEquals(2, sailor.getY());
    }

    @Test
    void getGridPosition() {
        assertEquals(Pair.of(3,2), sailor.getGridPosition());
    }

    @Test
    void getName() {
        assertEquals("Tom Pouce", sailor.getName());
    }

    @Test
    void moveRelative() {
        sailor.moveRelative(2, 1);
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveRelative(4,4));
    }

    @Test
    void testMoveRelative() {
        sailor.moveRelative(Pair.of(2, 1));
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveRelative(Pair.of(4, 4)));
    }

    @Test
    void moveAbsolute() {
        sailor.moveAbsolute(5, 3);
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveAbsolute(17,6));
    }

    @Test
    void testMoveAbsolute() {
        sailor.moveAbsolute(Pair.of(5,3));
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveAbsolute(Pair.of(17, 6)));
    }

    @Test
    void isRelPosReachable() {
        assertTrue(sailor.isRelPosReachable(2,1));
        assertFalse(sailor.isRelPosReachable(4,4));
    }

    @Test
    void isAbsPosReachable() {
        assertTrue(sailor.isAbsPosReachable(5,3));
        assertFalse(sailor.isAbsPosReachable(7,6));
    }
}