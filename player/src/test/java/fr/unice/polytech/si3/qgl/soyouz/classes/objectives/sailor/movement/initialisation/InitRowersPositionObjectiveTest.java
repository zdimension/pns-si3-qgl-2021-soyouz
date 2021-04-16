package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InitRowersPositionObjectiveTest
{
    InitRowersPositionObjective irpo;

    @BeforeEach
    void setUp()
    {
        Marin[] sailors = {
            new Marin(1, 1, 1, "Tom"),
            new Marin(1, 1, 2, "Tim"),
            new Marin(1, 2, 1, "Tam"),
            new Marin(1, 3, 1, "Tum"),
            new Marin(1, 4, 1, "Tym"),
        };
        List<Marin> rowers = new ArrayList<>(Arrays.asList(sailors));
        OnboardEntity[] ent = {
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(2, 0),
            new Rame(3, 0),
            new Rame(3, 2),
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 5), ent);
        List<LineOnBoat> linesOnBoat = new ArrayList<>();
        for (int i = 0; i < ship.getDeck().getLength(); i++)
        {
            linesOnBoat.add(new LineOnBoat(ship, i));
        }
        irpo = new InitRowersPositionObjective(rowers, linesOnBoat);
    }

    @Test
    void isValidated()
    {
        assertFalse(irpo.isValidated());
        irpo.resolve();
        assertTrue(irpo.isValidated());
    }

    @Test
    void resolve()
    {
        List<GameAction> ga = irpo.resolve();
        assertEquals(5, ga.size());
        ga.forEach(act -> assertTrue(act instanceof MoveAction));
    }
}