package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

import java.util.stream.IntStream;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Class to determine the optimal Sails configuration to be the closest possible to the objective.
 */
public class SailConfigHelper
{
    private final double distToCheckpoint;
    private final double orientation;
    private final int nbOfSails;
    private final Wind wind;

    /**
     * Constructor.
     *
     * @param distToCheckpoint The distance between the ship and the checkpoint.
     * @param nbOfSails        The number of sail onboard.
     * @param ship             The ship.
     * @param wind             The wind.
     */
    public SailConfigHelper(double distToCheckpoint, double orientation, int nbOfSails,
                            Bateau ship, Wind wind)
    {
        this.distToCheckpoint = distToCheckpoint;
        this.orientation = ship.getPosition().getOrientation() + orientation;
        this.nbOfSails = nbOfSails;
        this.wind = wind;
    }

    /**
     * Determine how many sails should be opened.
     *
     * @return the number of sails to be opened.
     */
    public int findOptSailConfiguration()
    {
        trace();
        int optimalConfig = 0;
        double diff = distToCheckpoint;
        for (int i = 1; i <= nbOfSails; i++)
        {
            double additionalSpeed = windAdditionalSpeed(i);
            if (distToCheckpoint - additionalSpeed < diff && additionalSpeed > 0)
            {
                diff = distToCheckpoint - additionalSpeed;
                optimalConfig = i;
            }
        }
        return optimalConfig;
    }

    /**
     * Determine how much speed will the wind add to the boat.
     *
     * @param openedSails The number of opened sails.
     * @return the speed added by the wind.
     */
    private double windAdditionalSpeed(int openedSails)
    {
        if (nbOfSails > 0)
        {
            return ((double) openedSails / nbOfSails) * wind.getStrength() * Math.cos(wind.getOrientation() - orientation);
        }
        return 0;
    }
}
