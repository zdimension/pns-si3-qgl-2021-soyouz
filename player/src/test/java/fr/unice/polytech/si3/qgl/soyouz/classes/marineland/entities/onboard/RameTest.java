package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RameTest
{
    Rame rame1;
    Rame rame2;
    Rame rame3;

    @BeforeEach
    void setUp()
    {
        rame1 = new Rame(1, 0);
        rame2 = new Rame(1, 1);
        rame3 = new Rame(2, 2);
    }

    @Test
    void getX()
    {
        assertEquals(1, rame1.getX());
        assertEquals(1, rame2.getX());
        assertEquals(2, rame3.getX());
    }

    @Test
    void getY()
    {
        assertEquals(0, rame1.getY());
        assertEquals(1, rame2.getY());
        assertEquals(2, rame3.getY());
    }

    @Test
    void getPos()
    {
        assertEquals(PosOnShip.of(1, 0), rame1.getPos());
        assertEquals(PosOnShip.of(1, 1), rame2.getPos());
        assertEquals(PosOnShip.of(2, 2), rame3.getPos());
    }

    @Test
    void testEquals()
    {
        boolean notEquals = rame1.equals("Hello");
        assertEquals(rame1, rame1);
        assertNotEquals(rame1, rame2);
        assertNotEquals(rame2, rame3);
        assertNotEquals(rame1, rame3);
        assertFalse(notEquals);
    }

    @Test
    void testHashCode()
    {
        assertNotEquals(rame1.hashCode(), rame2.hashCode());
    }

    @Test
    void isLeft()
    {
        assertTrue(rame1.isLeft());
        assertFalse(rame2.isLeft());
        assertFalse(rame3.isLeft());
    }
}