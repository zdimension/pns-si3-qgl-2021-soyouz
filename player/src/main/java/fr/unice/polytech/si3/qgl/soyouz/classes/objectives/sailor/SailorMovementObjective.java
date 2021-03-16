package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.Objective;

import java.util.ArrayList;
import java.util.List;

public class SailorMovementObjective implements Objective
{
    Marin sailor;
    int xOnDeck;
    int nbTurnToComplete;

    public SailorMovementObjective(Marin sailor, int xOnDeck)
    {
        this.sailor = sailor;
        this.xOnDeck = xOnDeck;
        nbTurnToComplete = Math.abs(xOnDeck - sailor.getX()) / 5;
    }

    @Override
    public boolean isValidated(GameState state)
    {
        return sailor.getX() == xOnDeck;
    }

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
        return moveAction;
    }
}
