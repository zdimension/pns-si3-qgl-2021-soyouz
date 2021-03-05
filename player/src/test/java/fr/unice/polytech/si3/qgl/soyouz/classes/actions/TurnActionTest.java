package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TurnActionTest {

    Marin sailor;
    TurnAction turnAction;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        turnAction = new TurnAction(sailor, Math.PI/4);
    }

    @Test
    void getSailorTest() {
        assertEquals(sailor, turnAction.getSailor());
        assertEquals(new Marin(1, 0, 0, "Tom Pouce"), turnAction.getSailor());
    }

    @Test
    void getSailorIdTest() {
        assertEquals(sailor.getId(), turnAction.getSailorId());
        assertEquals(1, turnAction.getSailorId());
    }

    @Test
    void getEntityNeededTest() {
        assertEquals(Gouvernail.class, turnAction.getEntityNeeded());
        assertNotEquals(Rame.class, turnAction.getEntityNeeded());
    }

    @Test
    void getRotationTest() {
        assertEquals(Math.PI/4, turnAction.getRotation());
        assertNotEquals(Math.PI/4.01, turnAction.getRotation());
    }

    @Test
    void toStringTest() {
        assertEquals("TurnAction : rotation = 0.7853981633974483 | sailor : Marin{id=1, name='Tom Pouce', x=0, y=0}", turnAction.toString());
    }

}