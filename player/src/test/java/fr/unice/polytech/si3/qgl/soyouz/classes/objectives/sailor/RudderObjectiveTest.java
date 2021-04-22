package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.DeckEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RudderObjectiveTest
{
    RudderObjective roWithoutMovement;
    RudderObjective roWithMovement;

    @BeforeEach
    void setUp()
    {
        DeckEntity[] ent = {
            new Gouvernail(1, 1)
        };
        Bateau ship = new Bateau("Peqoq", new Deck(2, 2), ent);
        Marin onRudder = new Marin(0, 1, 1, "Tom");
        Marin notOnRudder = new Marin(1, 1, 0, "Tim");
        double rotation = Math.PI / 10;
        roWithoutMovement = new RudderObjective(ship, rotation, onRudder);
        roWithMovement = new RudderObjective(ship, rotation, notOnRudder);
    }

    @Test
    void isValidated()
    {
        assertTrue(roWithoutMovement.isValidated());
        assertFalse(roWithMovement.isValidated());
        roWithMovement.resolve();
        assertTrue(roWithMovement.isValidated());
    }

    @Test
    void resolve()
    {
        TurnAction taWithoutMovement = (TurnAction) roWithoutMovement.resolve().get(0);
        List<GameAction> ga = roWithMovement.resolve();
        MoveAction ma = (MoveAction) ga.get(0);
        TurnAction ta = (TurnAction) ga.get(1);
        assertEquals(Math.PI / 10, taWithoutMovement.getRotation());
        assertEquals(0, ma.getXDistance());
        assertEquals(1, ma.getYDistance());
        assertEquals(Math.PI / 10, ta.getRotation());
    }
}