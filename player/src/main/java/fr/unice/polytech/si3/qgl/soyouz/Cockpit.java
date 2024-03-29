package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.AutreBateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Control panel of the whole game. Here happens all the magic.
 */
public class Cockpit implements ICockpit
{
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Queue<String> logList = new ConcurrentLinkedQueue<>();
    private static final Logger logger = Logger.getLogger(Cockpit.class.getSimpleName());
    private static final boolean ENABLE_TRACE = false;
    public static Level defaultLogLevel = Level.CONFIG;

    static
    {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);

        Util.configureLoggerFormat();

        try
        {
            logger.getParent().removeHandler(logger.getParent().getHandlers()[0]);
        }
        catch (Exception e)
        {
            //
        }
        logger.getParent().addHandler(new ListLogHandler(logList)); //NOSONAR
    }

    public final Map<String, ShapedEntity> entityMemory = new HashMap<>();
    private InitGameParameters ip;
    private RootObjective objective;

    /**
     * Logs data
     */
    public static void trace()
    {
        if (!ENABLE_TRACE)
        {
            return;
        }

        logger.info(
            Thread.currentThread().getStackTrace()[2].toString() +
                " from " +
                Thread.currentThread().getStackTrace()[3].toString()
        );
    }

    /**
     * Parse all the initial Game Parameters into a InitGameParameters object.
     *
     * @param game The Json to init the game.
     */
    @Override
    public void initGame(String game)
    {
        trace();
        try
        {
            Util.updateLogLevel(defaultLogLevel);
            initGameInternal(OBJECT_MAPPER.readValue(game, InitGameParameters.class));
        }
        catch (Exception e)
        {
            logger.severe(e.getMessage());
            e.printStackTrace(); //NOSONAR
        }
    }

    /**
     * Initializes the game
     *
     * @param ip
     */
    public void initGameInternal(InitGameParameters ip)
    {
        trace();
        try
        {
            this.ip = ip;
            Util.updateLogLevel(defaultLogLevel);
            if (ip.getGoal() instanceof RegattaGoal)
            {
                objective = new RegattaObjective((RegattaGoal) ip.getGoal(), ip);
            }
            logger.info("Init game input: " + OBJECT_MAPPER.writeValueAsString(ip));
        }
        catch (Exception e)
        {
            e.printStackTrace(); //NOSONAR
        }
    }

    /**
     * Parse all the current Game Parameters into a NextRoundParameters object. Determine which
     * actions to do in order to win and create a matching Json.
     *
     * @param round The Json of the current state of the Game.
     * @return the corresponding Json.
     */
    @Override
    public String nextRound(String round)
    {
        trace();
        try
        {
            NextRoundParameters np = OBJECT_MAPPER.readValue(round, NextRoundParameters.class);
            return OBJECT_MAPPER.writeValueAsString(nextRoundInternal(np));
        }
        catch (Exception e)
        {
            logger.severe("Error writing nextRound : " + e.getMessage());
            e.printStackTrace(); //NOSONAR
            return "[]";
        }
    }

    /**
     * @param np
     * @return actions to be performed during the next round
     */
    public GameAction[] nextRoundInternal(NextRoundParameters np)
    {
        trace();
        try
        {
            logger.info("Next round input: " + OBJECT_MAPPER.writeValueAsString(np));
            var added = 0;
            entityMemory.entrySet().removeIf(pair -> pair.getValue() instanceof AutreBateau);
            for (ShapedEntity ent : np.getVisibleEntities())
            {
                var json = OBJECT_MAPPER.writeValueAsString(ent);
                if (!entityMemory.containsKey(json))
                {
                    entityMemory.put(json, ent);
                    added++;
                }
            }
            logger.info("Added " + added + " entities; total " + entityMemory.size());
            logger.info(OBJECT_MAPPER.writeValueAsString(entityMemory.values()));
            np = new NextRoundParameters(np.getShip(), np.getWind(),
                entityMemory.values().toArray(new ShapedEntity[0]));
            var actions = objective.resolve(new GameState(ip, np, true));
            return actions.toArray(GameAction[]::new);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error writing nextRound : " + e.getMessage());
            e.printStackTrace(); //NOSONAR
            return new GameAction[0];
        }
    }

    /**
     * Getters.
     *
     * @return a list of log.
     */
    @Override
    public List<String> getLogs()
    {
        return new ArrayList<>(logList);
    }

    public CheckpointObjective getCurrentCheckpoint()
    {
        return ((RegattaObjective) objective).getCurrentCheckpoint();
    }

}