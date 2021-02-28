package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CompositeObjective implements Objective {
    protected ArrayList<Objective> intermediateObjective = new ArrayList<>();

    @Override
    public boolean isValidated(GameState state)
    {
        return intermediateObjective.stream().allMatch(o -> o.isValidated(state));
    }

    @Override
    public List<GameAction> resolve(GameState state)
    {
        return intermediateObjective.stream()
            .filter(o -> !o.isValidated(state))
            .flatMap(o -> o.resolve(state).stream())
            .collect(Collectors.toList());
    }
}
