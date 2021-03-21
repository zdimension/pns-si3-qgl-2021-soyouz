package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic implementation for essential methods of top level objectives.
 */
public abstract class CompositeObjective implements Objective
{
    protected final ArrayList<Objective> intermediateObjective = new ArrayList<>();

    /**
     * Determine if the goal is reached.
     *
     * @param state of the game
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return intermediateObjective.stream().allMatch(o -> o.isValidated(state));
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @param state of the game
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve(GameState state)
    {
        return intermediateObjective.stream()
            .filter(o -> !o.isValidated(state))
            .flatMap(o -> o.resolve(state).stream())
            .collect(Collectors.toList());
    }
}
