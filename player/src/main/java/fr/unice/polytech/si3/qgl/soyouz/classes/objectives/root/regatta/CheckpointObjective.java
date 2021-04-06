package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;
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
        update(state);

        if (roundObjective == null || roundObjective.isValidated())
        {
            roundObjective = new SailorObjective(onBoardDataHelper, seaDataHelper, distanceToCp,
                angleToCp);
        }

        return roundObjective.resolve();
    }

    private final List<Point2d> nodes = new ArrayList<>();

    private void traverseNode(Reef[] arr, int elem, Set<Pair<Integer, Integer>> lines, double shipSize)
    {
        var node = nodes.get(elem);
        outer:
        for (int i = 0; i < nodes.size(); i++)
        {
            Point2d p = nodes.get(i);
            if (p == node)
            {
                continue;
            }

            for (Reef reef : arr)
            {
                if (reef.getShape().linePassesThrough(reef.toLocal(node), reef.toLocal(p), shipSize))
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
    @Override
    public void update(GameState state)
    {
        Bateau boat = state.getNp().getShip();

        nodes.clear();
        nodes.add(boat.getPosition());
        nodes.add(cp.getPosition());

        var reef = state.getNp().getReef().toArray(Reef[]::new);

        var diam = boat.getShape().getMaxDiameter();
        for (Reef r : reef)
        {
            r.getShell(boat.getPosition(), diam).forEach(nodes::add);
        }

        var lines = new HashSet<Pair<Integer, Integer>>();
        traverseNode(reef, 0, lines, diam);

        var gnodes = new ArrayList<Node>();
        for (Point2d node : nodes)
        {
            gnodes.add(new Node(node));
        }

        for (Pair<Integer, Integer> line : lines)
        {
            gnodes.get(line.first).addNeighbour(gnodes.get(line.second));
        }

        var graph = new Graph(gnodes, 0, 1);
        var path = graph.getShortestPath();
        if (path.size() < 2)
        {
            logger.severe("WTF CHEMIN PAS FINI");
        }
        var point = path.get(1).position.sub(boat.getPosition());
        angleToCp = point.angle() - boat.getPosition().getOrientation();
        distanceToCp = point.norm();

        logger.info(String.format("New target: D=%.2f, ANGLE=%.2f°", distanceToCp,
            Math.toDegrees(angleToCp)));


        /*angleToCp = calculateAngleBetweenBoatAndCheckpoint(state.getNp().getShip());
        distanceToCp = boat.getPosition().getLength(cp.getPosition());
        distanceToCp += ((Circle) cp.getShape()).getRadius();*/
    }
}