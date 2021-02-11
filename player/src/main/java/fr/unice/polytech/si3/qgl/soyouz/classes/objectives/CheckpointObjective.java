package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

public class CheckpointObjective extends CompositeObjective{


    //TODO
    public CheckpointObjective(Checkpoint checkpoint) {

        /*var dist = this.getShip().getPosition().getDistance(checkpoint.getPosition()); //distance au centre pour l'instant
        //on vérifie la rotation
        var maxSpeed = this.maxSpeedPossible();
        if(this.getShip().getPosition().isPositionReachable(checkpoint.getPosition(), maxSpeed)){
            //on envoie le nombre de marins nécessaire aux rames
            for (Marin s : this.ip.getSailors()) {
                this.intermediateObjective.add(new GroundObjective(new OarAction(s)));
            }
        }*/
        //sinon, pas encore géré
    }


    //TODO
    //alors speed c'est un double mais c'est arbitraire
    /*private double maxSpeedPossible(){
        return 165.0*this.getSailors().length/this.getShip().getNumberOar();
    }*/
}
