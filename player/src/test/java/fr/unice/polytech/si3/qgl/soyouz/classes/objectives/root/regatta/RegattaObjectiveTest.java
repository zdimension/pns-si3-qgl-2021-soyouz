package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
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
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RegattaObjectiveTest
{

    GameState gameState;
    InitGameParameters ip;
    RegattaObjective regattaObjective;

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
        ship.setPosition(new Position(0, 0, 0));
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

    InitGameParameters setupInitObjective()
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
        ship.setPosition(new Position(0, 0, 0));
        Marin[] sailors = {
            new Marin(0, 0, 1, "a"),
            new Marin(1, 3, 1, "b"),
            new Marin(2, 2, 0, "c"),
        };
        return new InitGameParameters(rg, ship, sailors);
    }

    void setupObjectiveInLine()
    {
        ShapedEntity[] ent = {};
        NextRoundParameters np = new NextRoundParameters(ip.getShip(), new Wind(0, 50), ent);
        gameState = new GameState(ip, np);
        regattaObjective = new RegattaObjective((RegattaGoal)ip.getGoal(), ip);
    }

    void setupObjectiveOnLeft()
    {
        ShapedEntity[] ent = {};
        ip.getShip().setPosition(new Position(1000, -1000, -2));
        NextRoundParameters np = new NextRoundParameters(ip.getShip(), new Wind(0, 50), ent);
        gameState = new GameState(ip, np);
        regattaObjective = new RegattaObjective((RegattaGoal)ip.getGoal(), ip);
    }

    void setupObjectiveOnRight()
    {
        ShapedEntity[] ent = {};
        ip.getShip().setPosition(new Position(1000, 1000, 2));
        NextRoundParameters np = new NextRoundParameters(ip.getShip(), new Wind(0, 50), ent);
        gameState = new GameState(ip, np);
        regattaObjective = new RegattaObjective((RegattaGoal) ip.getGoal(), ip);
    }

    void setupObjectiveOnBoat()
    {
        ShapedEntity[] ent = {};
        ip.getShip().setPosition(new Position(1000, 0, 0));
        NextRoundParameters np = new NextRoundParameters(ip.getShip(), new Wind(0, 50), ent);
        gameState = new GameState(ip, np);
        regattaObjective = new RegattaObjective((RegattaGoal)ip.getGoal(), ip);
    }

    void setupSecondObjectiveOnBoat()
    {
        ShapedEntity[] ent = {};
        ip.getShip().setPosition(new Position(2000, 0, 0));
        NextRoundParameters np = new NextRoundParameters(ip.getShip(), new Wind(0, 50), ent);
        gameState = new GameState(ip, np);
        regattaObjective = new RegattaObjective((RegattaGoal)ip.getGoal(), ip);
    }

    @Test
    void resolveInitialisation()
    {
        InitGameParameters ip = setupInitObjective();
        GameState gs = new GameState(ip, new NextRoundParameters(
            ip.getShip(), new Wind(1, 1), null));
        RegattaObjective rO = new RegattaObjective((RegattaGoal)ip.getGoal(), ip);
        rO.update(gs);
        ArrayList<GameAction> gameActions = new ArrayList<>(rO.resolve(gs));
        assertEquals(3, gameActions.size());
        gameActions.forEach(act -> {
            assertTrue(act instanceof MoveAction);
        });
    }

    @Test
    void resolveWhenCpInLine()
    {
        setupObjectiveInLine();
        Bateau ship = gameState.getIp().getShip();
        regattaObjective.update(gameState);
        /*ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(6, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        List<GameAction> sailAction = gameActions.stream().filter(a -> a instanceof LiftSailAction).collect(Collectors.toList());
        assertEquals(4, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(1, sailAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size() - sailAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(2, oarUsedOnLeft);
        assertEquals(2, oarUsedOnRight);*/
    }

    @Test
    void resolveWhenCpOnLeft()
    {
        setupObjectiveOnLeft();
        Bateau ship = gameState.getIp().getShip();
        regattaObjective.update(gameState);
        /*ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(4, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        List<GameAction> sailAction = gameActions.stream().filter(a -> a instanceof LiftSailAction).collect(Collectors.toList());
        assertEquals(2, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(1, sailAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size() - sailAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(0, oarUsedOnLeft);
        assertEquals(2, oarUsedOnRight);*/
    }

    @Test
    void resolveWhenCpOnRight()
    {
        setupObjectiveOnRight();
        Bateau ship = gameState.getIp().getShip();
        regattaObjective.update(gameState);
        /*ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(4, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        List<GameAction> sailAction = gameActions.stream().filter(a -> a instanceof LiftSailAction).collect(Collectors.toList());
        assertEquals(2, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(1, sailAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size() - sailAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(2, oarUsedOnLeft);
        assertEquals(0, oarUsedOnRight);*/
    }

    @Test
    void isValidated()
    {
        setupObjectiveInLine();
        assertFalse(regattaObjective.isValidated(gameState));
    }

    @Test
    void updateWhenNotOnCp() throws NoSuchFieldException, IllegalAccessException
    {
        setupObjectiveInLine();
        regattaObjective.update(gameState);
        Field checkpointNumber = RegattaObjective.class.getDeclaredField("numCheckpoint");
        checkpointNumber.setAccessible(true);
        int numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
        /*regattaObjective.resolve(gameState);
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);*/
    }

    @Test
    void updateWhenOnCp() throws NoSuchFieldException, IllegalAccessException
    {
        setupObjectiveOnBoat();
        Field checkpointNumber = RegattaObjective.class.getDeclaredField("numCheckpoint");
        checkpointNumber.setAccessible(true);
        int numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(1, numCheckpoint);
        setupSecondObjectiveOnBoat();
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
    }
}