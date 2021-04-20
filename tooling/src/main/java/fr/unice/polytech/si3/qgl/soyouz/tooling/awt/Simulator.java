package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Simulator extends JFrame
{

    private static final String[] SPEEDS = { "Slow", "Medium", "Fast" };
    private static final int[] DELAYS = { 50, 10, 0 };
    final SimulatorModel smodel = new SimulatorModel();
    public final Timer timer;
    private final SimulatorCanvas canvas;
    private final JButton btnNext;
    private final JButton btnPlay;

    public Simulator() throws IOException
    {
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(900, 600);

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

        var btnResetShuffle = new JButton("Reset & shuffle");
        topcont.add(btnResetShuffle);
        btnResetShuffle.addActionListener(e ->
        {
            smodel.reset(true);
        });

        canvas = new SimulatorCanvas(smodel, this);
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

        JComboBox<Object> cbxFiles = new JComboBox<>(Files.walk(Paths.get("games"))
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(name -> name.contains("Week") && !name.contains("_next"))
            .sorted().toArray());
        cbxFiles.addItemListener(e -> loadFile(e.getItem().toString()));
        topcont.add(cbxFiles);

        smodel.listener = new SimulatorListener()
        {
            @Override
            public void npChanged()
            {
                canvas.centerView(false);
            }

            @Override
            public void fileLoaded()
            {
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
        smodel.loadFile(fn, false);
        canvas.centerView(true);
    }
}
