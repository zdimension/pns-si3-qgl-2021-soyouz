package fr.unice.polytech.si3.qgl.soyouz.tooling.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
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
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import fr.unice.polytech.si3.qgl.soyouz.tooling.Application;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulatorModel
{
    private static final Logger logger = Logger.getLogger(SimulatorModel.class.getSimpleName());
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
    public final ArrayList<OnboardEntity> usedEntities;
    public final HashMap<Marin, PosOnShip> sailorPositions = new HashMap<>();
    private final int COMP_STEPS = 10;
    public int speed = 0;
    public int currentStep = 0;
    public NextRoundParameters[] nps;
    public ICockpit[] cockpits;
    public boolean playMode = false;
    public SimulatorListener listener = null;
    public long nextRoundTime = -1;
    private double[] rotIncrement;
    private double[] spdIncrement;
    private RunnerParameters model;
    private int[] currentCheckpoints;
    private boolean[] vigies;
    private boolean inGame = true;
    private String lastLoadedFile;
    public static Class<?> cockpitClass = Cockpit.class;

    public SimulatorModel()
    {
        usedEntities = new ArrayList<>();
    }

    public void loadFile(String filename, boolean shuffleSailors)
    {
        try
        {
            if (filename.contains("_multi_"))
            {
                var pref = filename.substring(0, filename.indexOf('_'));
                var count = (int) Files.walk(Paths.get("games"))
                    .map(Path::toString)
                    .filter(name -> name.contains(pref + "_multi") && name.contains("_next"))
                    .count();
                var ips = new InitGameParameters[count];
                var nps = new NextRoundParameters[count];
                for (int i = 0; i < count; i++)
                {
                    var fn = pref + "_multi_" + (i + 1);
                    ips[i] = Application.OBJECT_MAPPER.readValue(Files.readString(Path.of(fn +
                            "_real.json")),
                        InitGameParameters.class);
                    nps[i] = Application.OBJECT_MAPPER.readValue(Files.readString(Path.of(fn +
                            "_real_next.json"))
                        , NextRoundParameters.class);
                }
                model = new RunnerParameters(ips, nps, shuffleSailors);
            }
            else if (filename.contains("_real"))
            {
                model = new RunnerParameters(
                    Application.OBJECT_MAPPER.readValue(Files.readString(Path.of(filename)),
                        InitGameParameters.class),
                    Application.OBJECT_MAPPER.readValue(Files.readString(Path.of(filename.replace("_real",
                        "_real_next")))
                        , NextRoundParameters.class),
                    shuffleSailors
                );
            }
            else
            {
                var ipt = Files.readString(Path.of(filename));
                model = Application.OBJECT_MAPPER.readValue(ipt, RunnerParameters.class);
            }
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
            return;
        }

        cockpits = new ICockpit[model.getShipCount()];
        for (int i = 0; i < model.getShipCount(); i++)
        {
            try
            {
                cockpits[i] = (ICockpit)cockpitClass.getConstructor().newInstance();
                cockpits[i].initGame(Application.OBJECT_MAPPER.writeValueAsString(model.getIp(i,
                    true)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        currentCheckpoints = new int[cockpits.length];
        nps = new NextRoundParameters[cockpits.length];
        vigies = new boolean[cockpits.length];
        rotIncrement = new double[cockpits.length];
        spdIncrement = new double[cockpits.length];

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

    public void reset(boolean shuffleSailors)
    {
        loadFile(lastLoadedFile, shuffleSailors);
    }

    public void reset()
    {
        reset(false);
    }

    private void loadNextRound()
    {
        for (int i = 0; i < cockpits.length; i++)
        {
            nps[i] = model.getNp(i, vigies[i]);
            vigies[i] = false;
        }
        if (listener != null)
        {
            listener.npChanged();
        }
    }

    static class JacksonBugfixUglyHack
    {
        InitGameParameters ip;
        GameAction[] act;

        public JacksonBugfixUglyHack()
        {

        }

        public InitGameParameters getIp()
        {
            return ip;
        }

        public GameAction[] getAct()
        {
            return act;
        }
    }

    public void computeRound()
    {
        if (!inGame)
        {
            reset();
        }

        loadNextRound();

        sailorPositions.clear();
        for (int i = 0; i < cockpits.length; i++)
        {
            ICockpit cockpit = cockpits[i];

            var time = System.currentTimeMillis();
            GameAction[] res = new GameAction[0];
            try
            {
                res = Application.OBJECT_MAPPER.readValue("{\"ip\":" + Application.OBJECT_MAPPER.writeValueAsString(model.getIp(i, true)) + ",\"act\":" +
                    cockpit.nextRound(Application.OBJECT_MAPPER.writeValueAsString(nps[i]).replace("\"type\":\"Bateau\",", "")) + "}", JacksonBugfixUglyHack.class).act;
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
            time = System.currentTimeMillis() - time;
            nextRoundTime += time;
            try
            {
                logger.log(Level.CONFIG, Application.OBJECT_MAPPER.writeValueAsString(res));
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
            Arrays.sort(res, Comparator.comparingInt(act -> ACTIONS_ORDER.indexOf(act.getClass())));

            logger.log(Level.CONFIG, "Next round took " + time + "ms");
            var activeOars = new ArrayList<Rame>();
            var rudderRotate = 0d;
            Bateau ship = model.getShip(i);
            for (GameAction act : res)
            {
                var sail = model.getSailorById(i, act.getSailorId()).get();
                var entType = act.entityNeeded;
                if (entType != null)
                {
                    var entOpt = ship.getEntityHere(sail.getPos());
                    if (entOpt.isPresent())
                    {
                        var ent = entOpt.get();

                        if (!(entType.isInstance(ent)))
                        {
                            logger.log(Level.SEVERE,
                                "INVALID ENTITY TYPE FOR " + act + ", EXPECTED " + entType + " " +
                                    "GOT " + ent.getClass());
                            continue;
                        }

                        if (i == 0)
                        {
                            if (usedEntities.contains(ent))
                            {
                                logger.log(Level.SEVERE, "ENTITY ALREADY USED FOR ACTION " + act);
                                continue;
                            }

                            usedEntities.add(ent);
                        }

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
                            vigies[i] = true;
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
                        if (i == 0)
                        {
                            sailorPositions.put(sail, sail.getPos());
                        }
                        sail.moveRelative(mv.getDelta());
                        if (sail.getX() < 0 || sail.getX() >= ship.getDeck().getLength()
                            || sail.getY() < 0 || sail.getY() >= ship.getDeck().getWidth())
                        {
                            logger.log(Level.SEVERE, "SAILOR " + sail.getId() + " MOVED OUTSIDE " +
                                "THE " +
                                "DECK");
                        }
                    }
                }
            }
            var noars = ship.getNumberOar();
            var oarFactor = 165.0 * activeOars.size() / noars;
            var activeOarsLeft = activeOars.stream().filter(o -> o.getY() == 0).count();
            var activeOarsRight = activeOars.size() - activeOarsLeft;
            var oarRot = (activeOarsRight - activeOarsLeft) * Math.PI / noars;
            var totalRot = oarRot + rudderRotate;
            rotIncrement[i] = totalRot / COMP_STEPS;
            spdIncrement[i] = oarFactor / COMP_STEPS;
        }
    }

    public Duration runBenchmark(int N)
    {
        long total = 0;
        var results = new long[N];
        System.out.println("Starting benchmark for " + N + " games");
        var old = Level.CONFIG;
        for (var i = -1; i < N; i++)
        {
            Cockpit.defaultLogLevel = Level.OFF;
            Util.updateLogLevel(Level.OFF);
            reset();
            playMode = true;
            computeRound();
            currentStep = 0;
            while (playMode)
            {
                processRound();
            }
            if (i != -1) // to ignore first round
            {
                results[i] = nextRoundTime;
                total += nextRoundTime;
            }
            System.out.println("End game " + (i + 1) + " - " + nextRoundTime / 1000.0);
        }
        Util.updateLogLevel(Level.CONFIG);
        Cockpit.defaultLogLevel = Level.CONFIG;
        var avg = total / N;
        System.out.println("AVG = " + avg / 1000.0 + "; TIMES = " + Arrays.toString(results));
        return Duration.ofMillis(avg);
    }

    public void processRound()
    {
        for (int i = 0; i < cockpits.length; i++)
        {
            if (currentCheckpoints[i] >= getCheckpoints().length)
            {
                continue;
            }

            var linSpeed = spdIncrement[i];
            Bateau ship = model.getShip(i);
            if (model.getWind() != null)
            {
                var sails = ship.getSails();
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
                    Wind wind = model.getWind();
                    linSpeed += ((double) counts.open / counts.total) * wind.getStrength() * Math.cos(wind.getOrientation() - ship.getPosition().getOrientation()) / COMP_STEPS;
                }
            }

            var cur = ship.getPosition();
            var linear = Point2d.fromPolar(linSpeed, cur.getOrientation());

            for (ShapedEntity visibleEntity : nps[i].getVisibleEntities())
            {
                if (visibleEntity instanceof Stream)
                {
                    var str = (Stream) visibleEntity;
                    if (str.contains(ship.getPosition()))
                    {
                        linear = linear.add(str.getProjectedStrength().mul(1d / COMP_STEPS));
                    }
                }
            }

            ship.setPosition(cur.add(linear).add(0, 0, rotIncrement[i]));
            logger.log(Level.CONFIG, "Ship position : " + ship.getPosition());

            if (getCheckpoints()[currentCheckpoints[i]].contains(ship.getPosition()))
            {
                currentCheckpoints[i]++;
            }
        }

        if (listener != null)
        {
            listener.updateRequired();
        }

        if (++currentStep >= COMP_STEPS)
        {
            if (Arrays.stream(currentCheckpoints).allMatch(cp -> cp >= getCheckpoints().length))
            {
                if (listener != null)
                {
                    listener.gameFinished();
                }
                playMode = false;
                logger.log(Level.FINE, Duration.ofMillis(nextRoundTime).toString());
                inGame = false;
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
        return model.getShip(0);
    }

    public GameGoal getGoal()
    {
        return model.getGoal();
    }

    public Marin[] getSailors()
    {
        return model.getSailors(0);
    }

    public Wind getWind()
    {
        return model.getWind();
    }

    public Bateau[] getShips()
    {
        return model.getShips();
    }
}
