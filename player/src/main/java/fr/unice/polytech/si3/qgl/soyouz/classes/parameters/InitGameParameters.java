package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;

public class InitGameParameters
{
    private GameGoal goal;
    private Bateau ship;
    private Marin[] sailors;
    private int shipCount;

    public GameGoal getGoal()
    {
        return goal;
    }

    public Bateau getShip()
    {
        return ship;
    }

    public Marin[] getSailors()
    {
        return sailors.clone();
    }

    public int getShipCount()
    {
        return shipCount;
    }
}
