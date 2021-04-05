package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.nodes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;

public abstract class GraphNode
{
    private Position position;
    private String name;
    private double additionnalSpeed;


    public Position getPosition()
    {
        return position;
    }

    public String getName()
    {
        return name;
    }

}
