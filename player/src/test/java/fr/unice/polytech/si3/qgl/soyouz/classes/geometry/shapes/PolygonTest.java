package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolygonTest
{

    Polygon polygon;

    @BeforeEach
    void init()
    {
        Cockpit cockpit = new Cockpit();

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
            "          \"type\": \"polygon\",\n" +
            "          \"orientation\": 1,\n" +
            "          \"vertices\" : [\n" +
            "            {\n" +
            "                \"x\" : 1005,\n" +
            "                \"y\" : -5\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\" : 1005,\n" +
            "                \"y\" : 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\" : 1010,\n" +
            "                \"y\" : 5\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\" : 1010,\n" +
            "                \"y\" : -5\n" +
            "            }\n" +
            "           ]\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }}");

        InitGameParameters ip = cockpit.getIp();
        RegattaGoal rg = (RegattaGoal) ip.getGoal();
        polygon = (Polygon) rg.getCheckpoints()[0].getShape();
    }

    @Test
    void getOrientationTest()
    {
        assertEquals(1, polygon.getOrientation());
    }

    @Test
    void getVerticesTest()
    {
        assertEquals(4, polygon.getVertices().length);
    }
}