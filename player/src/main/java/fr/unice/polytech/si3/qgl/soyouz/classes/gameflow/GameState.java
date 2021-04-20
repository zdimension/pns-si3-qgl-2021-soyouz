package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

/**
 * Class to save all parameters of the current game.
 */
public class GameState
{
    private final InitGameParameters ip;
    private final NextRoundParameters np;
    private final boolean recalculatePathfinding;

    /**
     * Constructor.
     *
     * @param ip The parameters at the creation of the game.
     * @param np The parameters of the current round.
     */
    public GameState(InitGameParameters ip, NextRoundParameters np)
    {
        this(ip, np, true);
    }

    public GameState(InitGameParameters ip, NextRoundParameters np, boolean recalculatePathfinding)
    {
        this.ip = ip;
        this.np = np;
        this.recalculatePathfinding = recalculatePathfinding;
    }

    /**
     * Getters.
     *
     * @return the initial game parameters.
     */
    public InitGameParameters getIp()
    {
        return ip;
    }

    /**
     * Getters.
     *
     * @return the next round parameters.
     */
    public NextRoundParameters getNp()
    {
        return np;
    }

    public boolean isRecalculatePathfinding()
    {
        return recalculatePathfinding;
    }
}
