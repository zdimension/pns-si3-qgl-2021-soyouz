package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolygonTest {

    Polygon polygon;

    @BeforeEach
    void init() {
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
                "   \"shape\": {\n" +
                "       \"type\": \"polygon\",\n" +
                "       \"orientation\": 1\n" +
                "       \"vertices\": [\n" +
                "         {\n" +
                "           \"point\": {\n"+
                "             \"x\": 1,\n" +
                "             \"y\": 0,\n" +
                "             }\n" +
                "         },\n" +
                "         {\n" +
                "           \"point\": {\n"+
                "           \"x\": 2,\n" +
                "           \"y\": 1,\n" +
                "             }\n" +
                "         },\n" +
                "         {\n" +
                "           \"point\": {\n"+
                "           \"x\": 3,\n" +
                "           \"y\": 0,\n" +
                "             }\n" +
                "         }\n" +
                "         ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }}");

        InitGameParameters ip = cockpit.getIp();
        RegattaGoal rg = (RegattaGoal) ip.getGoal();
        polygon = (Polygon) rg.getCheckpoints()[0].getShape();
    }


    void getOrientationTest() {
        assertEquals(1, polygon.getOrientation());
    }


    void getVerticesTest() {
        assertEquals(3, polygon.getVertices().length);
    }
}