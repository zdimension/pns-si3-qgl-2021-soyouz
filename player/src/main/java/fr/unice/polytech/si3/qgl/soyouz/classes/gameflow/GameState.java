package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

public class GameState
{
    InitGameParameters ip;
    NextRoundParameters np;

    public GameState(InitGameParameters ip, NextRoundParameters np)
    {
        this.ip = ip;
        this.np = np;
    }
}
