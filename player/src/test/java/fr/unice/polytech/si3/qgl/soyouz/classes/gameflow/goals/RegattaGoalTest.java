package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegattaGoalTest
{

    RegattaGoal rg;

    @BeforeEach
    void init()
    {
        Checkpoint[] cp = {
            new Checkpoint(new Position(1000, 1000, 0), new Circle(50)),
            new Checkpoint(new Position(2000, 2000, 0), new Circle(50))
        };
        rg = new RegattaGoal(cp);
    }

    @Test
    void getCheckpointTest()
    {
        assertEquals(2, rg.getCheckpoints().length);
    }
}