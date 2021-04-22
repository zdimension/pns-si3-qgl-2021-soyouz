package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LiftSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LowerSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.DeckEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SailObjectiveTest
{
    SailObjective twoToClose;
    SailObjective twoToOpen;

    @BeforeEach
    void setUp()
    {
        DeckEntity[] entClose = {
            new Voile(0, 1, false),
            new Voile(1, 1, false)
        };
        Bateau shipClosed = new Bateau("Peqoq", new Deck(2, 2), entClose);
        List<Marin> sailors1 = new ArrayList<>();
        sailors1.add(new Marin(0, 0, 0, "Tom"));
        sailors1.add(new Marin(1, 1, 0, "Tim"));
        DeckEntity[] entOpen = {
            new Voile(0, 1, true),
            new Voile(1, 1, true)
        };
        Bateau shipOpened = new Bateau("Peqoq", new Deck(2, 2), entOpen);
        List<Marin> sailors2 = new ArrayList<>();
        sailors2.add(new Marin(2, 0, 0, "Tem"));
        sailors2.add(new Marin(3, 1, 0, "Tum"));
        twoToClose = new SailObjective(shipOpened, 0, sailors1);
        twoToOpen = new SailObjective(shipClosed, 2, sailors2);
    }

    @Test
    void isValidated()
    {
        assertFalse(twoToClose.isValidated());
        assertFalse(twoToOpen.isValidated());
        twoToClose.resolve();
        twoToOpen.resolve();
        assertTrue(twoToClose.isValidated());
        assertTrue(twoToOpen.isValidated());
    }

    @Test
    void resolve()
    {
        List<GameAction> ga1 = twoToClose.resolve();
        List<GameAction> ga2 = twoToOpen.resolve();
        assertEquals(4, ga1.size());
        assertEquals(4, ga2.size());
        assertTrue(ga1.get(0) instanceof MoveAction);
        assertTrue(ga1.get(1) instanceof MoveAction);
        assertTrue(ga2.get(0) instanceof MoveAction);
        assertTrue(ga2.get(1) instanceof MoveAction);
        assertTrue(ga1.get(2) instanceof LowerSailAction);
        assertTrue(ga1.get(3) instanceof LowerSailAction);
        assertTrue(ga2.get(2) instanceof LiftSailAction);
        assertTrue(ga2.get(3) instanceof LiftSailAction);
    }
}