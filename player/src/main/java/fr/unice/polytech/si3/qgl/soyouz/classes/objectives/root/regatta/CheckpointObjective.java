package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Collidable;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.SailorObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Graph;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Checkpoint type of objective
 */
public class CheckpointObjective implements RootObjective
{
    private static final Logger logger =
        Logger.getLogger(CheckpointObjective.class.getSimpleName());
    public final List<Point2d> nodes = new ArrayList<>();
    private final Checkpoint cp;
    private final OnBoardDataHelper onBoardDataHelper;
    private final SeaDataHelper seaDataHelper;
    public List<Node> path;
    public Graph graph;
    private SailorObjective roundObjective;
    private double angleToCp;
    private double distanceToCp;

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
        return state.getNp().getShip().getPosition().distance(cp.getPosition())
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

    static class LinkedNode
    {
        final int value;
        LinkedNode next;

        public LinkedNode(int value)
        {
            this.value = value;
        }
    }

    private void traverseNode(ShapedEntity[] arr, List<Node> lines, double shipSize, //NOSONAR
                              Position shipPosition)
    {
        var stack = new LinkedNode(0); // premier nœud = position du bateau
        var last = stack;
        while (stack != null)
        {
            var elem = stack.value;
            var node = nodes.get(elem);
            outer: //NOSONAR
            for (int i = 0; i < nodes.size(); i++)
            {
                if (i == elem)
                {
                    continue;
                }
                Point2d p = nodes.get(i);

                for (ShapedEntity reef : arr)
                {
                    if (reef.contains(shipPosition))
                    {
                        continue;
                    }

                    if (reef.getShape().linePassesThrough(reef.toLocal(node), reef.toLocal(p),
                        shipSize))
                    {
                        if (reef instanceof Stream)
                        {
                            var stream = (Stream) reef;
                            var proj = p.sub(node).dot(stream.getProjectedStrength());
                            if (proj < 0)
                            {
                                continue outer;
                            }
                        }

                        if (reef instanceof Collidable)
                        {
                            continue outer;
                        }
                    }
                }

                if (lines.get(elem).addNeighbour(lines.get(i)))
                {
                    last = (last.next = new LinkedNode(i));
                }
            }
            stack = stack.next;
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

            var diam = 40;
            logger.info("Computing shells");
            for (ShapedEntity r : reef)
            {
                r.getShell().forEach(nodes::add);
            }

            logger.info(nodes.size() + " nodes; start traverse");
            try
            {
                logger.info(Cockpit.OBJECT_MAPPER.writeValueAsString(nodes));
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
            }
            var gnodes = new ArrayList<Node>();
            for (int i = 0; i < nodes.size(); i++)
            {
                Point2d node = nodes.get(i);
                gnodes.add(new Node(node, i));
            }

            traverseNode(state.getNp().getVisibleEntities(), gnodes, diam,
                onBoardDataHelper.getShip().getPosition());

            logger.info("Computing graph");
            graph = new Graph(gnodes, 0, 1);
            logger.info("Fetching shortest path");
            path = graph.getShortestPath();
            if (path.size() < 2)
            {
                logger.severe("Empty path, keeping old target");
                return;
            }
        }

        var point =
            path.get(1).position.sub(boat.getPosition()).rotate(-boat.getPosition().getOrientation());
        angleToCp = point.angle();
        while (angleToCp > Math.PI)
        {
            angleToCp -= Math.PI;
        }
        while (angleToCp < -Math.PI)
        {
            angleToCp += Math.PI;
        }

        distanceToCp = point.norm();

        logger.info(String.format("New target: D=%.2f, ANGLE=%.2f°", distanceToCp,
            Math.toDegrees(angleToCp)));
    }
}