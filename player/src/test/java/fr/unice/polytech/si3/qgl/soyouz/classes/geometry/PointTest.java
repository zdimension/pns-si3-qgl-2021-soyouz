package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PointTest
{

    Point2d point;

    @BeforeEach
    void init()
    {
        point = new Point2d(1,2);
    }

    @Test
    void getX()
    {
        assertNotEquals(0, point.getX());
        assertEquals(1, point.getX());
        assertNotEquals(2, point.getX());
    }

    @Test
    void getY()
    {
        assertNotEquals(1, point.getY());
        assertEquals(2, point.getY());
        assertNotEquals(3, point.getY());
    }
}