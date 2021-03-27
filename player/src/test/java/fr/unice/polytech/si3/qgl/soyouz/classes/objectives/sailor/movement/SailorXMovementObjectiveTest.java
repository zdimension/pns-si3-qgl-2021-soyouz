package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SailorXMovementObjectiveTest
{
    SailorXMovementObjective mov1Turn;
    SailorXMovementObjective mov2Turn;
    SailorXMovementObjective mov3Turn;

    @BeforeEach
    void setUp()
    {
        mov1Turn = new SailorXMovementObjective(new Marin(1, 1, 1, "Tom"), 3);
        mov2Turn = new SailorXMovementObjective(new Marin(2, 1, 1, "Tim"), 7);
        mov3Turn = new SailorXMovementObjective(new Marin(3, 13, 1, "Tam"), 1);
    }

    private void resolveObjs()
    {
        if (!mov1Turn.isValidated())
            mov1Turn.resolve();
        if (!mov2Turn.isValidated())
            mov2Turn.resolve();
        if (!mov3Turn.isValidated())
            mov3Turn.resolve();
    }

    @Test
    void isValidated()
    {
        assertFalse(mov1Turn.isValidated());
        assertFalse(mov2Turn.isValidated());
        assertFalse(mov3Turn.isValidated());
        resolveObjs();
        assertTrue(mov1Turn.isValidated());
        assertFalse(mov2Turn.isValidated());
        assertFalse(mov3Turn.isValidated());
        resolveObjs();
        assertTrue(mov1Turn.isValidated());
        assertTrue(mov2Turn.isValidated());
        assertFalse(mov3Turn.isValidated());
        resolveObjs();
        assertTrue(mov1Turn.isValidated());
        assertTrue(mov2Turn.isValidated());
        assertTrue(mov3Turn.isValidated());
    }

    @Test
    void resolve()
    {
        MoveAction movAction = (MoveAction) mov1Turn.resolve().get(0);
        assertEquals(2, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
        movAction = (MoveAction) mov2Turn.resolve().get(0);
        assertEquals(5, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
        movAction = (MoveAction) mov2Turn.resolve().get(0);
        assertEquals(1, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
        movAction = (MoveAction) mov3Turn.resolve().get(0);
        assertEquals(-5, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
        movAction = (MoveAction) mov3Turn.resolve().get(0);
        assertEquals(-5, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
        movAction = (MoveAction) mov3Turn.resolve().get(0);
        assertEquals(-2, movAction.getXDistance());
        assertEquals(0, movAction.getYDistance());
    }

    @Test
    void getNbTurnToComplete()
    {
        assertEquals(1, mov1Turn.getNbTurnToComplete());
        assertEquals(2, mov2Turn.getNbTurnToComplete());
        assertEquals(3, mov3Turn.getNbTurnToComplete());
        resolveObjs();
        assertEquals(0, mov1Turn.getNbTurnToComplete());
        assertEquals(1, mov2Turn.getNbTurnToComplete());
        assertEquals(2, mov3Turn.getNbTurnToComplete());
        resolveObjs();
        assertEquals(0, mov1Turn.getNbTurnToComplete());
        assertEquals(0, mov2Turn.getNbTurnToComplete());
        assertEquals(1, mov3Turn.getNbTurnToComplete());
        resolveObjs();
        assertEquals(0, mov1Turn.getNbTurnToComplete());
        assertEquals(0, mov2Turn.getNbTurnToComplete());
        assertEquals(0, mov3Turn.getNbTurnToComplete());
    }

    @Test
    void getSailor()
    {
        assertEquals(new Marin(1, 1, 1, "Tom"), mov1Turn.getSailor());
        assertEquals(new Marin(2, 1, 1, "Tim"), mov2Turn.getSailor());
        assertEquals(new Marin(3, 1, 1, "Tam"), mov3Turn.getSailor());
    }
}