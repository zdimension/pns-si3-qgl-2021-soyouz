package fr.unice.polytech.si3.qgl.soyouz.tooling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

public class Application
{

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static void init()
    { OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);

        Util.configureLoggerFormat();

    }

    public static void main(String[] args) throws IOException
    {
        init();

        if (args.length == 1)
        {
            // mode benchmark
            var model = new SimulatorModel();
            if (args[0].equals("test"))
            {
                try (var fw = new FileWriter("bench.csv", true);
                     var bw = new BufferedWriter(fw);
                     var writer = new PrintWriter(bw))
                {
                    Util.updateLogLevel(Level.OFF);
                    for (String week : getWeeks())
                    {
                        if (week.contains("real") && !week.contains("2019"))
                        {
                            continue;
                        }
                        model.loadFile(week, false);
                        var bench = model.runBenchmark(1);
                        System.out.println("TESTING " + week + " = " + bench);
                        writer.append(String.valueOf(bench.toMillis() / 1000.0)).append(",");
                    }
                    writer.append('\n');
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
            if (args.length == 3 && args[0].equals("-cockpit"))
            {
                URLClassLoader child = new URLClassLoader(
                    new URL[] {new File(args[2]).toURI().toURL()},
                    ClassLoader.getSystemClassLoader()
                );
                try
                {
                    SimulatorModel.cockpitClass = child.loadClass("fr.unice.polytech.si3.qgl." + args[1] + ".Cockpit");
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            runSimulator();
        }
    }

    public static void runSimulator()
    {
        init();

        new JFXPanel(); // required to initialize JavaFX
        Platform.runLater(() ->
        {
            try
            {
                new Simulator().setVisible(true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    public static <T extends ICockpit> void runSimulator(Class<T> cockpit)
    {
        SimulatorModel.cockpitClass = cockpit;
        runSimulator();
    }

    public static String[] getWeeks() throws IOException
    {
        return Files.walk(Paths.get("games"))
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(name -> name.contains("Week") && !name.contains("_next") && (!name.contains(
                "_multi_") || name.contains("_1_")))
            .sorted().toArray(String[]::new);
    }
}
