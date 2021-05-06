package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest
{

    Rectangle rectangle;

    @BeforeEach
    void init()
    {
        rectangle = new Rectangle(3, 6, 1);
    }

    @Test
    void getWidthTest()
    {
        assertNotEquals(2.99, rectangle.getWidth());
        assertEquals(3, rectangle.getWidth());
        assertNotEquals(3.01, rectangle.getWidth());
    }

    @Test
    void getHeightTest()
    {
        assertNotEquals(5.99, rectangle.getHeight());
        assertEquals(6, rectangle.getHeight());
        assertNotEquals(6.01, rectangle.getHeight());
    }

    @Test
    void getOrientationTest()
    {
        assertNotEquals(1.01, rectangle.getOrientation());
        assertEquals(1, rectangle.getOrientation());
        assertNotEquals(-1.01, rectangle.getOrientation());
    }

    @Test
    void contains()
    {
        assertTrue(rectangle.contains(new Point2d(1, 1)));
        assertTrue(rectangle.contains(new Point2d(2, 1)));
        assertTrue(rectangle.contains(new Point2d(3, 1)));
        assertFalse(rectangle.contains(new Point2d(4, 1)));
        assertFalse(rectangle.contains(new Point2d(1, 2)));
    }

    @Test
    void getMaxDiameter()
    {
        assertTrue(rectangle.getMaxDiameter() > 6.7 && rectangle.getMaxDiameter() < 6.8);
    }

    @Test
    void getShellInternal()
    {
        assertEquals(4, rectangle.getShellInternal(10).length);
    }
}