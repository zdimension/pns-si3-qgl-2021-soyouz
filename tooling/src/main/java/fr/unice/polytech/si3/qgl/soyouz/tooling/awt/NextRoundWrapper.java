package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

public class NextRoundWrapper
{
    private Marin[] sailors;
    private GameAction[] actions;

    public Marin[] getSailors()
    {
        return sailors;
    }

    public GameAction[] getActions()
    {
        return actions;
    }
}
