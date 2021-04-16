package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest
{

    Position position;

    @BeforeEach
    void init()
    {
        position = new Position(10, 20, 30);
    }

    @Test
    void getX()
    {
        assertNotEquals(9.99, position.getX());
        assertEquals(10, position.getX());
        assertNotEquals(10.01, position.getX());
    }

    @Test
    void getY()
    {
        assertNotEquals(19.99, position.getY());
        assertEquals(20, position.getY());
        assertNotEquals(20.01, position.getY());
    }

    @Test
    void getOrientation()
    {
        assertNotEquals(29.99, position.getOrientation());
        assertEquals(30, position.getOrientation());
        assertNotEquals(30.01, position.getOrientation());
    }

    @Test
    void getLength()
    {
        Position pos2 = new Position(20, 30, 0);
        double distance = position.distance(pos2);
        assertEquals(14.142135623730951, distance);
        assertEquals(distance,
            sqrt(pow(position.getX() - pos2.getX(), 2) + pow(position.getY() - pos2.getY(), 2)));
    }

    @Test
    void testEquals()
    {
        assertEquals(position, position);
        assertNotEquals("Hello", position);
        assertEquals(new Position(10, 20, 30), position);
        assertNotEquals(new Position(10, 20, 31), position);
        assertNotEquals(new Position(10, 21, 30), position);
        assertNotEquals(new Position(11, 20, 30), position);
    }

    @Test
    void testHashCode()
    {
        assertNotEquals(new Position(10, 20, 29).hashCode(), position.hashCode());
    }

    @Test
    void isFacingPositionTest()
    {
        Position pos2 = new Position(10, 0, Math.PI / 2);
        assertTrue(pos2.isFacingPosition(position));
        assertFalse(position.isFacingPosition(pos2));
    }

    @Test
    void addTest()
    {
        Position pos2 = new Position(10, 0, Math.PI / 2);
        Position pos3 = position.add(pos2);
        Position pos4 = position.add(pos2.getX(), pos2.getY(), pos2.getOrientation());

        assertEquals(pos3, pos4);
        assertEquals(20, pos3.getX());
        assertEquals(20, pos3.getY());
        assertEquals(30 + Math.PI / 2, pos3.getOrientation());
    }

    @Test
    void toStringTest()
    {
        String posToString = "{" +
            "x=" + position.getX() +
            ", y=" + position.getY() +
            "; " + position.getOrientation() +
            " rad}";
        assertEquals(posToString, position.toString());
    }
}