package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

public abstract class Objective {
    InitGameParameters ip;

    //TODO
    public Objective(InitGameParameters ip) {
        this.ip = ip;
    }
}
