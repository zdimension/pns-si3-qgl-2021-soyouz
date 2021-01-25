package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BateauTest {

    Bateau ship;

    @BeforeEach
    void init() {
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
                "        \"x\": 1,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }}");

        ship = cockpit.getNp().getShip();
    }

    @Test
    void getLife() {
        assertEquals(100, ship.getLife());
    }

    @Test
    void getPosition() {
        assertEquals(10.654, ship.getPosition().getX());
        assertEquals(3, ship.getPosition().getY());
    }

    @Test
    void getName() {
        assertEquals("Les copaings d'abord!", ship.getName());
    }

    @Test
    void getDeck() {
        assertEquals(2, ship.getDeck().getWidth());
        assertEquals(1, ship.getDeck().getLength());
    }

    @Test
    void getEntities() {
        assertEquals(2, ship.getEntities().length);
        assertTrue(ship.getEntities()[0] instanceof Rame);
        assertEquals(0, ship.getEntities()[0].getX());
        assertEquals(0, ship.getEntities()[0].getY());
        assertTrue(ship.getEntities()[1] instanceof Rame);
        assertEquals(1, ship.getEntities()[1].getX());
        assertEquals(0, ship.getEntities()[0].getY());
    }

    @Test
    void getNumberOar() {
        assertEquals(2, ship.getNumberOar());
    }

    @Test
    void getShape() {
        assertNull(ship.getShape());
    }

    @Test
    void getEntityHere() {
        assertTrue(ship.getEntityHere(0, 0).isPresent());
        assertTrue(ship.getEntityHere(1, 0).isPresent());
        assertTrue(ship.getEntityHere(1, 1).isEmpty());
    }

    @Test
    void testGetEntityHere() {
        assertTrue(ship.getEntityHere(Pair.of(0, 0)).isPresent());
        assertTrue(ship.getEntityHere(Pair.of(1, 0)).isPresent());
        assertTrue(ship.getEntityHere(Pair.of(1, 1)).isEmpty());
    }
}