package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

import java.util.Collection;

public class CompositeObjective extends Objective {
    protected Collection<Objective> intermediateObjective;

    //TODO
    public CompositeObjective(InitGameParameters ip, NextRoundParameters np) {
        super(ip, np);
    }
}
