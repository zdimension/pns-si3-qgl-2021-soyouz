package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;

public abstract class RootObjective extends CompositeObjective
{
    @Override
    public boolean isValidated(GameState state)
    {
        return false;
    }
}
