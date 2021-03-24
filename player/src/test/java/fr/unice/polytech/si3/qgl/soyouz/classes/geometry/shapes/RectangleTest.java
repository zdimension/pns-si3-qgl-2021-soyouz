package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}