package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * The rudder rotation related objective.
 */
public class RudderObjective implements OnBoardObjective
{
    private final double rotation;
    private final Marin sailor;
    List<MovingObjective> movement;

    /**
     * Constructor.
     *
     * @param ship     The ship.
     * @param rotation The wanted rotation.
     * @param sailor   The sailor attached to the rudder.
     */
    public RudderObjective(Bateau ship, double rotation, Marin sailor)
    {
        this.rotation = rotation;
        this.sailor = sailor;
        movement = new ArrayList<>();
        setMovement(ship);
    }

    /**
     * Move the rudder sailor to the rudder if needed.
     *
     * @param ship The ship.
     */
    private void setMovement(Bateau ship)
    {
        trace();
        OnboardEntity rudder = ship.findFirstEntity(Gouvernail.class);
        if (sailor != null && !sailor.getPos().equals(rudder.getPos()))
        {
            movement.add(new SailorMovementObjective(sailor, rudder.getPos()));
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
        if (sailor != null)
        {
            if (movement.size() == 1)
            {
                actions.addAll(movement.get(0).resolve());
            }
            if (isValidated())
            {
                actions.add(new TurnAction(sailor, rotation));
            }
        }
        return actions;
    }
}
