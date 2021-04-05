package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.nodes;

import java.util.function.Function;
import com.google.common.graph.*;
public class HeuristicForNodes implements Function<GraphNode, Double>
{
    
    private final double maxSpeed;
    private final GraphNode target;

    /**
     * Constructs the heuristic function for the specified graph and target node.
     *
     * @param graph the graph
     * @param target the target node
     */
    public HeuristicForNodes(ValueGraph<GraphNode, Double> graph, GraphNode target) {
        // We need the maximum speed possible on any path in the graph for the heuristic function to
        // calculate the cost for a euclidean distance
        this.maxSpeed = calculateMaxSpeed(graph);
        this.target = target;
    }

    /**
     * Calculates the maximum speed possible on any path in the graph. The speed of a path is
     * calculated as: euclidean distance between the path's nodes, divided by its cost.
     *
     * @param graph the graph
     * @return the maximum speed
     */
    private static double calculateMaxSpeed(ValueGraph<GraphNode, Double> graph) {
        return graph.edges().stream()
            .map(edge -> calculateSpeed(graph, edge))
            .max(Double::compare)
            .get();
    }

    /**
     * Calculates the speed on a path in the graph. The speed of a path is calculated as: euclidean
     * distance between the path's nodes, divided by its cost.
     *
     * @param graph the graph
     * @param edge the edge (= path)
     * @return the speed
     */
    private static double calculateSpeed(ValueGraph<GraphNode, Double> graph, EndpointPair<GraphNode> edge) {
        double euclideanDistance = calculateEuclideanDistance(edge.nodeU(), edge.nodeV());
        double cost = graph.edgeValue(edge).get();
        double speed = euclideanDistance / cost;

        return speed;
    }

    public static double calculateEuclideanDistance(GraphNode source, GraphNode target) {
        return source.getPosition().getLength(target.getPosition());
    }

    /**
     * Applies the heuristic function to the specified node.
     *
     * @param node the node
     * @return the minimum cost for traveling from the specified node to the target
     */
    @Override
    public Double apply(GraphNode node) {
        double euclideanDistance = calculateEuclideanDistance(node, target);
        double minimumCost = euclideanDistance / maxSpeed;

        return minimumCost;
    }
}
