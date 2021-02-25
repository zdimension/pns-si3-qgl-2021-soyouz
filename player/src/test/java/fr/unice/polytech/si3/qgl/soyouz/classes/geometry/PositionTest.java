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
        assertNotEquals(new Position(10, 20, 29).hashCode(), position.hashCode());
    }

    @Test
    void isFacingPositionTest() {
        Position pos2 = new Position(10, 0, Math.PI/2);
        assertTrue(pos2.isFacingPosition(position));
        assertFalse(position.isFacingPosition(pos2));
    }

    @Test
    void addTest() {
        Position pos2 = new Position(10, 0, Math.PI/2);
        Position pos3 = position.add(pos2);
        Position pos4 = position.add(pos2.getX(), pos2.getY(), pos2.getOrientation());

        assertEquals(pos3, pos4);
        assertEquals(20, pos3.getX());
        assertEquals(20, pos3.getY());
        assertEquals(30 + Math.PI/2, pos3.getOrientation());
    }

    @Test
    void toStringTest() {
        String posToString = "{" +
                "x=" + position.getX() +
                ", y=" + position.getY() +
                "; " + position.getOrientation() +
                " rad}";
        assertEquals(posToString, position.toString());
    }
}