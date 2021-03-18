package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;

import java.util.List;

public interface MovingObjective
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
