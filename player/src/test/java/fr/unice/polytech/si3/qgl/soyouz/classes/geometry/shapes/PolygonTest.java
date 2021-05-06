package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolygonTest
{

    Polygon polygon;

    @BeforeEach
    void init()
    {
        Point2d[] vertices = {
            new Point2d(1, 1),
            new Point2d(2, 1),
            new Point2d(1, 0),
            new Point2d(2, 0),
        };
        polygon = new Polygon(1, vertices);
    }

    @Test
    void getOrientationTest()
    {
        assertEquals(1, polygon.getOrientation());
    }

    @Test
    void getVerticesTest()
    {
        assertEquals(4, polygon.getVertices().length);
    }

    @Test
    void getCenterTest()
    {
        assertEquals(1.5, polygon.getCenter().getX());
        assertEquals(0.5, polygon.getCenter().getY());
    }

    @Test
    void contains()
    {
        assertTrue(polygon.contains(new Point2d(1, 1)));
        assertTrue(polygon.contains(new Point2d(2, 1)));
        assertFalse(polygon.contains(new Point2d(2, 1.1)));
    }

    @Test
    void getMaxDiameter()
    {
        assertTrue(polygon.getMaxDiameter()>4.4 && polygon.getMaxDiameter()< 4.5);
    }

    @Test
    void getShell()
    {
        assertEquals(4, polygon.getShell(10).toArray().length);
    }

    @Test
    void linePassesThrough()
    {
        assertTrue(polygon.linePassesThrough(new Point2d(1.5, -2), new Point2d(1.5, 2), 5));
        assertFalse(polygon.linePassesThrough(new Point2d(3, -2), new Point2d(3, 2), 5));
        assertTrue(polygon.linePassesThrough(new Point2d(2, -2), new Point2d(2, 2), 5));
    }
}