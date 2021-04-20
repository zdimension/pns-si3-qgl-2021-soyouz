package fr.unice.polytech.si3.qgl.soyouz.tooling;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.SimulatorModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Application
{

    public static void main(String[] args) throws IOException
    {
        Util.configureLoggerFormat();

        if (args.length == 1)
        {
            // mode benchmark
            var model = new SimulatorModel();
            if (args[0].equals("test"))
            {
                Util.updateLogLevel(Level.OFF);
                for (String week : getWeeks())
                {
                    System.out.println("TESTING " + week);
                    model.loadFile(week, false);
                    model.runBenchmark(1);
                }
            }
            else
            {
                model.loadFile(args[0], false);
                model.runBenchmark(15);
            }
        }
        else
        {
            new Simulator().setVisible(true);
        }
    }

    public static String[] getWeeks() throws IOException
    {
        return Files.walk(Paths.get("games"))
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(name -> name.contains("Week") && !name.contains("_next"))
            .sorted().toArray(String[]::new);
    }
}
