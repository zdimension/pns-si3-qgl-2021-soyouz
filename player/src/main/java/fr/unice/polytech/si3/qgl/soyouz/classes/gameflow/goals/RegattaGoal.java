package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;

public class RegattaGoal extends GameGoal
{
    private Checkpoint[] checkpoints;

    public Checkpoint[] getCheckpoints()
    {
        return checkpoints.clone();
    }
}
