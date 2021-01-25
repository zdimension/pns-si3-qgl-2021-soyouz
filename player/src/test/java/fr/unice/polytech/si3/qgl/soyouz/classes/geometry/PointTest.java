package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    Point point;

    @BeforeEach
    void init() {
        point = new Point();
    }

    @Test
    void getX() {
        assertEquals(0, point.getX());
    }

    @Test
    void getY() {
        assertEquals(0, point.getY());
    }
}