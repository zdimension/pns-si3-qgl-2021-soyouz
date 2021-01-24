package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

public class GroundObjective extends Objective{

    private GameAction action;

    //TODO
    public GroundObjective(InitGameParameters ip, NextRoundParameters np, GameAction action) {
        super(ip, np);
        //checker si l'action est faisable
        if(action.entityNeeded.isPresent())
            if(!this.getShip().getEntityHere(action.getSailor().getGridPosition()).equals(action.entityNeeded.get())){
                throw new IllegalArgumentException("Sailor cannot perform this action Here");
            }

        this.action = action;
    }

    public GameAction getAction() {
        return action;
    }
}
