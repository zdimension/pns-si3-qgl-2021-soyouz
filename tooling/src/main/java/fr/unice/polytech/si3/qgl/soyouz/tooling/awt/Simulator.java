package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.IntNode;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

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
import java.util.stream.Collectors;

public class Simulator extends JFrame
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Timer timer;

    public Simulator() throws IOException
    {
        System.setProperty("sun.awt.noerasebackground", "true");
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(600, 600);

        var model = OBJECT_MAPPER.readValue(Files.readString(Path.of("initGameLong.json")), InitGameParameters.class);
        var cockpit = new Cockpit();
        cockpit.initGame(OBJECT_MAPPER.writeValueAsString(model));

        OBJECT_MAPPER.registerModule(new SimpleModule() {{
            addDeserializer(Marin.class, new JsonDeserializer<Marin>()
            {
                @Override
                public Marin deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
                {
                    return model.getSailorById(jsonParser.getValueAsInt()).orElse(null);
                }
            });
            addDeserializer(OarAction.class, new JsonDeserializer<OarAction>()
            {
                @Override
                public OarAction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
                {
                    return new OarAction(model.getSailorById(((IntNode)OBJECT_MAPPER.readTree(jsonParser).get("sailorId")).asInt()).get());
                }
            });
        }});

        var btnNext = new JButton("Next");
        add(btnNext, BorderLayout.NORTH);

        var usedEntities = new ArrayList<OnboardEntity>();

        var canvas = new SimulatorCanvas(model, usedEntities);
        add(canvas, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });

        timer = new Timer(5, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                model.getShip().setPosition(model.getShip().getPosition().add(increment));
                if (++currentStep >= COMP_STEPS)
                {
                    timer.stop();
                    btnNext.setEnabled(true);
                    usedEntities.clear();
                }
                canvas.repaint();
            }
        });
        btnNext.addActionListener(event ->
        {
            btnNext.setEnabled(false);
            var np = new NextRoundParameters(model.getShip(), null, new Entity[0]); //TODO J'ai mis null a la place du vent
            try
            {
                var res = OBJECT_MAPPER.readValue(cockpit.nextRound(OBJECT_MAPPER.writeValueAsString(np)), GameAction[].class);
                var activeOars = new ArrayList<Rame>();
                for (GameAction act : res)
                {
                    var entType = act.entityNeeded;
                    entType.ifPresentOrElse(type ->
                    {
                        model.getShip().getEntityHere(act.getSailor().getGridPosition()).ifPresentOrElse(ent ->
                        {
                            if (!(type.isInstance(ent)))
                                return;

                            if (usedEntities.contains(ent))
                            {
                                System.err.println("ENTITY ALREADY USED FOR ACTION " + act);
                                return;
                            }

                            usedEntities.add(ent);

                            if (act instanceof OarAction)
                            {
                                activeOars.add((Rame)ent);
                            }
                        }, () ->
                        {
                            System.err.println("ENTITY MISSING FOR ACTION " + act);
                        });
                    }, () ->
                    {

                    });
                }
                var noars = model.getShip().getNumberOar();
                var oarFactor = 165.0 * activeOars.size() / noars;
                var windSpeed = 0;
                var dirSpeed = oarFactor + windSpeed;
                var vx = dirSpeed * Math.cos(model.getShip().getPosition().getOrientation());
                var vy = dirSpeed * Math.sin(model.getShip().getPosition().getOrientation());
                var activeOarsLeft = activeOars.stream().filter(o -> o.getY() == 0).count();
                var activeOarsRight = activeOars.size() - activeOarsLeft;
                var oarRot = (activeOarsRight - activeOarsLeft) * Math.PI / noars;
                System.out.println("Moving ship by " + vx + ";" + vy + " m/s, " + oarRot + " rad/s");
                this.increment = new Position(vx / COMP_STEPS, vy / COMP_STEPS, oarRot / COMP_STEPS);
                currentStep = 0;
                timer.start();
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
        });
    }

    private final int COMP_STEPS = 30;
    private int currentStep = 0;
    private Position increment;
}
