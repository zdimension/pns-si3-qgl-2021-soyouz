package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.Collections;
import java.util.List;

public class GroundObjective extends Objective{
    private final GameAction action;

    //TODO
    public GroundObjective(GameAction action) {
        /*//checker si l'action est faisable
        if(action.entityNeeded.isPresent())
            if(!this.getShip().getEntityHere(action.getSailor().getGridPosition()).equals(action.entityNeeded.get())){
                throw new IllegalArgumentException("Sailor cannot perform this action Here");
            }
*/
        this.action = action;
    }

    public GameAction getAction() {
        return action;
    }

    @Override
    public boolean isValidated(GameState state)
    {
        return true;
    }

    @Override
    public List<GameAction> resolve(GameState state)
    {
        return Collections.singletonList(action);
    }
}
