package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointObjectiveTest
{
    CheckpointObjective co;
    GameState gs;

    @BeforeEach
    void init()
    {
        DeckEntity[] ent = {
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
        ship.setPosition(new Position(10, 10, 1));
        OnBoardDataHelper obdh = new OnBoardDataHelper(ship,
            new ArrayList<>(Arrays.asList(sailors)));
        SeaDataHelper sdh = new SeaDataHelper(ship, new Wind(1, 100));
        co = new CheckpointObjective(new Checkpoint(new Position(10, 1000, 0), new Circle(50)),
            obdh, sdh);
        ShapedEntity[] entt = {};
        gs = new GameState(null, new NextRoundParameters(ship, new Wind(1, 100), entt));
    }

    @Test
    void isValidated()
    {
        assertFalse(co.isValidated(gs));
        gs.getNp().getShip().setPosition(new Position(10, 1000, 0));
        assertTrue(co.isValidated(gs));
    }

    @Test
    void resolve()
    {
        List<GameAction> ga = co.resolve(gs);
        assertEquals(4, ga.size());
        long moveAct = ga.stream().filter(act -> act instanceof MoveAction).count();
        long oarAct = ga.stream().filter(act -> act instanceof OarAction).count();
        long turnAct = ga.stream().filter(act -> act instanceof TurnAction).count();
        long liftAct = ga.stream().filter(act -> act instanceof LiftSailAction).count();
        long lowerAct = ga.stream().filter(act -> act instanceof LowerSailAction).count();
        assertEquals(0, moveAct);
        assertEquals(2, oarAct);
        assertEquals(1, turnAct);
        assertEquals(1, liftAct);
        assertEquals(0, lowerAct);
    }
}