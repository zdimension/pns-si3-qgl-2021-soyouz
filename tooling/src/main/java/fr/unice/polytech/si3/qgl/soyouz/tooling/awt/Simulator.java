package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Simulator extends JFrame
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Timer timer;
    private final int COMP_STEPS = 10;
    private int currentStep = 0;
    private double rotIncrement;
    private double spdIncrement;
    private NextRoundParameters np;
    private final SimulatorCanvas canvas;
    private RunnerParameters model;
    private final ArrayList<OnboardEntity> usedEntities;
    private Cockpit cockpit;
    private final JButton btnNext;
    private final JButton btnSlowNext;
    private final JButton btnPlay;

    private void reset() throws IOException
    {
        timer.stop();
        btnNext.setEnabled(true);
        btnSlowNext.setEnabled(true);
        btnPlay.setText("Play");
        var ipt = Files.readString(Path.of("games/Week8p2.json")).replace("\"ship\": {", "\"ship\": {\"type\":\"ship\",");
        model = OBJECT_MAPPER.readValue(ipt, RunnerParameters.class);
        np = null;
        canvas.setModel(model.getIp());
        cockpit = new Cockpit();
        cockpit.initGameInternal(model.getIp());
        usedEntities.clear();
        canvas.reset();
        loadNextRound();
    }

    public Simulator() throws IOException
    {
        System.setProperty("sun.awt.noerasebackground", "true");
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(600, 600);

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var topcont = new Panel();
        topcont.setLayout(new BoxLayout(topcont, BoxLayout.X_AXIS));

        btnNext = new JButton("Next");
        topcont.add(btnNext);

        btnSlowNext = new JButton("Next slow");
        topcont.add(btnSlowNext);

        btnPlay = new JButton("Play");
        topcont.add(btnPlay);

        var btnReset = new JButton("Reset");
        topcont.add(btnReset);
        btnReset.addActionListener(e ->
        {
            try
            {
                reset();
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
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

        timer = new Timer(5, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                var linSpeed = spdIncrement;
                if (np.getWind() != null)
                {
                    var sails = Util.filterType(Arrays.stream(np.getShip().getEntities()), Voile.class);
                    var counts = new Object()
                    {
                        int open;
                        int total;
                    };
                    sails.forEach(voile ->
                    {
                        if (voile.isOpenned())
                            counts.open++;
                        counts.total++;
                    });
                    Wind wind = np.getWind();
                    linSpeed += ((double) counts.open / counts.total) * wind.getStrength() * Math.cos(wind.getOrientation() - np.getShip().getPosition().getOrientation()) / COMP_STEPS;
                }

                var cur = model.getShip().getPosition();
                var linear = Position.fromPolar(linSpeed, cur.getOrientation());

                for (Entity visibleEntity : np.getVisibleEntities())
                {
                    if (visibleEntity instanceof Stream)
                    {
                        var str = (Stream)visibleEntity;
                        if (str.contains(model.getShip().getPosition()))
                        {
                            linear = linear.add(str.getProjectedStrength().mul(1d / COMP_STEPS));
                        }
                    }
                }

                model.getShip().setPosition(cur.add(linear).add(0, 0, rotIncrement));
                System.out.println("Ship position : " + model.getShip().getPosition());
                if (++currentStep >= COMP_STEPS)
                {
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
                    usedEntities.clear();
                }
                canvas.repaint();
            }
        });

        reset();

        btnNext.addActionListener(event ->
        {
            playMode = false;
            timer.setDelay(5);
            playRound();
        });

        btnSlowNext.addActionListener(event ->
        {
            playMode = false;
            timer.setDelay(50);
            playRound();
        });

        btnPlay.addActionListener(event ->
        {
            if (timer.isRunning())
            {
                playMode = false;
                btnPlay.setText("Play");
            }
            else
            {
                btnPlay.setText("Stop");
                playMode = true;
                timer.setDelay(5);
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
        });
    }

    private boolean playMode = false;

    private void playRound()
    {
        btnNext.setEnabled(false);

        computeRound();

        currentStep = 0;
        timer.start();
    }

    private void loadNextRound()
    {
        if (np == null)
        {
            np = model.getNp();
            canvas.setNp(np);
        }
    }

    private void computeRound()
    {
        loadNextRound();

        var res = cockpit.nextRoundInternal(np);
        var activeOars = new ArrayList<Rame>();
        var rudderRotate = 0d;
        for (GameAction act : res)
        {
            var sail = model.getSailors()[act.getSailorId()];
            var entType = act.entityNeeded;
            if (entType != null)
            {
                var entOpt = model.getShip().getEntityHere(act.getSailor().getGridPosition());
                if (entOpt.isPresent())
                {
                    var ent = entOpt.get();

                    if (!(entType.isInstance(ent)))
                    {
                        return;
                    }

                    if (usedEntities.contains(ent))
                    {
                        System.err.println("ENTITY ALREADY USED FOR ACTION " + act);
                        return;
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
                        ((Voile)ent).setOpenned(true);
                    }
                    else if (act instanceof LowerSailAction)
                    {
                        ((Voile)ent).setOpenned(false);
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
                    sail.moveRelative(mv.getXDistance(), mv.getYDistance());
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
        var sailSpeed = np.getWind().getStrength()
            * Math.cos(np.getWind().getOrientation() - model.getShip().getPosition().getOrientation())
            * Util.filterType(Arrays.stream(model.getShip().getEntities()),
            Voile.class).filter(Voile::isOpenned).count()
            / model.getShip().getNumberSail();
        var dirSpeed = oarFactor + sailSpeed;
        var activeOarsLeft = activeOars.stream().filter(o -> o.getY() == 0).count();
        var activeOarsRight = activeOars.size() - activeOarsLeft;
        var oarRot = (activeOarsRight - activeOarsLeft) * Math.PI / noars;
        var totalRot = oarRot + rudderRotate;
        rotIncrement = totalRot / COMP_STEPS;
        spdIncrement = dirSpeed / COMP_STEPS;
    }
}
