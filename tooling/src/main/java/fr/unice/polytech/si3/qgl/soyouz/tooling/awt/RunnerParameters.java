package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RunnerParameters
{
    private static final Random RNG = new Random();
    private GameGoal goal;
    private Bateau ship;
    private Wind wind;
    private int minumumCrewSize;
    private int maximumCrewSize;
    private Position[] startingPositions;
    private ShapedEntity[] seaEntities;
    private Marin[] sailors;
    private int maxRound;
    private InitGameParameters ip;

    public Bateau getShip()
    {
        return ship;
    }

    public GameGoal getGoal()
    {
        return goal;
    }

    public Wind getWind()
    {
        return wind;
    }

    public int getMinumumCrewSize()
    {
        return minumumCrewSize;
    }

    public int getMaximumCrewSize()
    {
        return maximumCrewSize;
    }

    public Position[] getStartingPositions()
    {
        return startingPositions;
    }

    public ShapedEntity[] getSeaEntities()
    {
        return seaEntities;
    }

    public int getMaxRound()
    {
        return maxRound;
    }

    public RunnerParameters()
    {

    }

    public RunnerParameters(InitGameParameters pars, NextRoundParameters nps)
    {
        ip = pars;
        sailors = Arrays.stream(pars.getSailors()).map(o -> new Marin(o.getId(), o.getX(), o.getY(), o.getName())).toArray(Marin[]::new);
        goal = pars.getGoal();
        ship = pars.getShip();
        seaEntities = nps.getVisibleEntities();
        wind = nps.getWind();
    }

    @JsonIgnore
    public Marin[] getSailors()
    {
        if (sailors == null)
        {
            var sailCount = RNG.nextInt(maximumCrewSize - minumumCrewSize + 1) + minumumCrewSize;

            var sails = new HashMap<PosOnShip, Marin>();
            for (int i = 0; i < sailCount; i++)
            {
                PosOnShip pos;
                do
                {
                    pos = new PosOnShip(
                        RNG.nextInt(ship.getDeck().getLength()),
                        RNG.nextInt(ship.getDeck().getWidth())
                    );
                }
                while (sails.containsKey(pos));
                sails.put(pos, new Marin(i, pos.getX(), pos.getY(), "Marin" + i));
            }

            sailors = sails.values().toArray(new Marin[0]);
        }

        return this.sailors;
    }

    @JsonIgnore
    public InitGameParameters getIp()
    {
        if (ip == null)
        {
            ship.setPosition(startingPositions[0]); // TODO
            ship.setShape(new Rectangle(ship.getDeck().getWidth(), ship.getDeck().getLength(), ship.getPosition().getOrientation()));

            ip = new InitGameParameters(
                goal,
                ship,
                Arrays.stream(getSailors()).map(o -> new Marin(o.getId(), o.getX(), o.getY(), o.getName())).toArray(Marin[]::new)
            );
        }

        return ip;
    }

    @JsonIgnore
    public NextRoundParameters getNp(boolean vigie)
    {
        return new NextRoundParameters(
            ship,
            wind,
            Arrays.stream(seaEntities).filter(p -> p.getShell(ship.getPosition(), 0).anyMatch(
                pt -> pt.sub(ship.getPosition()).norm() < (vigie ? 5000 : 1000)
            )).toArray(ShapedEntity[]::new)
        );
    }
}
