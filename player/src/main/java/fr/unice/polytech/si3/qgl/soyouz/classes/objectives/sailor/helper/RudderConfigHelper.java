package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;

/**
 * Class to determine the optimal Rudder configuration to be the closest possible to the objective.
 */
public class RudderConfigHelper
{

    private final double neededRotation;

    /**
     * Constructor.
     *
     * @param neededRotation The angle between the boat and the checkpoint.
     */
    public RudderConfigHelper(double neededRotation)
    {
        this.neededRotation = neededRotation;
    }

    /**
     * Determine if the rudder can fully complete the rotation.
     *
     * @return true if the angle is in range, false otherwise.
     */
    private boolean rudderRotationIsInRange()
    {
        return Gouvernail.isValid(neededRotation);
    }

    /**
     * Determine the best rudder rotation possible to reduce the angle between the boat and the
     * checkpoint.
     *
     * @return the angle of rotation that the rudder will perform.
     */
    public double findOptRudderRotation()
    {
        if (rudderRotationIsInRange())
        {
            return neededRotation;
        }
        else
        {
            return Math.signum(neededRotation) * Gouvernail.ALLOWED_ROTATION;
        }
    }
}
