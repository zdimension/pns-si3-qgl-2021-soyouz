package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

public class GroundObjective extends Objective{

    private GameAction action;

    public GroundObjective(InitGameParameters init, GameAction action) {
        super(init);
        this.action = action;
    }

    public GameAction getAction() {
        return action;
    }
}
