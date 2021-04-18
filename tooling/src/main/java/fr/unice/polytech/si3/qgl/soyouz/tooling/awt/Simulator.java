package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Simulator extends JFrame
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String[] SPEEDS = { "Slow", "Medium", "Fast" };
    private static final int[] DELAYS = { 50, 10, 0 };
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
    private final Timer timer;
    private final int COMP_STEPS = 10;
    private final SimulatorCanvas canvas;
    private final ArrayList<OnboardEntity> usedEntities;
    private final JButton btnNext;
    private final JButton btnPlay;
    private final JComboBox<Object> cbxFiles;
    private int speed = 0;
    private int currentStep = 0;
    private double rotIncrement;
    private double spdIncrement;
    private NextRoundParameters np;
    private RunnerParameters model;
    private Cockpit cockpit;
    private boolean playMode = false;
    private int currentCheckpoint;
    private LocalDateTime gameStart = null;
    private boolean vigie = false;

    public Simulator() throws IOException
    {
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(600, 600);

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);

        var topcont = new Panel();
        topcont.setLayout(new BoxLayout(topcont, BoxLayout.X_AXIS));

        btnNext = new JButton("Next");
        topcont.add(btnNext);

        btnPlay = new JButton("Play");
        topcont.add(btnPlay);

        var btnReset = new JButton("Reset");
        topcont.add(btnReset);
        btnReset.addActionListener(e ->
        {
            reset();
        });

        usedEntities = new ArrayList<>();

        canvas = new SimulatorCanvas(null, usedEntities);
        add(canvas, BorderLayout.CENTER);

        var btnClear = new JButton("Clear path");
        topcont.add(btnClear);
        btnClear.addActionListener(e ->
        {
            canvas.clearHistory();
        });

        var btnPath = new JButton("Hide graph");
        btnPath.addActionListener(e ->
        {
            if (canvas.drawPath = !canvas.drawPath)
            {
                btnPath.setText("Hide graph");
            }
            else
            {
                btnPath.setText("Show graph");
            }
            canvas.repaint();
        });
        topcont.add(btnPath);


        add(topcont, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println(cockpit.getLogs());
                dispose();
            }
        });

        timer = new Timer(0, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
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
                System.out.println("Ship position : " + model.getShip().getPosition());

                canvas.repaint();

                if (++currentStep >= COMP_STEPS)
                {
                    if (getCheckpoints()[currentCheckpoint].contains(model.getShip().getPosition()))
                    {
                        currentCheckpoint++;
                        if (currentCheckpoint >= getCheckpoints().length)
                        {
                            btnPlay.doClick();
                            System.out.println(Duration.between(gameStart, LocalDateTime.now()));
                            gameStart = null;
                        }
                    }

                    usedEntities.clear();

                    if (playMode)
                    {
                        computeRound();
                    }
                    else
                    {
                        timer.stop();
                        btnNext.setEnabled(true);
                    }

                    currentStep = 0;
                }
            }
        });

        var btnCenter = new JButton("Center view");
        topcont.add(btnCenter);
        btnCenter.addActionListener(e ->
        {
            canvas.centerView(true);
        });

        var cbxSpeed = new JComboBox<>(SPEEDS);
        cbxSpeed.addItemListener(e ->
        {
            this.speed = cbxSpeed.getSelectedIndex();
            timer.setDelay(DELAYS[this.speed]);
        });
        cbxSpeed.setSelectedIndex(2);
        topcont.add(cbxSpeed);

        cbxFiles = new JComboBox<Object>(Files.walk(Paths.get("games"))
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(name -> name.contains("Week") && !name.contains("_next"))
            .sorted().toArray());
        cbxFiles.addItemListener(e ->
        {
            loadFile(e.getItem().toString());
            canvas.centerView(true);
        });
        topcont.add(cbxFiles);

        reset();

        btnNext.addActionListener(event ->
        {
            playMode = false;
            playRound();
        });

        btnPlay.addActionListener(event ->
        {
            if (gameStart == null)
                reset();
            if (timer.isRunning())
            {
                playMode = false;
                btnPlay.setText("Play");
            }
            else
            {
                btnPlay.setText("Stop");
                playMode = true;
                playRound();
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                playMode = false;

                super.windowClosing(e);
            }

            @Override
            public void windowOpened(WindowEvent e)
            {
                super.windowOpened(e);

                canvas.centerView(true);
            }
        });
    }

    private void loadFile(String filename)
    {
        timer.stop();
        btnNext.setEnabled(true);
        btnPlay.setText("Play");
        try
        {
            if (filename.contains("_real"))
            {
                model = new RunnerParameters(
                    OBJECT_MAPPER.readValue(Files.readString(Path.of(filename)),
                        InitGameParameters.class),
                    OBJECT_MAPPER.readValue(Files.readString(Path.of(filename.replace("_real",
                        "_real_next")))
                        , NextRoundParameters.class)
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
        canvas.setModel(model.getIp(false));
        currentCheckpoint = 0;
        cockpit = new Cockpit();
        cockpit.initGameInternal(model.getIp(true));
        usedEntities.clear();
        canvas.reset();
        canvas.setCockpit(cockpit);
        loadNextRound();
    }

    private Checkpoint[] getCheckpoints()
    {
        return ((RegattaGoal) model.getGoal()).getCheckpoints();
    }

    private void reset()
    {
        loadFile((String) Objects.requireNonNull(cbxFiles.getSelectedItem()));
    }

    private void playRound()
    {
        btnNext.setEnabled(false);

        computeRound();

        currentStep = 0;
        timer.start();
    }

    private void loadNextRound()
    {
        if (gameStart == null)
        {
            gameStart = LocalDateTime.now();
        }
        np = model.getNp(vigie);
        canvas.setNp(np);
    }

    private void computeRound()
    {
        loadNextRound();

        var time = System.currentTimeMillis();
        var res = cockpit.nextRoundInternal(np);
        try
        {
            System.out.println(OBJECT_MAPPER.writeValueAsString(res));
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        Arrays.sort(res, Comparator.comparingInt(act -> ACTIONS_ORDER.indexOf(act.getClass())));
        time -= System.currentTimeMillis();
        System.out.println("Next round took " + -time + "ms");
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
                        System.err.println("INVALID ENTITY TYPE FOR " + act + ", EXPECTED " + entType + " GOT " + ent.getClass());
                        continue;
                    }

                    if (usedEntities.contains(ent))
                    {
                        System.err.println("ENTITY ALREADY USED FOR ACTION " + act);
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
                    System.err.println("ENTITY MISSING FOR ACTION " + act);
                }
            }
            else
            {
                if (act instanceof MoveAction)
                {
                    var mv = (MoveAction) act;
                    sail.moveRelative(mv.getDelta());
                    if (sail.getX() < 0 || sail.getX() >= model.getShip().getDeck().getLength()
                        || sail.getY() < 0 || sail.getY() >= model.getShip().getDeck().getWidth())
                    {
                        System.err.println("SAILOR " + sail.getId() + " MOVED OUTSIDE THE DECK");
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
}
