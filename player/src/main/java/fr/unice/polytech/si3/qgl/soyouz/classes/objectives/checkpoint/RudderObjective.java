package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;

public class RudderObjective {

    private final double neededRotation;

    public RudderObjective(double neededRotation) {
        this.neededRotation = neededRotation;
    }

    public boolean rudderRotationIsInRange (){
        return neededRotation > Gouvernail.ALLOWED_ROTATION.first && neededRotation < Gouvernail.ALLOWED_ROTATION.second;
    }

    public double resolve () {
        if (rudderRotationIsInRange())
            return neededRotation;
        else {
            if (neededRotation<0)
                return Gouvernail.ALLOWED_ROTATION.first;
            else
                return Gouvernail.ALLOWED_ROTATION.second;
        }
    }
}
