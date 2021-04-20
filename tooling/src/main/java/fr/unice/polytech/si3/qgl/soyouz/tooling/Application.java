package fr.unice.polytech.si3.qgl.soyouz.tooling;

import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.SimulatorModel;

import java.io.IOException;

public class Application
{

    public static void main(String[] args) throws IOException
    {
        if (args.length == 1)
        {
            // mode benchmark
            var model = new SimulatorModel();
            model.loadFile(args[0], false);
            model.runBenchmark();
        }
        else
        {
            new Simulator().setVisible(true);
        }
    }
}
