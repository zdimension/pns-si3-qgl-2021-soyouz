package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.nodes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import org.jetbrains.annotations.NotNull;

public class ReefNode extends GraphNode
{
    public ReefNode(Position position, String name)
    {
        super(position, name);
    }

    @Override
    public int compareTo(@NotNull GraphNode o)
    {
        return 0;
    }
}
