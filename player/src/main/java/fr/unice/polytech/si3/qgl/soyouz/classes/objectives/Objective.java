package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

public abstract class Objective {
    InitGameParameters ip;
    NextRoundParameters np;

    public Bateau getShip(){
        return np.getShip();
    }

    public Marin[] getSailors(){
        return ip.getSailors();
    }

    public GameGoal getGameGoal(){
        return ip.getGoal();
    }

    //TODO
    public Objective(InitGameParameters ip, NextRoundParameters np) {
        this.ip = ip;
        this.np = np;
    }
}
