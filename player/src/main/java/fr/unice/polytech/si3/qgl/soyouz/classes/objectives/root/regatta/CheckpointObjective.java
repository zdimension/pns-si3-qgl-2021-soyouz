package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.SailorObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Graph;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Node;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Checkpoint type of objective
 */
public class CheckpointObjective implements RootObjective
{
    private static final Logger logger = Logger.getLogger(CheckpointObjective.class.getSimpleName());

    private final Checkpoint cp;
    private final OnBoardDataHelper onBoardDataHelper;
    private final SeaDataHelper seaDataHelper;
    private SailorObjective roundObjective;
    private double angleToCp;
    private double distanceToCp;
    public static HashSet<Pair<Integer, Integer>> lines;
    public static  List<Node> path;

    /**
     * Constructor.
     *
     * @param checkpoint The checkpoint to reach.
     */
    public CheckpointObjective(Checkpoint checkpoint, OnBoardDataHelper onBoardDataHelper,
                               SeaDataHelper seaDataHelper)
    {
        cp = checkpoint;
        this.onBoardDataHelper = onBoardDataHelper;
        this.seaDataHelper = seaDataHelper;
        angleToCp = 0;
        distanceToCp = 0;
        roundObjective = null;
    }

    /**
     * Determine if the boat is inside the Checkpoint ot no.
     *
     * @param state The state of the game.
     * @return true if the boat is in, false otherwise.
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return state.getNp().getShip().getPosition().getLength(cp.getPosition())
            <= ((Circle) cp.getShape()).getRadius();
    }

    /**
     * Resolve the current objective.
     *
     * @param state The current game state.
     * @return a list of action to get closer to the goal.
     */
    @Override
    public List<GameAction> resolve(GameState state)
    {
        trace();
        update(state);

        if (roundObjective == null || roundObjective.isValidated())
        {
            roundObjective = new SailorObjective(onBoardDataHelper, seaDataHelper, distanceToCp,
                angleToCp);
        }

        return roundObjective.resolve();
    }

    public static final List<Point2d> nodes = new ArrayList<>();

    private void traverseNode(ShapedEntity[] arr, int elem, Set<Pair<Integer, Integer>> lines, double shipSize)
    {
        var node = nodes.get(elem);
        outer:
        for (int i = 0; i < nodes.size(); i++)
        {
            if (i == elem)
            {
                continue;
            }
            Point2d p = nodes.get(i);

            var line = p.sub(node);

            for (ShapedEntity reef : arr)
            {
                if (reef.contains(onBoardDataHelper.getShip().getPosition()))
                    continue;

                if (reef instanceof Stream)
                {
                    var ps = ((Stream) reef).getProjectedStrength().dot(line);
                    int x = 123;
                }

                if (reef.getShape().linePassesThrough(reef.toLocal(node), reef.toLocal(p), shipSize)
                && (reef instanceof Reef ||
                    (reef instanceof Stream && ((Stream) reef).getProjectedStrength().dot(line) != 1234)))
                {
                    continue outer;
                }
            }

            if (lines.add(Pair.of(Math.min(elem, i), Math.max(elem, i))))
            {
                traverseNode(arr, i, lines, shipSize);
            }
        }
    }

    /**
     * Update the current checkpoint to reach.
     *
     * @param state of the game
     */
    private void update(GameState state)
    {
        trace();
        Bateau boat = state.getNp().getShip();

        if (state.isRecalculatePathfinding() || path == null)
        {
            nodes.clear();
            nodes.add(boat.getPosition());
            nodes.add(cp.getPosition());

            var reef = state.getNp().getVisibleEntities();

            var diam = boat.getShape().getMaxDiameter();
            logger.info("Computing shells");
            for (ShapedEntity r : reef)
            {
                r.getShell(boat.getPosition(), diam).forEach(nodes::add);
            }

            lines = new HashSet<>();
            logger.info(nodes.size() + " nodes; start traverse");
            try
            {
                logger.info(Cockpit.OBJECT_MAPPER.writeValueAsString(nodes));
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
            traverseNode(state.getNp().getVisibleEntities(), 0, lines, diam);

            var gnodes = new ArrayList<Node>();
            for (Point2d node : nodes)
            {
                gnodes.add(new Node(node));
            }

            for (Pair<Integer, Integer> line : lines)
            {
                gnodes.get(line.first).addNeighbour(gnodes.get(line.second));
            }

            logger.info("Computing graph");
            var graph = new Graph(gnodes, 0, 1);
            logger.info("Fetching shortest path");
            path = graph.getShortestPath();
            if (path.size() < 2)
            {
                logger.severe("Empty path, keeping old target");
                return;
            }
        }

        var point = path.get(1).position.sub(boat.getPosition()).rotate(-boat.getPosition().getOrientation());
        angleToCp = point.angle();
        while (angleToCp > Math.PI)
            angleToCp -= Math.PI;
        while (angleToCp < -Math.PI)
            angleToCp += Math.PI;

        distanceToCp = point.norm();

        logger.info(String.format("New target: D=%.2f, ANGLE=%.2f°", distanceToCp,
            Math.toDegrees(angleToCp)));


        /*angleToCp = calculateAngleBetweenBoatAndCheckpoint(state.getNp().getShip());
        distanceToCp = boat.getPosition().getLength(cp.getPosition());
        distanceToCp += ((Circle) cp.getShape()).getRadius();*/
    }
}