package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.CompositeObjective;

/**
 * Basic class of objective.
 */
public abstract class RootObjective extends CompositeObjective {
    /**
     * Determine if a goal is validate or not
     *
     * @param state State of the goal.
     * @return true if it is, false otherwise.
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return false;
    }

    /**
     * Updates this objective according to the state of the game
     *
     * @param state of the game
     */
    public abstract void update(GameState state);
}
