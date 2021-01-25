package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OnboardEntityTest {

    OnboardEntity[] entities;

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

        entities = cockpit.getNp().getShip().getEntities();
    }

    @Test
    void getX() {
        assertEquals(0, entities[0].getX());
        assertEquals(1, entities[1].getX());
    }

    @Test
    void getY() {
        assertEquals(0, entities[0].getY());
        assertEquals(0, entities[1].getY());
    }
}