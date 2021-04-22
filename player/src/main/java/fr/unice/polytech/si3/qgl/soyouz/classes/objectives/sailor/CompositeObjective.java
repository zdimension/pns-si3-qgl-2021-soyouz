package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.Objective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.OnBoardObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CompositeObjective<T extends OnBoardObjective> implements OnBoardObjective
{
    protected final List<T> children = new ArrayList<>();

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return children.stream().allMatch(OnBoardObjective::isValidated);
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve()
    {
        return children.stream()
            .filter(obj -> !obj.isValidated())
            .flatMap(obj -> obj.resolve().stream())
            .collect(Collectors.toList());
    }
}
