package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.util.ArrayList;
import java.util.List;

public class Cockpit implements ICockpit
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void initGame(String game)
    {
        try
        {
            var ip = OBJECT_MAPPER.readValue(game, InitGameParameters.class);
            System.out.println("Init game input: " + ip);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String nextRound(String round)
    {
        System.out.println("Next round input: " + round);
        return "[]";
    }

    @Override
    public List<String> getLogs()
    {
        return new ArrayList<>();
    }
}
