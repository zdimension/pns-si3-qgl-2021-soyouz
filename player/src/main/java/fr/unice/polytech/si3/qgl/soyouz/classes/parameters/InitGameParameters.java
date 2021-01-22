package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;

/**
 * Stock all parameters given to init the Game.
 */
public class InitGameParameters
{
    private GameGoal goal;
    private Bateau ship;
    private Marin[] sailors;
    private int shipCount;

    /**
     * Getter.
     * @return the Goal of the game.
     */
    public GameGoal getGoal()
    {
        return goal;
    }

    /**
     * Getter.
     * @return our Ship.
     */
    public Bateau getShip()
    {
        return ship;
    }

    /**
     * Getter.
     * @return our crew members.
     */
    public Marin[] getSailors()
    {
        return sailors.clone();
    }

    /**
     * Getter.
     * @return the number of ships in competition.
     */
    public int getShipCount()
    {
        return shipCount;
    }
}
