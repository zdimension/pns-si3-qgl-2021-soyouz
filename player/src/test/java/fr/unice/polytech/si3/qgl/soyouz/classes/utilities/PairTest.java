package fr.unice.polytech.si3.qgl.soyouz.classes.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest
{

    Pair<Integer, Integer> coords;

    @BeforeEach
    void init()
    {
        coords = Pair.of(5, 10);
    }

    @Test
    void getComparator()
    {
        assertNotNull(Pair.getComparator());
    }

    @Test
    void testHashCode()
    {
        assertNotEquals(Pair.of(4, 10).hashCode(), coords.hashCode());
    }

    @Test
    void testEquals()
    {
        assertEquals(coords, Pair.of(5, 10));
        assertEquals(coords, coords);
        assertNotEquals(coords, "Hello");
    }

    @Test
    void testToString()
    {
        assertEquals("(5, 10)", coords.toString());
    }

    @Test
    void getFirst()
    {
        assertEquals(5, coords.getFirst());
    }

    @Test
    void getSecond()
    {
        assertEquals(10, coords.getSecond());
    }
}