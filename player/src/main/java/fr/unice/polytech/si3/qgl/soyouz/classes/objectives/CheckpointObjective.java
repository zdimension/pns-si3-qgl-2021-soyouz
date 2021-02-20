package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Trigonometry;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;

public class CheckpointObjective extends CompositeObjective{

    private Checkpoint cp;

    //TODO
    public CheckpointObjective(Checkpoint checkpoint) {
        cp = checkpoint;
    }

    @Override
    public boolean isValidated(GameState state) {
        return state.getNp().getShip().getPosition().getLength(cp.getPosition())
                < (((Circle) cp.getShape())).getRadius();
    }

    @Override
    public List<GameAction> resolve(GameState state) {
        var xb = state.getNp().getShip().getPosition().getX();
        var yb = state.getNp().getShip().getPosition().getY();

        var xo = cp.getPosition().getX();
        var yo = cp.getPosition().getY();
        var da = Math.atan2(yo - yb, xo - xb);
        var vl = da == 0 ? xo - xb : da * (Math.pow(xo - xb, 2) + Math.pow(yo - yb, 2)) / (yo - yb);
        var vr = 2 * da;
        //TODO Ignoble d'appeller 2 fois la meme instance du bateau
        var vrr = Trigonometry.neededRotation(state.getNp().getShip(), state.getIp().getShip(), cp.getPosition());
        Pair<Double, Double> opt = Pair.of(vl, -vrr);

        var sailors = state.getIp().getSailors();

        var wantedOarConfig = Trigonometry.findOptOarConfig(sailors.length, state.getIp().getShip().getNumberOar() / 2, opt);

        var wantedConfig = new HashMap<Class<? extends OnboardEntity>, Object>();
        wantedConfig.put(Rame.class, wantedOarConfig);

        var roundObj = new RoundObjective(wantedConfig);

        return roundObj.resolve(state);
    }
}
