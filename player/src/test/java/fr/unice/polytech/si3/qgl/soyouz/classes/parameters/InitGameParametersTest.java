package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitGameParametersTest
{
    InitGameParameters ip;


    @BeforeEach
    void init()
    {
        Checkpoint[] cp = {
            new Checkpoint(new Position(1000, 0, 0), new Circle(50)),
            new Checkpoint(new Position(2000, 0, 0), new Circle(50))
        };
        RegattaGoal rg = new RegattaGoal(cp);
        OnboardEntity[] ent = {
            new Rame(0, 0),
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(1, 2),
            new Voile(0, 1, false),
            new Gouvernail(1, 1)
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 4), ent);
        ship.setPosition(new Position(10, 20, 30));
        Marin[] sailors = {
            new Marin(0, 0, 0, "a"),
            new Marin(1, 0, 2, "b"),
            new Marin(2, 1, 0, "c"),
            new Marin(3, 1, 2, "d"),
            new Marin(4, 0, 1, "e"),
            new Marin(5, 1, 1, "f"),
        };
        ip = new InitGameParameters(rg, ship, sailors);
    }

    @Test
    void goalTest()
    {
        assertTrue(ip.getGoal() instanceof RegattaGoal);
        RegattaGoal rg = (RegattaGoal) ip.getGoal();
        assertEquals(2, rg.getCheckpoints().length);
    }

    @Test
    void shipTest()
    {
        assertEquals(1, ip.getShipCount());
        Bateau bateau = ip.getShip();
        assertEquals("Peqoq", bateau.getName());
        assertEquals(6, bateau.getEntities().length);
        Position pos = new Position(10, 20, 30);
        assertEquals(pos, bateau.getPosition());
        assertEquals(3, bateau.getDeck().getWidth());
        assertEquals(4, bateau.getDeck().getLength());
    }

    @Test
    void sailorsTest()
    {
        Marin[] marins = ip.getSailors();
        assertEquals(6, marins.length);
        assertEquals(PosOnShip.of(0, 0), marins[0].getPos());
        assertEquals(PosOnShip.of(0, 2), marins[1].getPos());
        assertEquals(PosOnShip.of(1, 0), marins[2].getPos());
        assertEquals(PosOnShip.of(1, 2), marins[3].getPos());
        assertEquals(PosOnShip.of(0, 1), marins[4].getPos());
        assertEquals(PosOnShip.of(1, 1), marins[5].getPos());
        assertEquals(ip.getSailorById(0).get(), marins[0]);
    }
}
