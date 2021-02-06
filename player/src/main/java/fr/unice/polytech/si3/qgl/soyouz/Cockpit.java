package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Cockpit implements ICockpit
{
    private static final Queue<String> logList = new ConcurrentLinkedQueue<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private InitGameParameters ip;
    private NextRoundParameters np;
    private RootObjective objective;

    static
    {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void log(String message)
    {
        System.out.println(message);
        logList.add(message);
    }

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
            objective = ip.getGoal().getObjective();
            log("Init game input: " + ip);
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
            log("Next round input: " + np);
            objective.update(new GameState(ip, np));
            return OBJECT_MAPPER.writeValueAsString(Arrays.stream(ip.getSailors()).filter(
                m -> ip.getShip().getEntityHere(m.getX(), m.getY()).orElse(null) instanceof Rame
            ).map(OarAction::new).toArray(OarAction[]::new));
            //return OBJECT_MAPPER.writeValueAsString(objective.resolve(new GameState(ip, np)).toArray());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "[]";
        }
    }

    @Override
    public List<String> getLogs()
    {
        return new ArrayList<>(logList);
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
