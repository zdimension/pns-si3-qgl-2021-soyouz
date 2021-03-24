package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}