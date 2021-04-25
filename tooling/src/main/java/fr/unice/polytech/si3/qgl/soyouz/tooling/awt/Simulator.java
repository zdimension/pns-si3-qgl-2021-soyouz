package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.tooling.Application;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorListener;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Simulator extends JFrame
{

    private static final String[] SPEEDS = { "Slow", "Medium", "Fast" };
    private static final int[] DELAYS = { 50, 10, 1 };
    public final Timer timer;
    final SimulatorModel smodel = new SimulatorModel();
    private final JButton btnNext;
    private final JButton btnPlay;
    private boolean threeD;
    private SimulatorView canvas;

    public Simulator() throws IOException
    {
        this.threeD = false;
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(900, 600);

        var topcont = new Panel();
        topcont.setLayout(new BoxLayout(topcont, BoxLayout.X_AXIS));

        var topcont2 = new Panel();
        topcont2.setLayout(new BoxLayout(topcont2, BoxLayout.X_AXIS));

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

        canvas = threeD
            ? new SimulatorCanvas3D(smodel)
            : new SimulatorCanvas(smodel, this);
        add((JComponent) canvas, BorderLayout.CENTER);

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
            smodel.runBenchmark(5);
        });

        var cbxPath = new JCheckBox("Show graph", true);
        cbxPath.addChangeListener(e ->
        {
            canvas.setDrawPath(cbxPath.isSelected());
        });
        topcont2.add(cbxPath);

        var cbxNodes = new JCheckBox("Show nodes", true);
        cbxNodes.addChangeListener(e ->
        {
            canvas.setDrawNodes(cbxNodes.isSelected());
        });
        topcont2.add(cbxNodes);

        var cbxDebugColl = new JCheckBox("Debug collisions");
        cbxDebugColl.addChangeListener(e ->
        {
            canvas.setDebugCollisions(cbxDebugColl.isSelected());
        });
        topcont2.add(cbxDebugColl);

        var top = new Panel(new BorderLayout());
        top.add(topcont, BorderLayout.NORTH);
        top.add(topcont2, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });

        timer = new Timer(1, e1 -> smodel.processRound());

        var btnCenter = new JButton("Center view");
        topcont.add(btnCenter);
        btnCenter.addActionListener(e ->
        {
            canvas.centerView(true);
        });

        var btnThree = new JButton("3D Mode");
        topcont.add(btnThree);
        btnThree.addActionListener(e ->
        {
            var old = canvas;
            this.threeD = !this.threeD;
            var nc = this.threeD
                ? new SimulatorCanvas3D(smodel)
                : new SimulatorCanvas(smodel, this);
            nc.clearHistory();
            add(nc);
            canvas = nc;
            remove((JComponent) old);
            nc.centerView(true);
            nc.update();
        });

        var cbxSpeed = new JComboBox<>(SPEEDS);
        cbxSpeed.addItemListener(e ->
        {
            smodel.speed = cbxSpeed.getSelectedIndex();
            timer.setDelay(DELAYS[smodel.speed]);
        });
        cbxSpeed.setSelectedIndex(2);
        topcont2.add(cbxSpeed);

        JComboBox<Object> cbxFiles = new JComboBox<>(Application.getWeeks());
        cbxFiles.addItemListener(e -> loadFile(e.getItem().toString()));
        topcont2.add(cbxFiles);

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
                canvas.update();
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
