package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;

@JsonTypeName("REGATTA")
public class RegattaGoal extends GameGoal
{
    private Checkpoint[] checkpoints;
}
