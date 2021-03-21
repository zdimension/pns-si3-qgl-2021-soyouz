package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;

/**
 * RegattaGoal : Cross all the checkpoints to win.
 */
public class RegattaGoal implements GameGoal
{
    private final Checkpoint[] checkpoints;

    /**
     * Constructor.
     *
     * @param checkpoints The list of the checkpoints to be cross.
     */
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
}
