package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.Objective;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to move a sailor only based on X position.
 */
public class SailorXMovementObjective implements Objective
{
    Marin sailor;
    int xOnDeck;
    int nbTurnToComplete;

    /**
     * Constructor.
     * @param sailor The sailor that will move.
     * @param xOnDeck The X position wanted.
     */
    public SailorXMovementObjective(Marin sailor, int xOnDeck)
    {
        this.sailor = sailor;
        this.xOnDeck = xOnDeck;
        nbTurnToComplete = Math.abs(xOnDeck - sailor.getX()) / 5 + 1;
    }

    /**
     * Determine if the goal is reached.
     *
     * @param state of the game
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return sailor.getX() == xOnDeck || nbTurnToComplete == 0;
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
        List<GameAction> moveAction = new ArrayList<>();
        int distStillToParkour = xOnDeck - sailor.getX();
        if (distStillToParkour > 5)
            moveAction.add(new MoveAction(sailor, 5, 0));
        else if (distStillToParkour < -5)
            moveAction.add(new MoveAction(sailor, -5, 0));
        else
            moveAction.add(new MoveAction(sailor, distStillToParkour, 0));
        nbTurnToComplete--;
        return moveAction;
    }

    /**
     * Getter.
     * @return the number of turn needed to complete the objective.
     */
    public int getNbTurnToComplete()
    {
        return nbTurnToComplete;
    }
}