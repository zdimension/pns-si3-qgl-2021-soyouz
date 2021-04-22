package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.tooling.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RunnerParameters
{
    private static final Random RNG = new Random();
    private final GameGoal goal;
    private final Bateau[] ships;
    private final Wind wind;
    private final int minumumCrewSize;
    private final int maximumCrewSize;
    private final ShapedEntity[] seaEntities;
    private Position[] startingPositions;
    private final Marin[][] sailors;
    private final InitGameParameters[] ip;

    @JsonCreator
    public RunnerParameters(
        @JsonProperty("goal") GameGoal goal,
        @JsonProperty("wind") Wind wind,
        @JsonProperty("minumumCrewSize") int minumumCrewSize,
        @JsonProperty("maximumCrewSize") int maximumCrewSize,
        @JsonProperty("startingPositions") Position[] startingPositions,
        @JsonProperty("seaEntities") ShapedEntity[] seaEntities,
        @JsonProperty("maxRound") int maxRound,
        @JsonProperty("ship") Bateau ship) throws JsonProcessingException
    {
        this.ships = new Bateau[startingPositions.length];
        for (int i = 0; i < this.ships.length; i++)
        {
            this.ships[i] = Application.OBJECT_MAPPER.readValue(
                Application.OBJECT_MAPPER.writeValueAsString(ship), Bateau.class
            );
        }
        this.goal = goal;
        this.wind = wind;
        this.minumumCrewSize = minumumCrewSize;
        this.maximumCrewSize = maximumCrewSize;
        this.startingPositions = startingPositions;
        this.seaEntities = seaEntities;
        this.ip = new InitGameParameters[startingPositions.length];
        this.sailors = new Marin[startingPositions.length][];
    }

    public RunnerParameters(InitGameParameters pars, NextRoundParameters nps, boolean shuffle)
    {
        this(new InitGameParameters[] { pars }, new NextRoundParameters[] { nps }, shuffle);
    }

    public RunnerParameters(InitGameParameters[] pars, NextRoundParameters[] nps, boolean shuffle)
    {
        maximumCrewSize = minumumCrewSize = pars[0].getSailors().length;
        goal = pars[0].getGoal();
        ships = Arrays.stream(pars).map(InitGameParameters::getShip).toArray(Bateau[]::new);
        if (shuffle)
        {
            startingPositions = Arrays.stream(pars)
                .map(InitGameParameters::getShip)
                .map(ShapedEntity::getPosition).toArray(Position[]::new);
            ip = new InitGameParameters[pars.length];
            sailors = new Marin[pars.length][];
            for (int i = 0; i < pars.length; i++)
            {
                getIp(i, false);
            }
        }
        else
        {
            ip = pars;
            sailors =
                Arrays.stream(pars).map(InitGameParameters::getSailors).toArray(Marin[][]::new);
        }
        seaEntities = nps[0].getVisibleEntities();
        wind = nps[0].getWind();
    }

    public int getShipCount()
    {
        return ships.length;
    }

    public Bateau getShip(int id)
    {
        return ships[id];
    }

    public GameGoal getGoal()
    {
        return goal;
    }

    @JsonIgnore
    public Marin[] getSailors(int id)
    {
        if (sailors[id] == null)
        {
            var sailCount = RNG.nextInt(maximumCrewSize - minumumCrewSize + 1) + minumumCrewSize;

            var res = new Marin[sailCount];
            var sails = new HashSet<PosOnShip>();
            for (int i = 0; i < sailCount; i++)
            {
                PosOnShip pos;
                do
                {
                    pos = new PosOnShip(
                        RNG.nextInt(ships[id].getDeck().getLength()),
                        RNG.nextInt(ships[id].getDeck().getWidth())
                    );
                }
                while (sails.contains(pos));
                var sailor = new Marin(i, pos.getX(), pos.getY(), "Marin" + i);
                sails.add(pos);
                res[i] = sailor;
            }

            sailors[id] = res;
        }

        return this.sailors[id];
    }

    @JsonIgnore
    public InitGameParameters getIp(int id, boolean cloneSailors)
    {
        var ship = ships[id];
        if (ip[id] == null)
        {
            ship.setPosition(startingPositions[id]); // TODO
            ship.setShape(new Rectangle(ship.getDeck().getWidth(), ship.getDeck().getLength(),
                ship.getPosition().getOrientation()));

            ip[id] = new InitGameParameters(goal, ship, getSailors(id));
        }

        if (cloneSailors)
        {
            return new InitGameParameters(
                goal,
                ship,
                Arrays.stream(ip[id].getSailors()).map(o -> new Marin(o.getId(), o.getX(), o.getY(),
                    o.getName())).toArray(Marin[]::new)
            );
        }
        else
        {
            return ip[id];
        }
    }

    @JsonIgnore
    public NextRoundParameters getNp(int id, boolean vigie)
    {
        var otherShips = IntStream.concat(
            IntStream.range(0, id),
            IntStream.range(id + 1, getShipCount())
        ).mapToObj(i -> ships[i]);
        return new NextRoundParameters(
            ships[id],
            wind,

            Stream.concat(Arrays.stream(seaEntities).filter(p -> p.getShell().anyMatch(
                pt -> pt.sub(ships[id].getPosition()).norm() < (vigie ? 5000 : 1000)
                )),
                otherShips).toArray(ShapedEntity[]::new)
        );
    }

    public Wind getWind()
    {
        return wind;
    }

    public Optional<Marin> getSailorById(int i, int id)
    {
        return ip[i].getSailorById(id);
    }

    public Bateau[] getShips()
    {
        return ships;
    }
}
