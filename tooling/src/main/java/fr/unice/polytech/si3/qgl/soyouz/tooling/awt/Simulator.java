package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

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
import java.util.concurrent.atomic.AtomicReference;

public class Simulator extends JFrame
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Timer timer;
    private final int COMP_STEPS = 10;
    private int currentStep = 0;
    private double rotIncrement;
    private double spdIncrement;
    private NextRoundParameters np;

    public Simulator() throws IOException
    {
        System.setProperty("sun.awt.noerasebackground", "true");
        setTitle("Soyouz Simulator");
        setLayout(new BorderLayout());
        setSize(600, 600);

        var model = OBJECT_MAPPER.readValue(Files.readString(Path.of("Week5.json")),
            InitGameParameters.class);
        var cockpit = new Cockpit();
        cockpit.initGame(OBJECT_MAPPER.writeValueAsString(model));

        OBJECT_MAPPER.registerModule(new SimpleModule()
        {{
            addDeserializer(Marin.class, new JsonDeserializer<>()
            {
                @Override
                public Marin deserialize(JsonParser jsonParser,
                                         DeserializationContext deserializationContext) throws IOException
                {
                    return model.getSailorById(jsonParser.getValueAsInt()).orElse(null);
                }
            });
            addDeserializer(OarAction.class, new JsonDeserializer<>()
            {
                @Override
                public OarAction deserialize(JsonParser jsonParser,
                                             DeserializationContext deserializationContext) throws IOException
                {
                    return new OarAction(model.getSailorById(((IntNode) OBJECT_MAPPER.readTree(jsonParser).get("sailorId")).asInt()).get());
                }
            });
            addDeserializer(MoveAction.class, new JsonDeserializer<>()
            {
                @Override
                public MoveAction deserialize(JsonParser jsonParser,
                                              DeserializationContext deserializationContext) throws IOException
                {
                    var tree = OBJECT_MAPPER.readTree(jsonParser);
                    return new MoveAction(model.getSailorById(((IntNode) tree.get("sailorId")).asInt()).get(),
                        ((IntNode) tree.get("xdistance")).asInt(),
                        ((IntNode) tree.get("ydistance")).asInt()
                    );
                }
            });
            addDeserializer(TurnAction.class, new JsonDeserializer<>()
            {
                @Override
                public TurnAction deserialize(JsonParser jsonParser,
                                              DeserializationContext deserializationContext) throws IOException
                {
                    var tree = OBJECT_MAPPER.readTree(jsonParser);
                    return new TurnAction(model.getSailorById(((IntNode) tree.get("sailorId")).asInt()).get(),
                        ((DoubleNode) tree.get("rotation")).asDouble()
                    );
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
                System.out.println(cockpit.getLogs());
                dispose();
            }
        });

        timer = new Timer(5, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                var linSpeed = spdIncrement;
                if (np.getWind() != null)
                {
                    var sails = Util.filterType(Arrays.stream(np.getShip().getEntities()), Voile.class);
                    var counts = new Object()
                    {
                        int open;
                        int total;
                    };
                    sails.forEach(voile ->
                    {
                        if (voile.isOpenned())
                            counts.open++;
                        counts.total++;
                    });
                    linSpeed += np.getWind().windAdditionnalSpeed(counts.total, counts.open, np.getShip()) / COMP_STEPS;
                }

                var cur = model.getShip().getPosition();
                model.getShip().setPosition(cur.add(new Position(
                    linSpeed * Math.cos(cur.getOrientation()),
                    linSpeed * Math.sin(cur.getOrientation()),
                    rotIncrement)));
                System.out.println("Ship position : " + model.getShip().getPosition());
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
            //TODO J'ai
            np = new NextRoundParameters(model.getShip(), null, new Entity[0]);
            // mis null a la place du vent
            try
            {
                var res =
                    OBJECT_MAPPER.readValue(cockpit.nextRound(OBJECT_MAPPER.writeValueAsString(np)), GameAction[].class);
                var activeOars = new ArrayList<Rame>();
                var rudderRotate = 0d;
                for (GameAction act : res)
                {
                    var entType = act.entityNeeded;
                    if (entType != null)
                    {
                        var entOpt = model.getShip().getEntityHere(act.getSailor().getGridPosition());
                        if (entOpt.isPresent())
                        {
                            var ent = entOpt.get();

                            if (!(entType.isInstance(ent)))
                            {
                                return;
                            }

                            if (usedEntities.contains(ent))
                            {
                                System.err.println("ENTITY ALREADY USED FOR ACTION " + act);
                                return;
                            }

                            //System.out.println("Entity " + ent + " used by sailor " + act
                            // .getSailorId());

                            usedEntities.add(ent);

                            if (act instanceof OarAction)
                            {
                                activeOars.add((Rame) ent);
                            }
                            if (act instanceof TurnAction)
                            {
                                rudderRotate = ((TurnAction) act).getRotation();
                            }
                        }
                        else
                        {
                            System.err.println("ENTITY MISSING FOR ACTION " + act);
                        }
                    }
                    else
                    {
                        if (act instanceof MoveAction)
                        {
                            var mv = (MoveAction) act;
                            mv.getSailor().moveRelative(mv.getXDistance(), mv.getYDistance());
                        }
                    }
                }
                var noars = model.getShip().getNumberOar();
                var oarFactor = 165.0 * activeOars.size() / noars;
                var windSpeed = 0;
                var dirSpeed = oarFactor + windSpeed;
                var activeOarsLeft = activeOars.stream().filter(o -> o.getY() == 0).count();
                var activeOarsRight = activeOars.size() - activeOarsLeft;
                var oarRot = (activeOarsRight - activeOarsLeft) * Math.PI / noars;
                var totalRot = oarRot + rudderRotate;
                rotIncrement = totalRot / COMP_STEPS;
                spdIncrement = dirSpeed / COMP_STEPS;
                currentStep = 0;
                timer.start();
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
        });
    }
}
