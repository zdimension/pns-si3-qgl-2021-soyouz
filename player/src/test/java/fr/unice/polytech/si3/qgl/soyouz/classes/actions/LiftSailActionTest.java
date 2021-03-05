package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LiftSailActionTest {

    Marin sailor;
    LiftSailAction lsa;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        lsa = new LiftSailAction(sailor);
    }

    @Test
    void getSailorTest() {
        assertEquals(sailor, lsa.getSailor());
        assertEquals(new Marin(1, 0, 0, "Tom Pouce"), lsa.getSailor());
    }

    @Test
    void getSailorIdTest() {
        assertEquals(sailor.getId(), lsa.getSailorId());
        assertEquals(1, lsa.getSailorId());
    }

    @Test
    void getEntityNeededTest() {
        assertEquals(Voile.class, lsa.getEntityNeeded());
        assertNotEquals(Rame.class, lsa.getEntityNeeded());
    }
}