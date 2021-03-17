package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;

/**
 * RegattaGoal : Cross all the checkpoints to win.
 */
public class RegattaGoal implements GameGoal
{
    private Checkpoint[] checkpoints;

    public RegattaGoal(@JsonProperty("checkpoints") Checkpoint[] checkpoints)
    {
        this.checkpoints = checkpoints;
    }

    /**
     * Getters.
     *
     * @return the list of all checkpoints of the race.
     */
    public Checkpoint[] getCheckpoints()
    {
        return checkpoints.clone();
    }

    /**
     * Getters.
     *
     * @return the current goal.
     */
    @Override
    public RootObjective getObjective()
    {
        return new RegattaObjective(this);
    }
}
