package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.ArrayList;
import java.util.List;

public class Cockpit implements ICockpit
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private InitGameParameters ip;
    private NextRoundParameters np;

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
