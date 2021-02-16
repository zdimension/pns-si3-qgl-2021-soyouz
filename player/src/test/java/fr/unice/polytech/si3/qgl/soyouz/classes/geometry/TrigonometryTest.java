package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TrigonometryTest {

    Cockpit cp;
    InitGameParameters ip;

    @BeforeEach
    void init() {
        Cockpit cp = new Cockpit();
        cp.initGame("{\n" +
                "  \"goal\": {\n" +
                "    \"mode\": \"REGATTA\",\n" +
                "    \"checkpoints\": [\n" +
                "      {\n" +
                "        \"position\": {\n" +
                "          \"x\": 1000,\n" +
                "          \"y\": 300,\n" +
                "          \"orientation\": 0\n" +
                "        },\n" +
                "        \"shape\": {\n" +
                "          \"type\": \"circle\",\n" +
                "          \"radius\": 50\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"ship\": {\n" +
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
                "      \"length\": 4\n" +
                "    },\n" +
                "    \"entities\": [\n" +
                "      {\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 1,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 1,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 1,\n" +
                "        \"type\": \"oar\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"shape\": {\n" +
                "      \"type\": \"rectangle\",\n" +
                "      \"width\": 3,\n" +
                "      \"height\": 6,\n" +
                "      \"orientation\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"sailors\": [\n" +
                "    {\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 0,\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"Edward Teach\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 1,\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"Edward Pouce\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 1,\n" +
                "      \"id\": 2,\n" +
                "      \"name\": \"Tom Pouce\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 0,\n" +
                "      \"id\": 3,\n" +
                "      \"name\": \"Jack Teach\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        ip = cp.getIp();
    }


    @Test
    void oarLinearSpeed() {
        assertEquals((165 * 3 / 6), Trigonometry.oarLinearSpeed(3, 6));
    }

    @Test
    void setTurnPossibilitiesTest() {
        InitGameParameters ip = cp.getIp();
        assertEquals(0, Trigonometry.rightTurnPossibilities.size());
        assertEquals(0, Trigonometry.leftTurnPossibilities.size());
        int nbOarOnSide = (int) Arrays.stream(ip.getShip().getEntities()).filter(oar -> oar.getY()==0).count(); //Oar à gauche
        Trigonometry.setTurnPossibilities(ip.getSailors().length, nbOarOnSide);
        assertEquals(3, Trigonometry.rightTurnPossibilities.size());
        assertEquals(3, Trigonometry.leftTurnPossibilities.size());
        Trigonometry.rightTurnPossibilities.keySet().forEach(System.out::println);
        Trigonometry.leftTurnPossibilities.keySet().forEach(System.out::println);
    }

    @Test
    void findOptOarConfigTest() {
        var xb = ip.getShip().getPosition().getX();
        var yb = ip.getShip().getPosition().getY();
        var nextCp = ((RegattaGoal) ip.getGoal()).getCheckpoints()[0];
        var xo = nextCp.getPosition().getX();
        var yo = nextCp.getPosition().getY();
        var da = Math.atan2(yo - yb, xo - xb);
        var vl = da == 0 ? xo - xb : da * (Math.pow(xo - xb, 2) + Math.pow(yo - yb, 2)) / (yo - yb);
        var vr = 2 * da;
        vl = 1000;
        vr = -0.20;
        int nbOarOnSide = (int) Arrays.stream(ip.getShip().getEntities()).filter(oar -> oar.getY()==0).count(); //Oar à gauche
        Pair<Double, Double> opt = Pair.of(vl, vr);
        System.out.println(opt);
        System.out.println(Trigonometry.findOptOarConfig(ip.getSailors().length, nbOarOnSide, opt));
    }
}