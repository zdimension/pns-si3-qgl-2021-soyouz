package fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

import java.util.HashMap;
import java.util.Map;

public class Node
{
    public final Point2d position;
    public double minCostToStart = Double.NaN;
    public double distanceToEnd;
    public Map<Node, Double> connections = new HashMap<>();
    public boolean visited;
    public Node nearestToStart;

    public Node(Point2d position)
    {
        this.position = position;
    }

    public void addNeighbour(Node neighbour)
    {
        var distance = neighbour.position.sub(this.position).norm();
        connections.put(neighbour, distance);
        neighbour.connections.put(this, distance);
    }
}
