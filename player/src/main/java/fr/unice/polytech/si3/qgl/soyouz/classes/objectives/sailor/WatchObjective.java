package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.WatchAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Watch objective
 */
public class WatchObjective implements OnBoardObjective
{
    private final Marin sailor;
    private List<MovingObjective> movement;

    /**
     * Constructor.
     *
     * @param ship   The ship.
     * @param sailor The sailor attached to the Watch.
     */
    public WatchObjective(Bateau ship, Marin sailor)
    {
        this.sailor = sailor;
        movement = new ArrayList<>();
        setMovement(ship);
    }

    /**
     * Move the Watch sailor to the watch if needed.
     *
     * @param ship The ship.
     */
    private void setMovement(Bateau ship)
    {
        trace();
        OnboardEntity watch = ship.findFirstEntity(Vigie.class);
        if (!ship.hasAt(sailor.getPos(), Vigie.class))
        {
            movement.add(new SailorMovementObjective(sailor, watch.getPos()));
        }
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */

    @Override
    public boolean isValidated()
    {
        return movement.isEmpty() || movement.get(0).isValidated();
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
        List<GameAction> actions = new ArrayList<>();
        if (movement.size() == 1)
        {
            actions.addAll(movement.get(0).resolve());
        }
        if (isValidated())
        {
            actions.add(new WatchAction(sailor));
        }
        return actions;
    }
}
