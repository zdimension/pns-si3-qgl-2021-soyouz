package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PointTest {

    Point point;

    @BeforeEach
    void init() {
        point = new Point();
    }

    @Test
    void getX() {
        assertNotEquals(0.01, point.getX());
        assertEquals(0, point.getX());
        assertNotEquals(-0.01, point.getX());
    }

    @Test
    void getY() {
        assertNotEquals(0.01, point.getY());
        assertEquals(0, point.getY());
        assertNotEquals(-0.01, point.getY());
    }
}