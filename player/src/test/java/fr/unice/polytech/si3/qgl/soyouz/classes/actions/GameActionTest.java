package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameActionTest {

    Marin sailor;
    Marin pirate;
    OarAction oarAction;
    MoveAction moveAction;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        pirate = new Marin(2, 1,1, "Jack Sparrow");
        oarAction = new OarAction(sailor);
        moveAction = new MoveAction(sailor);
    }

    @Test
    void getSailorIdTest() {
        assertEquals(sailor.getId(), oarAction.getSailorId());
        assertEquals(sailor.getId(), moveAction.getSailorId());
        assertNotEquals(pirate.getId(), oarAction.getSailorId());
        assertNotEquals(pirate.getId(), moveAction.getSailorId());
    }

    @Test
    void getSailorTest() {
        assertEquals(sailor, oarAction.getSailor());
        assertEquals(sailor, moveAction.getSailor());
        assertNotEquals(pirate, oarAction.getSailor());
        assertNotEquals(pirate, moveAction.getSailor());
    }
}