package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.Objective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorYMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.List;

public class RowersObjective implements Objective
{
    //TODO LES MARINS POUVANT BOUGER SONT SEULEMENT LES MARINS AYANT 2 RAMES SUR LEUR LIGNES
    private final List<Marin> rowers;
    //TODO UN ROWER IMMUTABLE EST UN ROWER SUR UNE LIGNE AVEC UNE SEULE RAME OU LES ROWERS QUI SONT 2 SUR LA LIGNE
    private final List<Marin> immutableRowers;
    private final Pair<Integer, Integer> rowerConfigurationWanted;
    private final List<SailorYMovementObjective> movingRowers;

    public RowersObjective(List<Marin> rowers, List<Marin> immutableRowers, Pair<Integer, Integer> rowerConfigurationWanted)
    {
        this.rowers = rowers;
        this.immutableRowers = immutableRowers;
        this.rowerConfigurationWanted = rowerConfigurationWanted;
        movingRowers = moveRowersToOars();
    }

    private List<SailorYMovementObjective> moveRowersToOars()
    {

        return null;
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
        long notDone = movingRowers.stream().filter(mov -> !mov.isValidated()).count();
        return notDone == 0;
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
        List<GameAction> actions = new ArrayList<>();
        movingRowers.forEach(obj -> actions.addAll(obj.resolve()));
        if (!isValidated(state))
            return actions;
        rowers.forEach(rower -> actions.add(new OarAction(rower)));
        immutableRowers.forEach(rowers -> actions.add(new OarAction(rowers)));
        return actions;
    }
    //TODO SI LA CONFIG N'EST PAS ATTEIGNABLE IMMEDIATEMENT ALORS DEPLACE SEULEMENT LES MARINS
}
