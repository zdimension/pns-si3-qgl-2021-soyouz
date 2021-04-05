package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SailorObjectiveTest
{
    SailorObjective so;

    @BeforeEach
    void setUp()
    {
        OnboardEntity[] ent = {
            new Rame(0, 0),
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(2, 2),
            new Gouvernail(1, 2),
            new Voile(2, 0, false)
        };
        Marin[] sailors = {
            new Marin(0, 0, 1, "Tom"),
            new Marin(1, 1, 0, "Tam"),
            new Marin(2, 2, 2, "Tem"),
            new Marin(3, 1, 2, "tum"),
            new Marin(4, 2, 0, "Tim")
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 3), ent);
        ship.setPosition(new Position(10, 20, 1));
        OnBoardDataHelper obdh = new OnBoardDataHelper(ship, new ArrayList<>(Arrays.asList(sailors)));
        SeaDataHelper sdh = new SeaDataHelper(ship, new Wind(1.8, 100), new ShapedEntity[0]);
        so = new SailorObjective(obdh, sdh, 1000, 2);
    }

    @Test
    void isValidated()
    {
        assertFalse(so.isValidated());
        so.resolve();
        assertTrue(so.isValidated());
    }

    @Test
    void resolve()
    {
        List<GameAction> ga = so.resolve();
        long moveAction = ga.stream().filter(act -> act instanceof MoveAction).count();
        long oarAction = ga.stream().filter(act -> act instanceof OarAction).count();
        long turnAction = ga.stream().filter(act -> act instanceof TurnAction).count();
        long liftAction = ga.stream().filter(act -> act instanceof LiftSailAction).count();
        assertEquals(1, moveAction);
        assertEquals(2, oarAction);
        assertEquals(1, turnAction);
        assertEquals(1, liftAction);
    }
}