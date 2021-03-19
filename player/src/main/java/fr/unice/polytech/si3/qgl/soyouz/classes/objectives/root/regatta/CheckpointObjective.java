package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.SailorObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.List;

/**
 * Checkpoint type of objective
 */
public class CheckpointObjective extends CompositeObjective
{

    private final Checkpoint cp;
    OnBoardDataHelper onBoardDataHelper;
    SailorObjective roundObjective;

    /**
     * Constructor.
     *
     * @param checkpoint The checkpoint to reach.
     */
    public CheckpointObjective(Checkpoint checkpoint, OnBoardDataHelper onBoardDataHelper)
    {
        cp = checkpoint;
        this.onBoardDataHelper = onBoardDataHelper;
        roundObjective = null;
    }

    /**
     * Determine if the boat is inside the Checkpoint ot no.
     *
     * @param state The state of the game.
     * @return true if the boat is in, false otherwise.
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return state.getNp().getShip().getPosition().getLength(cp.getPosition())
            < ((Circle) cp.getShape()).getRadius();
    }

    /**
     * Resolve the current objective by finding the bests configuration for oars and rudder.
     *
     * @param state The current game state.
     * @return a list of action to be closer of the goal.
     */
    //TODO VOIR SI SAILOR OBJECTIF NE DEVRAIT PAS GERER LE COTE HELPER
    @Override
    public List<GameAction> resolve(GameState state)
    {
        Bateau boat = state.getNp().getShip();
        double angleToCp = calculateAngleBetweenBoatAndCheckpoint(state.getNp().getShip());
        double distanceToCp = boat.getPosition().getLength(cp.getPosition());

        if (roundObjective == null || roundObjective.isValidated())
            roundObjective = new SailorObjective(onBoardDataHelper, distanceToCp, angleToCp);

        return roundObjective.resolve();
    }

    /**
     * Calculate the angle in rad between the boat and the current checkpoint.
     *
     * @param boat Our boat.
     * @return an angle in rad.
     */
    private double calculateAngleBetweenBoatAndCheckpoint(Bateau boat)
    {
        double boatOrientation = boat.getPosition().getOrientation();
        Pair<Double, Double> boatVector = Pair.of(Math.cos(boatOrientation), Math.sin(boatOrientation));
        Pair<Double, Double> cpVector = Pair.of(cp.getPosition().getX() - boat.getPosition().getX(),
            cp.getPosition().getY() - boat.getPosition().getY());
        double normDirection = Math.sqrt(Math.pow(cpVector.first, 2) + Math.pow(cpVector.second, 2));
        var scalar = boatVector.first * cpVector.first + boatVector.second * cpVector.second;
        double angle = Math.acos(scalar / normDirection);
        if (cpVector.second - boatVector.second < 0)
        {
            angle = -angle;
        }
        return angle;
    }
}
