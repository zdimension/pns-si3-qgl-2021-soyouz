package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolygonTest
{

    Polygon polygon;

    @BeforeEach
    void init()
    {
        Point[] vertices = {
            new Point(1, 1),
            new Point(2, 1),
            new Point(3, 1),
            new Point(4, 1),
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