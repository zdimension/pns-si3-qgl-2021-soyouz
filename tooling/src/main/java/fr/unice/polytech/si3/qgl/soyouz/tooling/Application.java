package fr.unice.polytech.si3.qgl.soyouz.tooling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;
import javafx.embed.swing.JFXPanel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
                try (var fw = new FileWriter("bench.csv", true);
                     var bw = new BufferedWriter(fw);
                     var writer = new PrintWriter(bw))
                {
                    Util.updateLogLevel(Level.OFF);
                    for (String week : getWeeks())
                    {
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
            new JFXPanel(); // required to initialize JavaFX
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
