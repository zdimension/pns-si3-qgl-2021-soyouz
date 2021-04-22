package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InitSailorPositionObjectiveTest
{
    InitSailorPositionObjective ispo;

    @BeforeEach
    void setUp()
    {
        Marin[] sailor = {
            new Marin(1, 1, 1, "Tom"),
            new Marin(1, 1, 0, "Tim"),
            new Marin(1, 2, 1, "Tam"),
            new Marin(1, 3, 1, "Tum"),
            new Marin(1, 4, 1, "Tym"),
            new Marin(1, 4, 2, "Tem"),
            new Marin(1, 2, 0, "Tm"),
        };
        List<Marin> sailors = new ArrayList<>(Arrays.asList(sailor));
        DeckEntity[] ent = {
            new Rame(0, 2),
            new Rame(1, 0),
            new Gouvernail(1, 2),
            new Voile(3, 2, false),
            new Voile(2, 2, false),
            new Vigie(1, 1)
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 5), ent);
        ispo = new InitSailorPositionObjective(ship, sailors);
    }

    @Test
    void isValidated()
    {
        assertFalse(ispo.isValidated());
        ispo.resolve();
        assertTrue(ispo.isValidated());
    }

    @Test
    void resolve()
    {
        List<GameAction> ga = ispo.resolve();
        assertEquals(7, ga.size());
        ga.forEach(act -> assertTrue(act instanceof MoveAction));
    }
}