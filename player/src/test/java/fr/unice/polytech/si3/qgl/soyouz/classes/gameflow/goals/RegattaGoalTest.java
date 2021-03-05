package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegattaGoalTest
{

    Cockpit cockpit;
    RegattaGoal rg;

    @BeforeEach
    void init()
    {
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
    }

    @Test
    void getCheckpointTest()
    {
        assertEquals(1, rg.getCheckpoints().length);
    }
}