package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.DeckEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeaDataHelperTest
{
    SeaDataHelper sdh;

    @BeforeEach
    void setUp()
    {
        DeckEntity[] ent = {};
        sdh = new SeaDataHelper(new Bateau("Peqoq", new Deck(1, 2), ent), new Wind(1, 100),
            new ShapedEntity[] {});
    }

    @Test
    void update()
    {
        assertEquals(1, sdh.getWind().getOrientation());
        assertEquals(100, sdh.getWind().getStrength());
        assertEquals("Peqoq", sdh.getShip().getName());
        DeckEntity[] ent = {};
        NextRoundParameters np = new NextRoundParameters(new Bateau("Peqoc", new Deck(1, 2), ent)
            , new Wind(2, 50), null);
        sdh.update(new GameState(null, np));
        assertEquals(2, sdh.getWind().getOrientation());
        assertEquals(50, sdh.getWind().getStrength());
        assertEquals("Peqoc", sdh.getShip().getName());
    }

    @Test
    void getWind()
    {
        assertEquals(1, sdh.getWind().getOrientation());
        assertEquals(100, sdh.getWind().getStrength());
    }

    @Test
    void getShip()
    {
        assertEquals("Peqoq", sdh.getShip().getName());
        assertEquals(1, sdh.getShip().getDeck().getWidth());
        assertEquals(2, sdh.getShip().getDeck().getLength());
        assertEquals(0, sdh.getShip().getEntities().length);
    }
}