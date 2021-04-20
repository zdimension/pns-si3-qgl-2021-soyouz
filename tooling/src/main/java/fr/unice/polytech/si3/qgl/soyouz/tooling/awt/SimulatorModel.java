package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SimulatorModel
{
    private static final Logger logger = Logger.getLogger(SimulatorModel.class.getSimpleName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final java.util.List<Class<? extends GameAction>> ACTIONS_ORDER =
        java.util.List.of(
            MoveAction.class,
            OarAction.class,
            LiftSailAction.class,
            LowerSailAction.class,
            TurnAction.class,
            WatchAction.class
            // turn cannon
            // load cannon
            // shoot cannon
        );
    final ArrayList<OnboardEntity> usedEntities;
    final HashMap<Marin, PosOnShip> sailorPositions = new HashMap<>();
    private final int COMP_STEPS = 10;
    public int speed = 0;
    public int currentStep = 0;
    public NextRoundParameters np;
    public Cockpit cockpit;
    public boolean playMode = false;
    public SimulatorListener listener = null;
    long nextRoundTime = -1;
    private double rotIncrement;
    private double spdIncrement;
    private RunnerParameters model;
    private int currentCheckpoint;
    private boolean vigie = false;
    private boolean inGame = true;
    private String lastLoadedFile;

    public SimulatorModel()
    {
        usedEntities = new ArrayList<>();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);
    }

    public void loadFile(String filename, boolean shuffleSailors)
    {
        try
        {
            if (filename.contains("_real"))
            {
                model = new RunnerParameters(
                    OBJECT_MAPPER.readValue(Files.readString(Path.of(filename)),
                        InitGameParameters.class),
                    OBJECT_MAPPER.readValue(Files.readString(Path.of(filename.replace("_real",
                        "_real_next")))
                        , NextRoundParameters.class),
                    shuffleSailors
                );
            }
            else
            {
                var ipt = Files.readString(Path.of(filename));
                model = OBJECT_MAPPER.readValue(ipt, RunnerParameters.class);
            }
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
            return;
        }

        np = null;
        CheckpointObjective.graph = null;
        currentCheckpoint = 0;
        cockpit = new Cockpit();
        cockpit.initGameInternal(model.getIp(true));
        usedEntities.clear();
        loadNextRound();
        sailorPositions.clear();
        nextRoundTime = -1;
        inGame = true;
        if (listener != null)
        {
            listener.fileLoaded();
        }
        lastLoadedFile = filename;
    }

    private Checkpoint[] getCheckpoints()
    {
        return ((RegattaGoal) model.getGoal()).getCheckpoints();
    }

    void reset(boolean shuffleSailors)
    {
        loadFile(lastLoadedFile, shuffleSailors);
    }

    void reset()
    {
        reset(false);
    }

    private void loadNextRound()
    {
        np = model.getNp(vigie);
        vigie = false;
        if (listener != null)
        {
            listener.npChanged();
        }
    }

    void computeRound()
    {
        if (!inGame)
        {
            reset();
        }

        sailorPositions.clear();
        loadNextRound();

        var time = System.currentTimeMillis();
        var res = cockpit.nextRoundInternal(np);
        time = System.currentTimeMillis() - time;
        nextRoundTime += time;
        try
        {
            logger.log(Level.CONFIG, OBJECT_MAPPER.writeValueAsString(res));
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        Arrays.sort(res, Comparator.comparingInt(act -> ACTIONS_ORDER.indexOf(act.getClass())));

        logger.log(Level.CONFIG, "Next round took " + time + "ms");
        var activeOars = new ArrayList<Rame>();
        var rudderRotate = 0d;
        for (GameAction act : res)
        {
            /*if (act.getSailor() == null)
            {
                System.out.println("SAILOR IS NULL!");
                continue;
            }*/
            var sail = model.getSailors()[act.getSailorId()];
            var entType = act.entityNeeded;
            if (entType != null)
            {
                var entOpt = model.getShip().getEntityHere(act.getSailor().getPos());
                if (entOpt.isPresent())
                {
                    var ent = entOpt.get();

                    if (!(entType.isInstance(ent)))
                    {
                        logger.log(Level.SEVERE,
                            "INVALID ENTITY TYPE FOR " + act + ", EXPECTED " + entType + " GOT " + ent.getClass());
                        continue;
                    }

                    if (usedEntities.contains(ent))
                    {
                        logger.log(Level.SEVERE, "ENTITY ALREADY USED FOR ACTION " + act);
                        continue;
                    }

                    //System.out.println("Entity " + ent + " used by sailor " + act
                    // .getSailorId());

                    usedEntities.add(ent);

                    if (act instanceof OarAction)
                    {
                        activeOars.add((Rame) ent);
                    }
                    else if (act instanceof TurnAction)
                    {
                        rudderRotate = ((TurnAction) act).getRotation();
                    }
                    else if (act instanceof LiftSailAction)
                    {
                        ((Voile) ent).setOpenned(true);
                    }
                    else if (act instanceof LowerSailAction)
                    {
                        ((Voile) ent).setOpenned(false);
                    }
                    else if (act instanceof WatchAction)
                    {
                        vigie = true;
                    }
                }
                else
                {
                    logger.log(Level.SEVERE, "ENTITY MISSING FOR ACTION " + act);
                }
            }
            else
            {
                if (act instanceof MoveAction)
                {
                    var mv = (MoveAction) act;
                    sailorPositions.put(sail, sail.getPos());
                    sail.moveRelative(mv.getDelta());
                    if (sail.getX() < 0 || sail.getX() >= model.getShip().getDeck().getLength()
                        || sail.getY() < 0 || sail.getY() >= model.getShip().getDeck().getWidth())
                    {
                        logger.log(Level.SEVERE, "SAILOR " + sail.getId() + " MOVED OUTSIDE THE " +
                            "DECK");
                    }
                }
            }
        }
        var noars = model.getShip().getNumberOar();
        var oarFactor = 165.0 * activeOars.size() / noars;
        var activeOarsLeft = activeOars.stream().filter(o -> o.getY() == 0).count();
        var activeOarsRight = activeOars.size() - activeOarsLeft;
        var oarRot = (activeOarsRight - activeOarsLeft) * Math.PI / noars;
        var totalRot = oarRot + rudderRotate;
        rotIncrement = totalRot / COMP_STEPS;
        spdIncrement = oarFactor / COMP_STEPS;
    }

    public Duration runBenchmark(int N)
    {
        long total = 0;
        var results = new long[N];
        logger.log(Level.FINE, "Starting benchmark for " + N + " games");
        var current = LogManager.getLogManager().getLogger("").getLevel();
        for (var i = 0; i < N; i++)
        {
            reset();
            Util.updateLogLevel(Level.OFF);
            playMode = true;
            computeRound();
            currentStep = 0;
            while (playMode)
            {
                processRound(null);
            }
            results[i] = nextRoundTime;
            total += nextRoundTime;
        }
        Util.updateLogLevel(current);
        var avg = Duration.ofMillis(total / N);
        logger.log(Level.INFO, "AVG = " + avg + "; TIMES = " + Arrays.toString(results));
        return avg;
    }

    public void processRound(ActionEvent ignored)
    {
        var linSpeed = spdIncrement;
        if (np.getWind() != null)
        {
            var sails = Util.filterType(Arrays.stream(np.getShip().getEntities()),
                Voile.class);
            var counts = new Object()
            {
                int open;
                int total;
            };
            sails.forEach(voile ->
            {
                if (voile.isOpenned())
                {
                    counts.open++;
                }
                counts.total++;
            });
            if (counts.total > 0)
            {
                Wind wind = np.getWind();
                linSpeed += ((double) counts.open / counts.total) * wind.getStrength() * Math.cos(wind.getOrientation() - np.getShip().getPosition().getOrientation()) / COMP_STEPS;
            }
        }

        var cur = model.getShip().getPosition();
        var linear = Point2d.fromPolar(linSpeed, cur.getOrientation());

        for (ShapedEntity visibleEntity : np.getVisibleEntities())
        {
            if (visibleEntity instanceof Stream)
            {
                var str = (Stream) visibleEntity;
                if (str.contains(model.getShip().getPosition()))
                {
                    linear = linear.add(str.getProjectedStrength().mul(1d / COMP_STEPS));
                }
            }
        }

        model.getShip().setPosition(cur.add(linear).add(0, 0, rotIncrement));
        logger.log(Level.CONFIG, "Ship position : " + model.getShip().getPosition());

        if (listener != null)
        {
            listener.updateRequired();
        }

        if (++currentStep >= COMP_STEPS)
        {
            if (getCheckpoints()[currentCheckpoint].contains(model.getShip().getPosition()))
            {
                currentCheckpoint++;
                if (currentCheckpoint >= getCheckpoints().length)
                {
                    if (listener != null)
                    {
                        listener.gameFinished();
                    }
                    playMode = false;
                    logger.log(Level.FINE, Duration.ofMillis(nextRoundTime).toString());
                    inGame = false;
                }
            }

            usedEntities.clear();

            if (playMode)
            {
                computeRound();
            }
            else
            {
                if (listener != null)
                {
                    listener.turnEnd();
                }
            }

            currentStep = 0;
        }
    }

    public Bateau getShip()
    {
        return model.getShip();
    }

    public GameGoal getGoal()
    {
        return model.getGoal();
    }

    public Marin[] getSailors()
    {
        return model.getSailors();
    }

    public Wind getWind()
    {
        return np.getWind();
    }
}
