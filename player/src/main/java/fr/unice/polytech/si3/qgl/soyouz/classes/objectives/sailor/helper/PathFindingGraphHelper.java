package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import com.google.common.graph.ValueGraph;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Stream;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.astar.AStar;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.pathfinding.nodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathFindingGraphHelper
{
    ValueGraph<GraphNode, Double> currentGraph;
    StartingNode start;
    EndingNode target;
    HeuristicForNodes heuristic;
    List<GraphNode> shortestPath;

    public void updateGraphWithNextTarget(Checkpoint cp, Bateau boat, Set<Entity> visibleEntities){
        start = new StartingNode(boat.getPosition(),"newStart");
        target = new EndingNode(cp.getPosition(),"newCp");
        generateVisibleEntitiesNodes(visibleEntities);
        shortestPath = AStar.findShortestPath(currentGraph,start,target,heuristic);
    }

    public void updateCurrentGraphWithNewEntities(Set<Entity> entities, Bateau boat){
        start = new StartingNode(boat.getPosition(),"newStart?");
        generateVisibleEntitiesNodes(entities);
        shortestPath = AStar.findShortestPath(currentGraph,start,target,heuristic);
    }

    private void generateVisibleEntitiesNodes(Set<Entity> entities)
    {
        for ( Entity e: entities)
        {
            if (e instanceof Reef){
                //TODO : generate new nodes and edges
                generateReefNodes((Reef) e);
            }
            else if (e instanceof Stream){
                generateStreamNodes((Stream) e);
            }

        }
    }

    private void generateReefNodes(Reef reef){
        //TODO : add nodes and links to graph
    }

    private void generateStreamNodes(Stream stream){
        //TODO : add nodes and links to graph
    }
}
