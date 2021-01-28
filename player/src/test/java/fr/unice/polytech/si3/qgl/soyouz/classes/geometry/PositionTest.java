package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    Position position;

    @BeforeEach
    void init() {
        position = new Position(10, 20, 30);
    }

    @Test
    void getX() {
        assertEquals(10, position.getX());
    }

    @Test
    void getY() {
        assertEquals(20, position.getY());
    }

    @Test
    void getOrientation() {
        assertEquals(30, position.getOrientation());
    }

    @Test
    void getDistance() {
        Pair<Double, Double> dist = position.getDistance(new Position(20, 40, 60));
        assertEquals(10, dist.getFirst());
        assertEquals(20, dist.getSecond());
    }

    @Test
    void getPositionPlusPath() {
    }

    @Test
    void isPositionReachable() {
    }

    @Test
    void testEquals() {
        assertTrue(position.equals(position));
        assertFalse(position.equals("Hello"));
        assertTrue(position.equals(new Position(10, 20, 30)));
        assertFalse(position.equals(new Position(10, 20, 31)));
        assertFalse(position.equals(new Position(10, 21, 30)));
        assertFalse(position.equals(new Position(11, 20, 30)));
    }

    @Test
    void testHashCode() {
        assertNotNull(position.hashCode());
    }
}