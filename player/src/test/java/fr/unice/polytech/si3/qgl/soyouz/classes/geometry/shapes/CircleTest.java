package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest
{
    Circle circle;

    @BeforeEach
    void init()
    {
        circle = new Circle(50);
    }

    @Test
    void getRadius()
    {
        assertNotEquals(49.99, circle.getRadius());
        assertEquals(50, circle.getRadius());
        assertNotEquals(50.01, circle.getRadius());
    }

    @Test
    void contains()
    {
        assertTrue(circle.contains(new Point2d(50, 0)));
        assertTrue(circle.contains(new Point2d(0, 50)));
        assertFalse(circle.contains(new Point2d(50, 1)));
        assertFalse(circle.contains(new Point2d(1, 50)));
        assertTrue(circle.contains(new Point2d(25, 25)));
    }

    @Test
    void getMaxDiameterTest()
    {
        assertNotEquals(99.1, circle.getMaxDiameter());
        assertEquals(100, circle.getMaxDiameter());
        assertNotEquals(100.1, circle.getMaxDiameter());
    }

    @Test
    void linePassThroughTest()
    {
        assertTrue(circle.linePassesThrough(new Point2d(-100, 5), new Point2d(100, 5), 10));
        assertFalse(circle.linePassesThrough(new Point2d(-100, 70), new Point2d(100, 70), 10));
    }

    @Test
    void getShellInternalTest()
    {
        assertEquals(16, circle.getShellInternal(10).length);
        assertEquals(16, circle.getShellInternal(5).length);
    }
}