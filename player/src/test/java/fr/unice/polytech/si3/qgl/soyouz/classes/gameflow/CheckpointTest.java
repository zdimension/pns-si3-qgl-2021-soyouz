package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckpointTest {

    Cockpit cockpit;
    Checkpoint[] checkpoints;
    RegattaGoal rg;

    @BeforeEach
    void init() {
        cockpit = new Cockpit();
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
        rg = (RegattaGoal) cockpit.getIp().getGoal();
        checkpoints = rg.getCheckpoints();
    }

    @Test
    void getPositionTest() {
        assertEquals(1000, checkpoints[0].getPosition().getX());
        assertEquals(0, checkpoints[0].getPosition().getY());
        assertEquals(0, checkpoints[0].getPosition().getOrientation());
    }

    @Test
    void getShapeTest() {
        assertTrue(checkpoints[0].getShape() instanceof Circle);
    }
}