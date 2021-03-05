package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InitGameParametersTest
{
    Cockpit cockpit;
    InitGameParameters ip;


    @BeforeEach
    void setUp()
    {
        this.cockpit = new Cockpit();
    }

    @Test
    void goalTest()
    {
        cockpit.initGame("{\"goal\": {\n" +
            "    \"mode\": \"REGATTA\",\n" +
            "    \"checkpoints\": [\n" +
            "      {\n" +
            "        \"position\": {\n" +
            "          \"x\": 1000,\n" +
            "          \"y\": 0,\n" +
            "          \"orientation\": 0\n" +
            "        },\n" +
            "        \"shape\": {\n" +
            "          \"type\": \"circle\",\n" +
            "          \"radius\": 50\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }}");
        ip = cockpit.getIp();
        RegattaGoal goal = (RegattaGoal) ip.getGoal();
        assertEquals(1, goal.getCheckpoints().length);
    }

    @Test
    void shipTest()
    {
        cockpit.initGame("{\"shipCount\": 1," +
            "\"ship\": {\n" +
            "    \"type\": \"ship\",\n" +
            "    \"life\": 100,\n" +
            "    \"position\": {\n" +
            "      \"x\": 0,\n" +
            "      \"y\": 0,\n" +
            "      \"orientation\": 0\n" +
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
            "    ],\n" +
            "    \"shape\": {\n" +
            "      \"type\": \"rectangle\",\n" +
            "      \"width\": 2,\n" +
            "      \"height\": 3,\n" +
            "      \"orientation\": 0\n" +
            "    }\n" +
            "  }}");
        ip = cockpit.getIp();
        assertEquals(1, ip.getShipCount());
        Bateau bateau = ip.getShip();
        assertEquals("Les copaings d'abord!", bateau.getName());
        assertEquals(100, bateau.getLife());
        assertEquals(2, bateau.getEntities().length);
        //Boat position
        Position pos = new Position(0, 0, 0);
        assertEquals(pos, bateau.getPosition());
        //Deck size
        assertEquals(2, bateau.getDeck().getWidth());
        assertEquals(1, bateau.getDeck().getLength());
        //Boat Shape
        assertTrue(bateau.getShape() instanceof Rectangle);
    }

    @Test
    void sailorsTest()
    {
        cockpit.initGame("{\"sailors\": [\n" +
            "    {\n" +
            "      \"x\": 0,\n" +
            "      \"y\": 0,\n" +
            "      \"id\": 0,\n" +
            "      \"name\": \"Edward Teach\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"x\": 1,\n" +
            "      \"y\": 0,\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"Tom Pouce\"\n" +
            "    }\n" +
            "  ]}");
        ip = cockpit.getIp();
        Marin[] marins = ip.getSailors();
        assertEquals(2, marins.length);
        assertEquals("Edward Teach", marins[0].getName());
        assertEquals(Pair.of(0, 0), marins[0].getGridPosition());
        assertEquals("Tom Pouce", marins[1].getName());
        assertEquals(Pair.of(1, 0), marins[1].getGridPosition());
        assertEquals("Tom Pouce", ip.getSailorById(1).get().getName());
    }

}
