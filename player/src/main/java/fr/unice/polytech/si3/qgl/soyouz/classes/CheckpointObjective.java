package fr.unice.polytech.si3.qgl.soyouz.classes;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.util.Arrays;

public class CheckpointObjective extends CompositeObjective{


    //TODO
    public CheckpointObjective(InitGameParameters init, Checkpoint checkpoint) {
        super(init);

        var dist = this.ip.getShip().getPosition().getDistance(checkpoint.getPosition()); //distance au centre pour l'instant
        //on vérifie la rotation
        var maxSpeed = this.maxSpeedPossible();
        if(this.ip.getShip().getPosition().isPositionReachable(checkpoint.getPosition(), maxSpeed)){
            //on envoie le nombre de marins nécessaire aux rames
            for (Marin s : this.ip.getSailors()) {
                this.intermediateObjective.add(new GroundObjective(init, new OarAction(s)));
            }
        }
        //sinon, pas encore géré
    }


    //alors speed c'est un double mais c'est arbitraire
    private double maxSpeedPossible(){
        return 165*ip.getSailors().length/ip.getShip().getNumberOar();
    }
}
