package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.DeckEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SailConfigHelperTest
{
    SailConfigHelper sch;

    @BeforeEach
    void setUp()
    {
        DeckEntity[] ent = {
            new Voile(1, 1, false),
            new Voile(2, 1, false),
            new Voile(3, 1, false),
        };
        Bateau ship = new Bateau("Peqoq", new Deck(2, 4), ent);
        ship.setPosition(new Position(0, 0, 1));
        sch = new SailConfigHelper(1000, 0, 3, ship, new Wind(1, 100));
    }

    @Test
    void findOptSailConfiguration()
    {
        assertEquals(3, sch.findOptSailConfiguration());
    }
}