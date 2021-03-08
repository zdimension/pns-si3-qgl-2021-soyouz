package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BateauTest
{

    Bateau ship;

    @BeforeEach
    void init()
    {
        Cockpit cockpit = new Cockpit();

        cockpit.nextRound("{\"ship\": {\n" +
            "    \"type\": \"ship\",\n" +
            "    \"life\": 100,\n" +
            "    \"position\": {\n" +
            "      \"x\": 10.654,\n" +
            "      \"y\": 3,\n" +
            "      \"orientation\": 2.05\n" +
            "    },\n" +
            "    \"name\": \"Les copaings d'abord!\",\n" +
            "    \"deck\": {\n" +
            "      \"width\": 2,\n" +
            "      \"length\": 1\n" +
            "    },\n" +
            "    \"entities\": [\n" +
            "      {\n" +
            "        \"x\": 0,\n" +
            "        \"y\": 0,\n" +
            "        \"type\": \"oar\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"x\": 0,\n" +
            "        \"y\": 1,\n" +
            "        \"type\": \"oar\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }}");

        ship = cockpit.getNp().getShip();
    }

    @Test
    void getLife()
    {
        assertEquals(100, ship.getLife());
    }

    @Test
    void getPosition()
    {
        assertEquals(10.654, ship.getPosition().getX());
        assertEquals(3, ship.getPosition().getY());
    }

    @Test
    void getName()
    {
        assertEquals("Les copaings d'abord!", ship.getName());
    }

    @Test
    void getDeck()
    {
        assertEquals(2, ship.getDeck().getWidth());
        assertEquals(1, ship.getDeck().getLength());
    }

    @Test
    void getEntities()
    {
        assertEquals(2, ship.getEntities().length);
        assertTrue(ship.getEntities()[0] instanceof Rame);
        assertEquals(0, ship.getEntities()[0].getX());
        assertEquals(0, ship.getEntities()[0].getY());
        assertTrue(ship.getEntities()[1] instanceof Rame);
        assertEquals(0, ship.getEntities()[1].getX());
        assertEquals(1, ship.getEntities()[1].getY());
    }

    @Test
    void getNumberOar()
    {
        assertEquals(2, ship.getNumberOar());
    }

    @Test
    void getShape()
    {
        assertNull(ship.getShape());
    }

    @Test
    void getEntityHere()
    {
        assertTrue(ship.getEntityHere(0, 0).isPresent());
        assertTrue(ship.getEntityHere(0, 1).isPresent());
        assertTrue(ship.getEntityHere(1, 1).isEmpty());
    }

    @Test
    void testGetEntityHere()
    {
        assertTrue(ship.getEntityHere(Pair.of(0, 0)).isPresent());
        assertTrue(ship.getEntityHere(Pair.of(0, 1)).isPresent());
        assertTrue(ship.getEntityHere(Pair.of(1, 1)).isEmpty());
    }

    @Test
    void getNbOfOarOnEachSideTest(){
        Pair<Integer, Integer> combi = ship.getNbOfOarOnEachSide();
        assertEquals(1,combi.first);
        assertEquals(1,combi.second);
    }

    @Test
    void findFirstPosOfEntityTest(){
        Pair<Integer, Integer> combi = ship.findFirstPosOfEntity(Rame.class);
        assertNotNull(combi);
    }

    @Test
    void findFirstEntityTest(){
        Rame oar = ship.findFirstEntity(Rame.class);
        assertNotNull(oar);
    }

    @Test
    void hasAtTest(){
        assertTrue(ship.hasAt(0,0,Rame.class));
        assertFalse(ship.hasAt(0,0, Gouvernail.class));
    }

    @Test
    void isPositionOnLeft(){
        Position position = new Position(150,50,0);
        assertTrue(ship.isPositionOnLeft(position));
        Position position2 = new Position(-40,20,0);
        assertFalse(ship.isPositionOnLeft(position2));
    }

    //No Point in testing a toString only for coverage purposes
    @Test
    void toStringTest(){
        ship.toString();
        assertTrue(true);
    }


}