package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;

import java.util.List;

/**
 * This interface is meant to represents objectives that must be done in a turn or two but don't
 * last all game long.
 */
public interface OnBoardObjective
{
    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    boolean isValidated();

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    List<GameAction> resolve();
}