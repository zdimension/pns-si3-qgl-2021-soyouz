package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.ArrayList;
import java.util.List;

public class Cockpit implements ICockpit
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private InitGameParameters ip;
    private NextRoundParameters np;

    /**
     * Parse all the initial Game Parameters into a InitGameParameters object.
     *
     * @param game The Json to init the game.
     */
    @Override
    public void initGame(String game)
    {
        try
        {
            ip = OBJECT_MAPPER.readValue(game, InitGameParameters.class);
            System.out.println("Init game input: " + ip);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Parse all the current Game Parameters into a NextRoundParameters object.
     * Determine which actions to do in order to win and create a matching Json.
     *
     * @param round The Json of the current state of the Game.
     * @return the corresponding Json.
     */
    @Override
    public String nextRound(String round)
    {
        try
        {
            np = OBJECT_MAPPER.readValue(round, NextRoundParameters.class);
            System.out.println("Next round input: " + np);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "[]";
    }

    @Override
    public List<String> getLogs()
    {
        return new ArrayList<>();
    }

    public InitGameParameters getIp()
    {
        return ip;
    }

    public  NextRoundParameters getNp()
    {
        return np;
    }
}
