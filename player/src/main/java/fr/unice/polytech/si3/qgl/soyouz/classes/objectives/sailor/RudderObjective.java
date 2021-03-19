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

public class RudderObjective implements OnBoardObjective
{
    private final double rotation;
    private final Marin sailor;
    MovingObjective movement;

    public RudderObjective(Bateau ship, double rotation, Marin sailor)
    {
        this.rotation = rotation;
        this.sailor = sailor;
        movement = null;
        setMovement(ship);
    }

    private void setMovement(Bateau ship)
    {
        OnboardEntity rudder = ship.findFirstEntity(Gouvernail.class);
        if (!ship.hasAt(sailor.getX(), sailor.getY(), Gouvernail.class))
        {
            movement = new SailorMovementObjective(sailor, rudder.getPos());
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
        return movement == null || movement.isValidated();
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve()
    {
        List<GameAction> actions = new ArrayList<>();
        if (movement != null)
            actions.addAll(movement.resolve());
        if (movement.isValidated())
            actions.add(new TurnAction(sailor, rotation));
        return actions;
    }
}
