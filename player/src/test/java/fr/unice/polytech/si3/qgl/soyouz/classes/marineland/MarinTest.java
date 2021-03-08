package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarinTest
{

    Marin sailor;

    @BeforeEach
    void init()
    {
        sailor = new Marin(1, 3, 2, "Tom Pouce");
    }

    @Test
    void getId()
    {
        assertNotEquals(0, sailor.getId());
        assertEquals(1, sailor.getId());
        assertNotEquals(2, sailor.getId());
    }

    @Test
    void getX()
    {
        assertNotEquals(2, sailor.getX());
        assertEquals(3, sailor.getX());
        assertNotEquals(4, sailor.getX());
    }

    @Test
    void setX()
    {
        sailor.setX(2);
        assertNotEquals(1, sailor.getX());
        assertEquals(2, sailor.getX());
        assertNotEquals(3, sailor.getX());
    }

    @Test
    void setY()
    {
        sailor.setY(2);
        assertNotEquals(1, sailor.getY());
        assertEquals(2, sailor.getY());
        assertNotEquals(3, sailor.getY());
    }

    @Test
    void isAbsPosReachableTest(){
        assertTrue(sailor.isAbsPosReachable(Pair.of(3,3)));
        assertTrue(sailor.isAbsPosReachable(Pair.of(6,4)));
        assertFalse(sailor.isAbsPosReachable(Pair.of(7,4)));
        assertFalse(sailor.isAbsPosReachable(Pair.of(6,5)));
    }

    @Test
    void numberExtraRoundsToReachEntity(){
        assertEquals(0,sailor.numberExtraRoundsToReachEntity(Pair.of(3,3)));
        assertEquals(0,sailor.numberExtraRoundsToReachEntity(3,3));
        assertEquals(1,sailor.numberExtraRoundsToReachEntity(Pair.of(8,3)));
        assertEquals(1,sailor.numberExtraRoundsToReachEntity(8,3));
    }

    @Test
    void getY()
    {
        assertNotEquals(1, sailor.getY());
        assertEquals(2, sailor.getY());
        assertNotEquals(3, sailor.getY());
    }

    @Test
    void getGridPosition()
    {
        assertNotEquals(Pair.of(2, 2), sailor.getGridPosition());
        assertEquals(Pair.of(3, 2), sailor.getGridPosition());
        assertNotEquals(Pair.of(3, 3), sailor.getGridPosition());
    }

    @Test
    void getName()
    {
        assertEquals("Tom Pouce", sailor.getName());
    }

    @Test
    void moveRelative()
    {
        sailor.moveRelative(2, 1);
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveRelative(4, 4));
    }

    @Test
    void testMoveRelative()
    {
        sailor.moveRelative(Pair.of(2, 1));
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveRelative(Pair.of(4, 4)));
    }

    @Test
    void moveAbsolute()
    {
        sailor.moveAbsolute(5, 3);
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveAbsolute(17, 6));
    }

    @Test
    void testMoveAbsolute()
    {
        sailor.moveAbsolute(Pair.of(5, 3));
        assertEquals(5, sailor.getX());
        assertEquals(3, sailor.getY());
        assertThrows(IllegalArgumentException.class, () -> sailor.moveAbsolute(Pair.of(17, 6)));
    }

    @Test
    void isRelPosReachable()
    {
        assertTrue(sailor.isRelPosReachable(4, 1));
        assertFalse(sailor.isRelPosReachable(4, 2));
        assertFalse(sailor.isRelPosReachable(5, 1));
    }

    @Test
    void isAbsPosReachable()
    {
        assertTrue(sailor.isAbsPosReachable(3, 3));
        assertTrue(sailor.isAbsPosReachable(6, 4));
        assertFalse(sailor.isAbsPosReachable(7, 4));
        assertFalse(sailor.isAbsPosReachable(6, 5));
    }

    @Test
    void hashCodeTest()
    {
        assertEquals(sailor.hashCode(), sailor.hashCode());
        assertNotEquals(sailor.hashCode(), new Marin(2, 3, 2, "Tom Pouce").hashCode());
    }

    @Test
    void equalsTest()
    {
        boolean notEquals = sailor.equals(new Gouvernail(1, 2));
        boolean equals = sailor.equals(sailor);
        boolean notEquals2 = sailor.equals(new Marin(2, 3, 2, "Tom Pouce"));
        assertTrue(equals);
        assertFalse(notEquals);
        assertFalse(notEquals2);
    }
}