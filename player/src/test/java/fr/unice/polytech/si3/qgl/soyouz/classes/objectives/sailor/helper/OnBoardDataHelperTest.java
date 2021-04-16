package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnBoardDataHelperTest
{
    OnBoardDataHelper obdh;

    @BeforeEach
    void setUp()
    {
        OnboardEntity[] ent = {
            new Rame(0, 0),
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(2, 2),
            new Gouvernail(1, 2),
            new Voile(2, 0, false),
            new Vigie(2, 1)
        };
        Marin[] sailors = {
            new Marin(0, 0, 1, "Tom"),
            new Marin(1, 1, 0, "Tam"),
            new Marin(2, 2, 2, "Tem"),
            new Marin(3, 1, 2, "tum"),
            new Marin(4, 2, 0, "Tim"),
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 3), ent);
        obdh = new OnBoardDataHelper(ship, new ArrayList<>(Arrays.asList(sailors)));

    }

    @Test
    void getMutableRowers()
    {
        assertEquals(1, obdh.getMutableRowers().size());
        assertEquals(new Marin(0, 0, 1, "Tom"), obdh.getMutableRowers().get(0));
    }

    @Test
    void getImmutableRowers()
    {
        assertEquals(2, obdh.getImmutableRowers().size());
        assertEquals(new Marin(1, 1, 0, "Tam"), obdh.getImmutableRowers().get(0));
        assertEquals(new Marin(2, 2, 2, "Tem"), obdh.getImmutableRowers().get(1));
    }

    @Test
    void getSailSailors()
    {
        assertEquals(1, obdh.getSailSailors().size());
        assertEquals(new Marin(4, 2, 0, "Tim"), obdh.getSailSailors().get(0));
    }

    @Test
    void getRudderSailor()
    {
        assertEquals(new Marin(3, 1, 2, "tum"), obdh.getRudderSailor());
    }

    @Test
    void getShip()
    {
        assertEquals("Peqoq", obdh.getShip().getName());
        assertEquals(3, obdh.getShip().getDeck().getWidth());
        assertEquals(3, obdh.getShip().getDeck().getLength());
        assertEquals(7, obdh.getShip().getEntities().length);
    }
}