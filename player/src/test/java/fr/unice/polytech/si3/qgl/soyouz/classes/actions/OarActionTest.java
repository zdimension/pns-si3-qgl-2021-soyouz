package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OarActionTest
{

    Marin sailor;
    OarAction oarAction;

    @BeforeEach
    void init()
    {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        oarAction = new OarAction(sailor);
    }

    @Test
    void getSailorTest()
    {
        assertEquals(sailor, oarAction.getSailor());
        assertEquals(new Marin(1, 0, 0, "Tom Pouce"), oarAction.getSailor());
    }

    @Test
    void getSailorIdTest()
    {
        assertEquals(sailor.getId(), oarAction.getSailorId());
        assertEquals(1, oarAction.getSailorId());
    }

    @Test
    void getEntityNeededTest()
    {
        assertEquals(Rame.class, oarAction.getEntityNeeded());
        assertNotEquals(Voile.class, oarAction.getEntityNeeded());
    }

    @Test
    void toStringTest()
    {
        assertEquals("OarAction : sailor : Marin{id=1, name='Tom Pouce', x=0, y=0}",
            oarAction.toString());
    }

}