package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;

/**
 * RegattaGoal : Cross all the checkpoints to win.
 */
public class RegattaGoal extends GameGoal
{
    private Checkpoint[] checkpoints;

    /**
     * Getters.
     * @return the list of all checkpoints of the race.
     */
    public Checkpoint[] getCheckpoints()
    {
        return checkpoints.clone();
    }
}
