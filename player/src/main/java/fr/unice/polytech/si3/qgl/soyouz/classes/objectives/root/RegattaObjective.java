package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;

public class RegattaObjective extends RootObjective
{
    private RegattaGoal goalData;

    public RegattaObjective(RegattaGoal goalData)
    {
        this.goalData = goalData;
    }

    @Override
    public void update(GameState state)
    {
        super.update(state);


    }
}
