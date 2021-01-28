package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;

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

    @Override
    public RootObjective getObjective()
    {
        return new RegattaObjective(this);
    }
}
