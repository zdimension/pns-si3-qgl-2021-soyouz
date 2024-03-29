package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Class to move a sailor only based on a Y position.
 */
public class SailorYMovementObjective implements MovingObjective
{
    private final Marin sailor;
    private final int yOnDeck;
    private int nbTurnToComplete;

    /**
     * Constructor.
     *
     * @param sailor  The sailor that will move.
     * @param yOnDeck The Y position wanted.
     */
    public SailorYMovementObjective(Marin sailor, int yOnDeck)
    {
        this.sailor = sailor;
        this.yOnDeck = yOnDeck;
        nbTurnToComplete = Math.abs(yOnDeck - sailor.getY()) / 5 + 1;
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
        trace();
        List<GameAction> moveAction = new ArrayList<>();
        int distStillToParkour = yOnDeck - sailor.getY();
        int yMove = Util.clamp(distStillToParkour, -5, 5);
        sailor.moveRelative(PosOnShip.of(0, yMove));
        moveAction.add(new MoveAction(sailor, 0, yMove));
        nbTurnToComplete--;
        return moveAction;
    }

    /**
     * Getter.
     *
     * @return the number of turn needed to complete the objective.
     */
    public int getNbTurnToComplete()
    {
        return nbTurnToComplete;
    }

    /**
     * Getter.
     *
     * @return the sailor.
     */
    public Marin getSailor()
    {
        return sailor;
    }
}