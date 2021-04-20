package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Simulator extends JFrame
{

    private static final String[] SPEEDS = { "Slow", "Medium", "Fast" };
    private static final int[] DELAYS = { 50, 10, 0 };

    private final Timer timer;

    private final SimulatorCanvas canvas;

    private final JButton btnNext;
    private final JButton btnPlay;
    private final JComboBox<Object> cbxFiles;
    final SimulatorModel smodel = new SimulatorModel();

    public Simulator() throws IOException
    {
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(600, 600);



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
            smodel.reset();
        });

        canvas = new SimulatorCanvas(null, smodel.usedEntities, this);
        add(canvas, BorderLayout.CENTER);

        var btnClear = new JButton("Clear path");
        topcont.add(btnClear);
        btnClear.addActionListener(e ->
        {
            canvas.clearHistory();
        });

        var btnBenchmark = new JButton("Benchmark");
        topcont.add(btnBenchmark);
        btnBenchmark.addActionListener(e ->
        {
            smodel.runBenchmark();
        });

        var cbxPath = new JCheckBox("Show graph", true);
        cbxPath.addChangeListener(e ->
        {
            canvas.drawPath = cbxPath.isSelected();
            canvas.repaint();
        });
        topcont.add(cbxPath);

        var cbxNodes = new JCheckBox("Show nodes", true);
        cbxNodes.addChangeListener(e ->
        {
            canvas.drawNodes = cbxNodes.isSelected();
            canvas.repaint();
        });
        topcont.add(cbxNodes);

        var cbxDebugColl = new JCheckBox("Debug collisions");
        cbxDebugColl.addChangeListener(e ->
        {
            canvas.debugCollisions = cbxDebugColl.isSelected();
            canvas.repaint();
        });
        topcont.add(cbxDebugColl);

        add(topcont, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println(smodel.cockpit.getLogs());
                dispose();
            }
        });

        timer = new Timer(0, smodel::processRound);

        var btnCenter = new JButton("Center view");
        topcont.add(btnCenter);
        btnCenter.addActionListener(e ->
        {
            canvas.centerView(true);
        });

        var cbxSpeed = new JComboBox<>(SPEEDS);
        cbxSpeed.addItemListener(e ->
        {
            smodel.speed = cbxSpeed.getSelectedIndex();
            timer.setDelay(DELAYS[smodel.speed]);
        });
        cbxSpeed.setSelectedIndex(2);
        topcont.add(cbxSpeed);

        cbxFiles = new JComboBox<Object>(Files.walk(Paths.get("games"))
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(name -> name.contains("Week") && !name.contains("_next"))
            .sorted().toArray());
        cbxFiles.addItemListener(e -> loadFile(e.getItem().toString()));
        topcont.add(cbxFiles);

        smodel.listener = new SimulatorListener()
        {
            @Override
            public void npChanged(NextRoundParameters np)
            {
                canvas.setNp(np);
            }

            @Override
            public void fileLoaded(InitGameParameters ip, Cockpit cockpit)
            {
                canvas.setModel(ip);
                canvas.setCockpit(cockpit);
                canvas.reset();
            }

            @Override
            public void turnEnd()
            {
                timer.stop();
                btnNext.setEnabled(true);
            }

            @Override
            public void updateRequired()
            {
                canvas.repaint();
            }

            @Override
            public void gameFinished()
            {
                btnPlay.doClick();
            }
        };

        loadFile(cbxFiles.getSelectedItem().toString());
        //smodel.reset();

        btnNext.addActionListener(event ->
        {
            smodel.playMode = false;
            playRound();
        });

        btnPlay.addActionListener(event ->
        {
            if (smodel.nextRoundTime == -1)
            {
                smodel.reset();
            }
            if (smodel.playMode)
            {
                smodel.playMode = false;
                btnPlay.setText("Play");
            }
            else
            {
                btnPlay.setText("Stop");
                smodel.playMode = true;
                playRound();
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                smodel.playMode = false;

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

    private void playRound()
    {
        btnNext.setEnabled(false);

        smodel.computeRound();

        smodel.currentStep = 0;
        timer.start();
    }

    private void loadFile(String fn)
    {
        timer.stop();
        btnNext.setEnabled(true);
        btnPlay.setText("Play");
        smodel.loadFile(fn);
        canvas.centerView(true);
    }
}
