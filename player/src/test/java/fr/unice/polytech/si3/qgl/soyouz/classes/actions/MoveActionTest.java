package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {

    Marin sailor;
    MoveAction moveAction;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        moveAction = new MoveAction(sailor);
    }

    @Test
    void getXDistanceTest() {
        assertEquals(0, moveAction.getXdistance());
    }

    @Test
    void getYDistanceTest() {
        assertEquals(0, moveAction.getYdistance());
    }
}