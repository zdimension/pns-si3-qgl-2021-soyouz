package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.nodes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;

public abstract class GraphNode implements Comparable<GraphNode>
{
    private Position position;
    private String name;

    public GraphNode(Position position, String name)
    {
        this.position = position;
        this.name = name;
    }

    public Position getPosition()
    {
        return position;
    }

    public String getName()
    {
        return name;
    }

}
