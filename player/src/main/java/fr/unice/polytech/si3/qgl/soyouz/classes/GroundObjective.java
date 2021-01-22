package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

public class GroundObjective extends Objective{

    private GameAction action;

    //TODO
    public GroundObjective(InitGameParameters ip, NextRoundParameters np, GameAction action) {
        super(ip, np);
        this.action = action;
    }

    public GameAction getAction() {
        return action;
    }
}
