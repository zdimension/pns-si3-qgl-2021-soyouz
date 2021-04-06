package fr.unice.polytech.si3.qgl.soyouz.tooling;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application
{

    public static void main(String[] args) throws IOException
    {
        var mapper = new ObjectMapper();
        mapper.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);
        var test = mapper.readValue(Files.readString(Path.of("games/Week8p1_real.json")), InitGameParameters.class);

        new Simulator().setVisible(true);
    }
}
