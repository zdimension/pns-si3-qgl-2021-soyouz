package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

//TODO VERIFIER QU'ON NE DEPASSE PAS LE CP
/**
 * Class to determine the optimal Sails configuration to be the closest possible to the objective.
 */
public class SailConfigHelper
{
    private final double distToCheckpoint;
    private final double orientation;
    private final int nbOfSails;
    private final Bateau ship;
    private final Wind wind;

    /**
     * Constructor.
     *
     * @param distToCheckpoint The distance between the ship and the checkpoint.
     * @param nbOfSails The number of sail onboard.
     * @param ship The ship.
     * @param wind The wind.
     */
    public SailConfigHelper(double distToCheckpoint, double orientation, int nbOfSails, Bateau ship, Wind wind)
    {
        this.distToCheckpoint = distToCheckpoint;
        this.orientation = orientation;
        this.nbOfSails = nbOfSails;
        this.ship = ship;
        this.wind = wind;
    }

    /**
     * Determine how many sails should be opened.
     *
     * @return the number of sails to be opened.
     */
    public int findOptSailConfiguration()
    {
        int optimalConfig = 0;
        double diff = distToCheckpoint;
        for (int i = 1; i <= nbOfSails; i++)
        {
            double additionalSpeed = windAdditionalSpeed(nbOfSails, i, ship, wind);
            if (distToCheckpoint - additionalSpeed < diff && additionalSpeed > 0)
                optimalConfig = i;
        }
        return optimalConfig;
    }

    /**
     * Determine how much speed will the wind add to the boat.
     *
     * @param nbOfSails   The number of sails.
     * @param openedSails The number of opened sails.
     * @param boat        Out boat.
     * @return the speed added by the wind.
     */
    private double windAdditionalSpeed(int nbOfSails, int openedSails, Bateau boat, Wind wind)
    {
        if (nbOfSails > 0)
            return ((double) openedSails / nbOfSails) * wind.getStrength() * Math.cos(wind.getOrientation() - boat.getPosition().getOrientation());
        return 0;
    }
}
