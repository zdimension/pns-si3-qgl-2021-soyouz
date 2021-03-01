package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import java.util.List;

public interface Objective {

    /**
     *
     * @param state of the game
     * @return true if this objective is validated
     */
    boolean isValidated(GameState state);

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @param state of the game
     * @return a list of all actions to send to JSON
     */
    List<GameAction> resolve(GameState state);
}
