package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest
{

    Marin sailor;
    MoveAction moveAction;

    @BeforeEach
    void init()
    {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        moveAction = new MoveAction(sailor, 3, 2);
    }

    @Test
    void getSailorTest()
    {
        assertEquals(sailor, moveAction.getSailor());
        assertEquals(new Marin(1, 0, 0, "Tom Pouce"), moveAction.getSailor());
    }

    @Test
    void getSailorIdTest()
    {
        assertEquals(sailor.getId(), moveAction.getSailorId());
        assertEquals(1, moveAction.getSailorId());
    }

    @Test
    void getEntityNeededTest()
    {
        assertNull(moveAction.getEntityNeeded());
    }

    @Test
    void getXDistanceTest()
    {
        assertEquals(3, moveAction.getXDistance());
        assertNotEquals(4, moveAction.getXDistance());
        assertNotEquals(2, moveAction.getXDistance());
    }

    @Test
    void getYDistanceTest()
    {
        assertEquals(2, moveAction.getYDistance());
        assertNotEquals(1, moveAction.getYDistance());
        assertNotEquals(3, moveAction.getYDistance());
    }

    @Test
    void newPosTest()
    {
        assertEquals(PosOnShip.of(3, 2), moveAction.newPos());
        assertNotEquals(PosOnShip.of(2, 3), moveAction.newPos());
        assertNotEquals(PosOnShip.of(4, 2), moveAction.newPos());
        assertNotEquals(PosOnShip.of(2, 2), moveAction.newPos());
        assertNotEquals(PosOnShip.of(4, 3), moveAction.newPos());
    }

    @Test
    void equalsTest()
    {
        boolean notSameType = moveAction.equals(sailor);
        boolean equals = moveAction.equals(moveAction);
        boolean alsoEquals = moveAction.equals(new MoveAction(sailor, 3, 2));
        assertTrue(equals);
        assertTrue(alsoEquals);
        assertFalse(notSameType);
    }

    @Test
    void hashTest()
    {
        assertNotEquals(new MoveAction(sailor, 2, 3).hashCode(), moveAction.hashCode());
        assertNotEquals(new MoveAction(sailor, 3, 2).hashCode(), moveAction.hashCode());
        assertEquals(moveAction.hashCode(), moveAction.hashCode());
    }

    @Test
    void toStringTest()
    {
        assertEquals("MoveAction : x = 3 y = 2 | sailor : Marin{id=1, name='Tom Pouce', x=0, " +
            "y=0}", moveAction.toString());
    }
}