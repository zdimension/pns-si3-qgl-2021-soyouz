package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.OnBoardObjective;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to move a sailor only based on a Y position.
 */
public class SailorYMovementObjective implements MovingObjective
{
    Marin sailor;
    int yOnDeck;
    int nbTurnToComplete;

    /**
     * Constructor.
     * @param sailor The sailor that will move.
     * @param yOnDeck The Y position wanted.
     */
    public SailorYMovementObjective(Marin sailor, int yOnDeck)
    {
        this.sailor = sailor;
        this.yOnDeck = yOnDeck;
        nbTurnToComplete = Math.abs(yOnDeck - sailor.getX()) / 5 + 1;
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return sailor.getY() == yOnDeck || nbTurnToComplete == 0;
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve()
    {
        List<GameAction> moveAction = new ArrayList<>();
        int distStillToParkour = yOnDeck - sailor.getY();
        if (distStillToParkour > 5)
            moveAction.add(new MoveAction(sailor, 0, 5));
        else if (distStillToParkour < -5)
            moveAction.add(new MoveAction(sailor, 0, -5));
        else
            moveAction.add(new MoveAction(sailor, 0, distStillToParkour));
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