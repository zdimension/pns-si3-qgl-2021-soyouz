package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolygonTest
{

    Polygon polygon;

    @BeforeEach
    void init()
    {
        Point2d[] vertices = {
            new Point2d(1, 1),
            new Point2d(2, 1),
            new Point2d(3, 1),
            new Point2d(4, 1),
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
}