package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckpointTest
{
    Checkpoint[] checkpoints;

    @BeforeEach
    void init()
    {
        checkpoints = new Checkpoint[] {
            new Checkpoint(new Position(10, 10, 5), new Circle(10)),
            new Checkpoint(new Position(20, 10, 10), new Circle(10)),
            new Checkpoint(new Position(30, 10, 15), new Circle(10))
        };
    }

    @Test
    void getPositionTest()
    {
        assertEquals(new Position(10, 10, 5), checkpoints[0].getPosition());
        assertEquals(new Position(20, 10, 10), checkpoints[1].getPosition());
        assertEquals(new Position(30, 10, 15), checkpoints[2].getPosition());
    }

    @Test
    void getShapeTest()
    {
        assertTrue(checkpoints[0].getShape() instanceof Circle);
        assertTrue(checkpoints[1].getShape() instanceof Circle);
        assertTrue(checkpoints[2].getShape() instanceof Circle);
    }
}