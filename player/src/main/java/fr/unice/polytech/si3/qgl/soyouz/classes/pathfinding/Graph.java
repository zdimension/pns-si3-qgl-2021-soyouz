package fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Computes a graph according to given nodes
 */
public class Graph
{
    private final List<Node> path;
    private final Node start;
    private final Node end;
    private final List<Node> nodes;

    public Graph(List<Node> nodes, int start, int end)
    {
        trace();
        this.nodes = nodes;
        this.start = nodes.get(start);
        this.start.minCostToStart = 0;
        this.end = nodes.get(end);

        for (Node node : nodes)
        {
            node.distanceToEnd = node.position.sub(this.end.position).norm();
        }

        doAStar();

        var tmpPath = new ArrayList<Node>();
        tmpPath.add(this.end);
        buildShortestPath(tmpPath, this.end);
        Collections.reverse(tmpPath);
        this.path = tmpPath;
    }

    /**
     * Computes A* algorithm
     */
    private void doAStar()
    {
        trace();
        var queue = new TreeSet<Node>();
        queue.add(start);
        do
        {
            var node = queue.first();
            queue.remove(node);
            node.connections.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .forEachOrdered(entry ->
                {
                    var child = entry.getKey();
                    if (child.visited)
                    {
                        return;
                    }
                    var cost = entry.getValue();
                    if (Double.isNaN(child.minCostToStart)
                        || node.minCostToStart + cost < child.minCostToStart)
                    {
                        queue.remove(child);
                        child.minCostToStart = node.minCostToStart + cost;
                        child.nearestToStart = node;
                        queue.add(child);
                    }
                });
            node.visited = true;
            if (node == end)
            {
                return;
            }
        }
        while (!queue.isEmpty());
    }

    public List<Node> getShortestPath()
    {
        return this.path;
    }

    /**
     * Builds the shortest path
     *
     * @param path
     * @param end
     */
    private void buildShortestPath(List<Node> path, Node end)
    {
        trace();

        while (end.nearestToStart != null)
        {
            path.add(end.nearestToStart);
            end = end.nearestToStart;
        }
    }

    /**
     *
     * @return a set of all the edges connecting the nodes
     */
    public Set<Pair<Node, Node>> getEdges()
    {
        var res = new HashSet<Pair<Integer, Integer>>();
        var stack = new LinkedList<Node>();
        stack.add(start);
        while (!stack.isEmpty())
        {
            var node = stack.remove();
            var id = nodes.indexOf(node);
            for (Node nb : node.connections.keySet())
            {
                var i = nodes.indexOf(nb);
                if (res.add(Pair.of(Math.min(i, id), Math.max(i, id))))
                {
                    stack.add(nb);
                }
            }
        }
        return res.stream().map(p -> Pair.of(nodes.get(p.first), nodes.get(p.second))).collect(Collectors.toSet());
    }
}
