package fr.unice.polytech.si3.qgl.soyouz.tooling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException
    {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);

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
                    model.loadFile(week, false);
                    System.out.println("TESTING " + week + " = " + model.runBenchmark(1));
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
            .filter(name -> name.contains("Week") && !name.contains("_next") && (!name.contains(
                "_multi_") || name.contains("_1_")))
            .sorted().toArray(String[]::new);
    }
}
