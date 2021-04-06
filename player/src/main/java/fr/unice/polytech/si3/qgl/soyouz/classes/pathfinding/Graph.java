package fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding;

import java.util.*;

public class Graph
{
    private final List<Node> path;
    private final Node start;
    private final Node end;

    public Graph(List<Node> nodes, int start, int end)
    {
        this.start = nodes.get(start);
        this.start.minCostToStart = 0;
        this.end = nodes.get(end);

        for (Node node : nodes)
        {
            node.distanceToEnd = node.position.sub(this.end.position).norm();
        }

        doAStar();

        var path = new ArrayList<Node>();
        path.add(this.end);
        buildShortestPath(path, this.end);
        Collections.reverse(path);
        this.path = path;
    }

    private void doAStar()
    {
        var queue = new ArrayList<Node>();
        queue.add(start);
        do
        {
            queue.sort(Comparator.comparing(node -> node.minCostToStart + node.distanceToEnd));
            var node = queue.remove(0);
            node.connections.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .forEach(entry ->
                {
                    var child = entry.getKey();
                    if (child.visited)
                        return;
                    var cost = entry.getValue();
                    if (Double.isNaN(child.minCostToStart)
                    || node.minCostToStart + cost < child.minCostToStart)
                    {
                        child.minCostToStart = node.minCostToStart + cost;
                        child.nearestToStart = node;
                        if (!queue.contains(child))
                            queue.add(child);
                    }
                });
            node.visited = true;
            if (node == end)
                return;
        } while (!queue.isEmpty());
    }

    public List<Node> getShortestPath()
    {
        return this.path;
    }

    private void buildShortestPath(List<Node> path, Node end)
    {
        if (end.nearestToStart == null)
        {
            return;
        }

        path.add(end.nearestToStart);
        buildShortestPath(path, end.nearestToStart);
    }
}
