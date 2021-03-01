package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.RoundObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.WantedSailorConfig;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;

/**
 * Checkpoint type of objective
 */
public class CheckpointObjective extends CompositeObjective {

    private final Checkpoint cp;

    public CheckpointObjective(Checkpoint checkpoint) {
        cp = checkpoint;
    }

    @Override
    public boolean isValidated(GameState state) {
        return state.getNp().getShip().getPosition().getLength(cp.getPosition())
                < ((Circle) cp.getShape()).getRadius();
    }

    @Override
    public List<GameAction> resolve(GameState state) {

        Bateau boat = state.getNp().getShip();
        double angleToCp = calculateAngleBetweenBoatAndCheckpoint(state.getNp().getShip());
        double distanceToCp = boat.getPosition().getLength(cp.getPosition());
        int nbSailors = state.getIp().getSailors().length;
        Pair<Integer, Integer> nbOarOnEachSide = state.getIp().getShip().getNbOfOarOnEachSide();

        RowersObjective rowersObjective = new RowersObjective(angleToCp, distanceToCp, nbSailors, nbOarOnEachSide.first, nbOarOnEachSide.second);
        OarConfiguration wantedOarConfiguration = rowersObjective.resolve();

        RudderObjective rudderObjective = new RudderObjective(angleToCp - wantedOarConfiguration.getAngleOfRotation());
        double wantedRudderRotation = rudderObjective.resolve();

        WantedSailorConfig wanted;
        //if (wantedRudderRotation != 0)
            wanted = new WantedSailorConfig(wantedOarConfiguration.getSailorConfiguration(), state.getIp().getShip().findFirstEntity(Gouvernail.class), wantedRudderRotation);
        //else
        //    wanted = new WantedSailorConfig(wantedOarConfiguration.getSailorConfiguration(),null, null);

        var roundObj = new RoundObjective(wanted);
        return roundObj.resolve(state);
    }

    private double calculateAngleBetweenBoatAndCheckpoint(Bateau boat) {
        double boatOrientation = boat.getPosition().getOrientation();
        var boatVector = Pair.of(Math.cos(boatOrientation), Math.sin(boatOrientation));
        var cpVector = Pair.of(cp.getPosition().getX() - boat.getPosition().getX(), cp.getPosition().getY() - boat.getPosition().getY());
        var normDirection = Math.sqrt(Math.pow(cpVector.first, 2) + Math.pow(cpVector.second, 2));
        var scalar = boatVector.first * cpVector.first + boatVector.second * cpVector.second;
        var angle = Math.acos(scalar / normDirection);
        if (cpVector.second - boatVector.second < 0)
            angle = -angle;
        return angle;
    }
}
