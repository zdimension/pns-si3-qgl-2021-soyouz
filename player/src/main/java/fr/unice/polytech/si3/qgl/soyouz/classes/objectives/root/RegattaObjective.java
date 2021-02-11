package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;

/**
 * Race type goal.
 */
public class RegattaObjective extends RootObjective
{
    private RegattaGoal goalData;

    /**
     * Constructor.
     *
     * @param goalData The data of the race.
     */
    public RegattaObjective(RegattaGoal goalData)
    {
        this.goalData = goalData;
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
    }
}
