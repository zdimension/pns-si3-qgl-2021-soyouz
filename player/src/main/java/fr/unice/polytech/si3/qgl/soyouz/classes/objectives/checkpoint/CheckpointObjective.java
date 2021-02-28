package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Trigonometry;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.RoundObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.WantedSailorConfig;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;

/**
 * Checkpoint type of objective
 */
public class CheckpointObjective extends CompositeObjective {

    private Checkpoint cp;
    private HashMap<Pair<Integer, Integer>, Double> leftTurnPossibilities;
    private HashMap<Pair<Integer, Integer>, Double> rightTurnPossibilities;
    private Pair<HashMap<Pair<Integer, Integer>, Double>, HashMap<Pair<Integer, Integer>, Double>> turnPossibilities;

    public CheckpointObjective(Checkpoint checkpoint) {
        cp = checkpoint;
        leftTurnPossibilities = new HashMap<>();
        rightTurnPossibilities = new HashMap<>();
        turnPossibilities = Pair.of(leftTurnPossibilities, rightTurnPossibilities);
    }

    @Override
    public boolean isValidated(GameState state) {
        return state.getNp().getShip().getPosition().getLength(cp.getPosition())
                < ((Circle) cp.getShape()).getRadius();
    }

    /**
     * Updates this objective according to the state of the game
     *
     * @param state of the game
     */
    @Override
    public void update(GameState state) {

    }

    //TODO A REFACTO
    @Override
    public List<GameAction> resolve(GameState state) {

        Double neededRotation2 = calculateAngleBetweenBoatAndCheckpoint(state.getNp().getShip(), cp);

        var xBoat = state.getNp().getShip().getPosition().getX();
        var yBoat = state.getNp().getShip().getPosition().getY();

        var xObjective = cp.getPosition().getX();
        var yObjective = cp.getPosition().getY();
        var da = Math.atan2(yObjective - yBoat, xObjective - xBoat);
        var vl = da == 0 ? xObjective - xBoat : da * (Math.pow(xObjective - xBoat, 2) + Math.pow(yObjective - yBoat, 2)) / (yObjective - yBoat);
        var neededRotation = Trigonometry.calculateAngle(state.getNp().getShip(), cp);
        Pair<Double, Double> opt = Pair.of(vl, neededRotation);

        var sailors = state.getIp().getSailors();

        var wantedTurnConfig = Trigonometry.findOptTurnConfig(sailors.length, state.getIp().getShip().getNumberOar() / 2, opt, turnPossibilities);
        var wantedOarConfig = wantedTurnConfig.first;
        var wantedRudderConfig = wantedTurnConfig.second;
        var wanted = new WantedSailorConfig(wantedOarConfig, (wantedRudderConfig != null? state.getIp().getShip().findFirstEntity(Gouvernail.class) : null), (wantedRudderConfig));

        var roundObj = new RoundObjective(wanted);

        return roundObj.resolve(state);
    }

    public double calculateAngleBetweenBoatAndCheckpoint(Bateau boat, Checkpoint checkpoint) {
        double boatOrientation = boat.getPosition().getOrientation();
        var boatVect = Pair.of(Math.cos(boatOrientation), Math.sin(boatOrientation));
        var cpVect = Pair.of(checkpoint.getPosition().getX() - boat.getPosition().getX(), checkpoint.getPosition().getY() - boat.getPosition().getY());
        var normeDirection = Math.sqrt(Math.pow(cpVect.first, 2) + Math.pow(cpVect.second, 2));
        var scalaire = boatVect.first * cpVect.first + boatVect.second * cpVect.second;

        var angle = Math.acos(scalaire / normeDirection);
        if (cpVect.second - boatVect.second < 0)
            angle = -angle;
        return angle;
    }
}
