package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PosOnShipTest
{

    PosOnShip posByCoords;
    PosOnShip posByPairOfCoords;
    PosOnShip posByEntityCoords;

    @BeforeEach
    void init()
    {
        posByCoords = new PosOnShip(1, 2);
        posByPairOfCoords = new PosOnShip(2, 3);
        posByEntityCoords = new PosOnShip(new Gouvernail(3, 4));
    }

    @Test
    void getX()
    {
        assertNotEquals(0, posByCoords.getX());
        assertEquals(1, posByCoords.getX());
        assertNotEquals(2, posByCoords.getX());
        assertNotEquals(1, posByPairOfCoords.getX());
        assertEquals(2, posByPairOfCoords.getX());
        assertNotEquals(3, posByPairOfCoords.getX());
        assertNotEquals(2, posByEntityCoords.getX());
        assertEquals(3, posByEntityCoords.getX());
        assertNotEquals(4, posByEntityCoords.getX());
    }

    @Test
    void getY()
    {
        assertNotEquals(1, posByCoords.getY());
        assertEquals(2, posByCoords.getY());
        assertNotEquals(3, posByCoords.getY());
        assertNotEquals(2, posByPairOfCoords.getY());
        assertEquals(3, posByPairOfCoords.getY());
        assertNotEquals(4, posByPairOfCoords.getY());
        assertNotEquals(3, posByEntityCoords.getY());
        assertEquals(4, posByEntityCoords.getY());
        assertNotEquals(5, posByEntityCoords.getY());
    }

    @Test
    void getPosCoord()
    {
        assertNotEquals(PosOnShip.of(0,2), posByCoords);
        assertEquals(PosOnShip.of(1,2), posByCoords);
        assertNotEquals(PosOnShip.of(1,3), posByCoords);
        assertNotEquals(PosOnShip.of(1,3), posByPairOfCoords);
        assertEquals(PosOnShip.of(2,3), posByPairOfCoords);
        assertNotEquals(PosOnShip.of(2,4), posByPairOfCoords);
        assertNotEquals(PosOnShip.of(2,4), posByEntityCoords);
        assertEquals(PosOnShip.of(3,4), posByEntityCoords);
        assertNotEquals(PosOnShip.of(3,5), posByEntityCoords);
    }

    @Test
    void testToString()
    {
        assertEquals("PosOnShip{x=1, y=2}", posByCoords.toString());
        assertEquals("PosOnShip{x=2, y=3}", posByPairOfCoords.toString());
        assertEquals("PosOnShip{x=3, y=4}", posByEntityCoords.toString());
    }

    @Test
    void testEquals()
    {
        boolean notEquals = posByCoords.equals(new Gouvernail(1, 2));
        boolean notEquals2 = posByPairOfCoords.equals(posByEntityCoords);
        boolean equals = posByCoords.equals(new PosOnShip(new Gouvernail(1, 2)));
        boolean equals2 = posByCoords.equals(posByCoords);
        assertTrue(equals);
        assertTrue(equals2);
        assertFalse(notEquals);
        assertFalse(notEquals2);
    }

    @Test
    void testHashCode()
    {
        assertNotEquals(posByCoords.hashCode(), posByPairOfCoords.hashCode());
        assertEquals(posByCoords.hashCode(), new PosOnShip(new Gouvernail(1, 2)).hashCode());
    }
}