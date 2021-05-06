package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.DeckEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NextRoundParametersTest
{
    NextRoundParameters np;

    @BeforeEach
    void setUp()
    {
        ShapedEntity[] ent = {
            new Reef(),
            new Stream(new Position(10, 10, 2), new Rectangle(30, 20, 1), 0),
        };
        DeckEntity[] onboardEntities = {
            new Rame(0, 0),
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(1, 2),
            new Voile(0, 1, false),
            new Gouvernail(1, 1)
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 4), onboardEntities);
        ship.setPosition(new Position(10, 20, 30));
        np = new NextRoundParameters(ship, new Wind(2, 50), ent);
    }

    @Test
    void shipTest()
    {
        Bateau bateau = np.getShip();
        assertEquals("Peqoq", bateau.getName());
        assertEquals(6, bateau.getEntities().length);
        Position pos = new Position(10, 20, 30);
        assertEquals(pos, bateau.getPosition());
        assertEquals(3, bateau.getDeck().getWidth());
        assertEquals(4, bateau.getDeck().getLength());
    }

    @Test
    void windTest()
    {
        assertEquals(2, np.getWind().getOrientation());
        assertEquals(50, np.getWind().getStrength());
    }

    @Test
    void visibleEntitiesTest()
    {
        assertEquals(2, np.getVisibleEntities().length);
    }
}
