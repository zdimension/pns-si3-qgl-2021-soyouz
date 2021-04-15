package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

/**
 * Control panel of the whole game. Here happens all the magic.
 */
public class Cockpit implements ICockpit
{
    private static final Queue<String> logList = new ConcurrentLinkedQueue<>();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(Cockpit.class.getSimpleName());
    private static final boolean ENABLE_TRACE = false;

    static
    {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);

        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tF %1$tT:%1$tL %4$s %3$s : %5$s%6$s%n");

        var fmt = new SimpleFormatter();
        logger.getParent().addHandler(new Handler()
        {
            @Override
            public void publish(LogRecord record)
            {
                logList.add(fmt.format(record));
            }

            @Override
            public void flush()
            {
                //Not necessary to implement but you know.. override..
            }

            @Override
            public void close()
            {
                //Not necessary to implement but you know.. override..
            }
        });
    }
    
    public final Map<String, ShapedEntity> entityMemory = new HashMap<>();
    private InitGameParameters ip;
    private RootObjective objective;

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
            updateLogLevel();
            initGameInternal(OBJECT_MAPPER.readValue(game, InitGameParameters.class));
        }
        catch (Exception e)
        {
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void initGameInternal(InitGameParameters ip)
    {
        trace();
        try
        {
            this.ip = ip;
            updateLogLevel();
            if (ip.getGoal() instanceof RegattaGoal)
            {
                objective = new RegattaObjective((RegattaGoal) ip.getGoal(), ip);
            }
            logger.info("Init game input: " + OBJECT_MAPPER.writeValueAsString(ip));
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
            return "[]";
        }
    }

    public GameAction[] nextRoundInternal(NextRoundParameters np)
    {
        trace();
        try
        {
            logger.info("Next round input: " + np);
            var added = 0;
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
            var changed = added != 0;
            changed = true;
            np = new NextRoundParameters(np.getShip(), np.getWind(),
                entityMemory.values().toArray(new ShapedEntity[0]));
            var actions = objective.resolve(new GameState(ip, np, changed));
            return actions.toArray(GameAction[]::new);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error writing nextRound : " + e.getMessage());
            e.printStackTrace();
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

    /**
     * Update the log level.
     */
    private void updateLogLevel()
    {
        var logLevel = Level.CONFIG;
        var root = LogManager.getLogManager().getLogger("");
        root.setLevel(logLevel);
        Arrays.stream(root.getHandlers()).forEach(h -> h.setLevel(logLevel));
    }
}