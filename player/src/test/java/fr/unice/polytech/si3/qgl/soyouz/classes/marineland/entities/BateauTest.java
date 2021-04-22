package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BateauTest
{

    Bateau ship;

    @BeforeEach
    void init()
    {
        DeckEntity[] ent = {
            new Rame(0, 2),
            new Rame(0, 0),
            new Rame(1, 0),
            new Rame(1, 2),
            new Gouvernail(3, 2),
            new Voile(2, 2, false)
        };
        ship = new Bateau("Pecoq", new Deck(3, 4), ent);
        ship.setPosition(new Position(10.654, 3, 2));
    }

    @Test
    void getLife()
    {
        assertEquals(0, ship.getLife());
    }

    @Test
    void getPosition()
    {
        assertEquals(new Position(10.654, 3, 2), ship.getPosition());
    }

    @Test
    void getName()
    {
        assertEquals("Pecoq", ship.getName());
    }

    @Test
    void getDeck()
    {
        assertEquals(3, ship.getDeck().getWidth());
        assertEquals(4, ship.getDeck().getLength());
    }

    @Test
    void getEntities()
    {
        assertEquals(6, ship.getEntities().length);
        int oarCount =
            (int) Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Rame).count();
        int sailCount =
            (int) Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Voile).count();
        int rudderCount =
            (int) Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Gouvernail).count();
        assertEquals(4, oarCount);
        assertEquals(1, sailCount);
        assertEquals(1, rudderCount);
    }

    @Test
    void getNumberOar()
    {
        assertEquals(4, ship.getNumberOar());
    }

    @Test
    void getShape()
    {
        assertNull(ship.getShape());
    }

    @Test
    void getEntityHere()
    {
        assertTrue(ship.getEntityHere(PosOnShip.of(0, 0)).isPresent());
        assertTrue(ship.getEntityHere(PosOnShip.of(0, 2)).isPresent());
        assertTrue(ship.getEntityHere(PosOnShip.of(1, 1)).isEmpty());
    }

    @Test
    void testGetEntityHere()
    {
        assertTrue(ship.getEntityHere(PosOnShip.of(0, 0)).isPresent());
        assertTrue(ship.getEntityHere(PosOnShip.of(0, 2)).isPresent());
        assertTrue(ship.getEntityHere(PosOnShip.of(1, 1)).isEmpty());
    }

    @Test
    void getNbOfOarOnEachSideTest()
    {
        Pair<Integer, Integer> combi = ship.getNbOfOarOnEachSide();
        assertEquals(2, combi.first);
        assertEquals(2, combi.second);
    }

    @Test
    void findFirstPosOfEntityTest()
    {
        assertEquals(PosOnShip.of(0, 2), ship.findFirstPosOfEntity(Rame.class));
    }

    @Test
    void findFirstEntityTest()
    {
        assertEquals(new Rame(0, 2), ship.findFirstEntity(Rame.class));
    }

    @Test
    void hasAtTest()
    {
        assertTrue(ship.hasAt(PosOnShip.of(0, 0), Rame.class));
        assertFalse(ship.hasAt(PosOnShip.of(0, 0), Gouvernail.class));
    }

    //No Point in testing a toString only for coverage purposes
    @Test
    void toStringTest()
    {
        String shipString = ship.toString();
        assertEquals(134, shipString.length());
    }
}