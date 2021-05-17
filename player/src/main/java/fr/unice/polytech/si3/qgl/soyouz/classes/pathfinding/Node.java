package fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Item representing a point of interest on the sea (boat, checkpoint, reef corner)
 */
public class Node implements Comparable<Node>
{
    private static final Comparator<Node> COMPARATOR =
        Comparator.comparingDouble(Node::getCost).thenComparingInt(Node::getId);
    public final Point2d position;
    private final int id;
    public double minCostToStart = Double.NaN;
    public double distanceToEnd;
    public Map<Node, Double> connections = new HashMap<>();
    public boolean visited;
    public Node nearestToStart;

    public Node(Point2d position, int id)
    {
        this.position = position;
        this.id = id;
    }

    /**
     * Adds a neighbour to the node
     *
     * @param neighbour
     * @return success state, fails if the neighbour has been added previously
     */
    public boolean addNeighbour(Node neighbour)
    {
        if (connections.containsKey(neighbour))
        {
            return false;
        }
        var distance = neighbour.position.sub(this.position).norm();
        connections.put(neighbour, distance);
        return true;
    }

    private double getCost()
    {
        return minCostToStart + distanceToEnd;
    }

    private int getId()
    {
        return id;
    }

    @Override
    public int compareTo(Node o)
    {
        if (this == o)
        {
            return 0;
        }
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Node && ((Node) o).id == id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}
