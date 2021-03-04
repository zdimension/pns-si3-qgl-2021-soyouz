package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    Circle circle;

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
                "        \"shape\": {\n" +
                "          \"type\": \"circle\",\n" +
                "          \"radius\": 50\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }}");
        RegattaGoal rg = (RegattaGoal) cockpit.getIp().getGoal();
        circle = (Circle) rg.getCheckpoints()[0].getShape();
    }

    @Test
    void getRadius() {
        assertNotEquals(49.99, circle.getRadius());
        assertEquals(50, circle.getRadius());
        assertNotEquals(50.01, circle.getRadius());
    }
}