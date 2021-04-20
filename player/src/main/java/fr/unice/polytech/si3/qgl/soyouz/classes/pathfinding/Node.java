package fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node implements Comparable<Node>
{
    public final Point2d position;
    public final int id;
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

    public double getCost()
    {
        return minCostToStart + distanceToEnd;
    }

    public int getId()
    {
        return id;
    }

    private static final Comparator<Node> COMPARATOR =
        Comparator.comparingDouble(Node::getCost).thenComparingInt(Node::getId);

    @Override
    public int compareTo(@NotNull Node o)
    {
        if (this == o)
            return 0;
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Node && ((Node)o).id == id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}
