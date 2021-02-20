package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;

import java.util.ArrayList;
import java.util.List;

/**
 * Race type objective.
 */
public class RegattaObjective extends RootObjective
{
    private RegattaGoal goalData;

    private int numCheckpoint = 0;
    private CheckpointObjective currentCheckpoint;


    /**
     * Constructor.
     *
     * @param goalData The data of the race.
     */
    public RegattaObjective(RegattaGoal goalData)
    {
        this.goalData = goalData;
        currentCheckpoint = null;
    }

    /**
     * Method to update the state of the goal.
     *
     * @param state New state to set.
     */
    @Override
    public void update(GameState state)
    {
        super.update(state);
        if(currentCheckpoint != null)
        if(currentCheckpoint.isValidated(state)){
            if(goalData.getCheckpoints().length - 1 > numCheckpoint){
                numCheckpoint++;
            }
        }
    }

    @Override
    public List<GameAction> resolve(GameState state) {
        currentCheckpoint = new CheckpointObjective(goalData.getCheckpoints()[numCheckpoint]);
        return currentCheckpoint.resolve(state);
    }
}
