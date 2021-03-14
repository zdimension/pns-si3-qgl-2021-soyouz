package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.WantedSailorConfig;

import java.util.List;

public class SailorMovementObjective implements Objective
{
    private WantedSailorConfig wanted;
    private int roundsToAchieve;

    public SailorMovementObjective(WantedSailorConfig wanted)
    {
        this.wanted = wanted;
        roundsToAchieve = Integer.MAX_VALUE;
    }

    public SailorMovementObjective(WantedSailorConfig wanted, int rounds)
    {
        this.wanted = wanted;
        this.roundsToAchieve = rounds;
    }

    @Override
    public boolean isValidated(GameState state)
    {
        //todo voir si tous les endroits voulus ont bien quelqu'un dessus
        return false;
    }

    @Override
    public List<GameAction> resolve(GameState state)
    {
        return null;
    }


}
