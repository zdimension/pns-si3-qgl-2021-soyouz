package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.WatchAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Crow-nest objective
 */
public class WatchObjective implements OnBoardObjective
{
    private final Marin sailor;
    List<MovingObjective> movement;


    public WatchObjective(Bateau ship, Marin sailor)
    {
        this.sailor = sailor;
        movement = new ArrayList<>();
        setMovement(ship);
    }

    private void setMovement(Bateau ship)
    {
        trace();
        OnboardEntity crownest = ship.findFirstEntity(Vigie.class);
        if (!ship.hasAt(sailor.getX(), sailor.getY(), Vigie.class))
        {
            movement.add(new SailorMovementObjective(sailor, crownest.getPos()));
        }
    }

    @Override
    public boolean isValidated()
    {
        return movement.isEmpty() || movement.get(0).isValidated();
    }

    @Override
    public List<GameAction> resolve()
    {
        trace();
        List<GameAction> actions = new ArrayList<>();
        if (movement.size() == 1)
            actions.addAll(movement.get(0).resolve());
        if (movement.isEmpty() || movement.get(0).isValidated())
            actions.add(new WatchAction(sailor));
        return actions;
    }
}
