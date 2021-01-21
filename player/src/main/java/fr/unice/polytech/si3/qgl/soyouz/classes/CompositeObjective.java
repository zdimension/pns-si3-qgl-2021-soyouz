package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.util.Collection;

public class CompositeObjective extends Objective {
    protected Collection<Objective> intermediateObjective;

    public CompositeObjective(InitGameParameters ip) {
        super(ip);
    }
}
