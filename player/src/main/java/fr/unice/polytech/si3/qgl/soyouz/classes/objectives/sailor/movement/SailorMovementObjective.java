package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to move a sailor based on X and Y positions.
 */
public class SailorMovementObjective implements MovingObjective
{
    Marin sailor;
    int xOnDeck;
    int yOnDeck;
    int nbTurnToComplete;

    /**
     * Constructor.
     *
     * @param sailor The sailor that will move.
     * @param xOnDeck The X position wanted.
     * @param yOnDeck The Y position wanted.
     */
    public SailorMovementObjective(Marin sailor, int xOnDeck, int yOnDeck)
    {
        this.sailor = sailor;
        this.xOnDeck = xOnDeck;
        this.yOnDeck = yOnDeck;
        nbTurnToComplete = Math.abs((xOnDeck - sailor.getX()) + (yOnDeck
         - sailor.getY())) / 5 + 1;
    }

    /**
     * Alternative Constructor.
     *
     * @param sailor The sailor that will move.
     * @param pos The PosOnShip wanted.
     */
    public SailorMovementObjective(Marin sailor, PosOnShip pos)
    {
        this(sailor, pos.getX(), pos.getY());
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return (sailor.getX() == xOnDeck && sailor.getY() == yOnDeck) || nbTurnToComplete == 0;
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
        int xMove = resolveXMove();
        int yMove = resolveYMove(xMove);
        sailor.moveRelative(xMove, yMove);
        moveAction.add(new MoveAction(sailor, xMove, yMove));
        nbTurnToComplete--;
        return moveAction;
    }

    /**
     * Find the optimal XMove to resolve the objective.
     *
     * @return the X translation.
     */
    private int resolveXMove()
    {
        int xDistStillToParkour = xOnDeck - sailor.getX();
        if (xDistStillToParkour > 5)
            return 5;
        else if (xDistStillToParkour < -5)
            return -5;
        return xDistStillToParkour;
    }

    /**
     * Find the optimal YMove to resolve the objective taking in count the XMove.
     *
     * @param xMove The previously determined X translation.
     * @return the Y translation.
     */
    private int resolveYMove(int xMove)
    {
        int moveCredit = 5 - Math.abs(xMove);
        int yDistStillToParkour = yOnDeck - sailor.getY();
        if (moveCredit < Math.abs(yDistStillToParkour))
            return yDistStillToParkour > 0 ? moveCredit : -moveCredit;
        return yDistStillToParkour;
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
