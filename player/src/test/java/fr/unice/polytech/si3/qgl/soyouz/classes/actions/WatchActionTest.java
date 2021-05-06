package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class WatchActionTest
{
    WatchAction wa;
    Marin sailor;

    @BeforeEach
    void setUp()
    {
        sailor = new Marin(1, 2, 3, "Tom");
        wa = new WatchAction(sailor);
    }

    @Test
    void getSailorTest()
    {
        assertEquals(sailor, wa.getSailor());
        assertEquals(new Marin(1, 0, 0, "Tom Pouce"), wa.getSailor());
    }

    @Test
    void getSailorIdTest()
    {
        assertEquals(sailor.getId(), wa.getSailorId());
        assertEquals(1, wa.getSailorId());
    }

    @Test
    void getEntityNeededTest()
    {
        assertSame(wa.getEntityNeeded(), Vigie.class);
    }
}