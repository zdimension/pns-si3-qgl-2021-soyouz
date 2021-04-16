package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.OarConfiguration;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Class to determine the optimal OarConfiguration to be the closest possible to the objective.
 */
public class RowersConfigHelper
{

    private final double neededRotation;
    private final double distToCheckpoint;
    private final int totalNbOfOar;
    private final int immutableLeftSailor;
    private final int immutableRightSailor;
    private final int mutableSailors;

    private final List<OarConfiguration> leftTurnPossibilities;
    private final List<OarConfiguration> rightTurnPossibilities;
    private final List<OarConfiguration> forwardPossibilities;

    /**
     * Constructor.
     *
     * @param neededRotation       The angle between the boat and the CP.
     * @param distToCheckpoint     The distance between the ship and the CP.
     * @param mutableSailors       The number of sailors that can move.
     * @param immutableRightSailor The number of sailors already placed on a right oar.
     * @param immutableLeftSailor  The number of sailors already placed on a left oar.
     * @param totalNbOfOar         The total number of oars.
     */
    public RowersConfigHelper(double neededRotation, double distToCheckpoint, int mutableSailors
        , int immutableRightSailor, int immutableLeftSailor, int totalNbOfOar)
    {
        this.neededRotation = neededRotation;
        this.distToCheckpoint = distToCheckpoint;
        this.totalNbOfOar = totalNbOfOar;
        this.immutableLeftSailor = immutableLeftSailor;
        this.immutableRightSailor = immutableRightSailor;
        this.mutableSailors = mutableSailors;
        leftTurnPossibilities = new ArrayList<>();
        rightTurnPossibilities = new ArrayList<>();
        forwardPossibilities = new ArrayList<>();
        setOaringPossibilities();
    }

    /**
     * Determine if the boat should go straight or turn and call the respective method.
     */
    private void setOaringPossibilities()
    {
        if (Gouvernail.isValid(neededRotation))
        {
            setForwardPossibilities();
        }
        else
        {
            setTurnPossibilities();
        }
    }

    /**
     * Initialise all the possibles oar configuration to go straight forward.
     */
    private void setForwardPossibilities()
    {
        trace();
        int maxSailorOnEachSide = determineMaxSailorNumberOnEachSide();
        for (int i = 1; i <= maxSailorOnEachSide; i++)
        {
            forwardPossibilities.add(new OarConfiguration(i, i, totalNbOfOar));
        }
    }

    /**
     * Detemine how much sailors will be able to be on each side of the boat in order
     * to go straight forward.
     *
     * @return the number of pair of rowers.
     */
    private int determineMaxSailorNumberOnEachSide()
    {
        trace();
        int mSailors = this.mutableSailors;
        int sailorLeft = immutableLeftSailor;
        int sailorRight = immutableRightSailor;
        while (Math.abs(sailorLeft - sailorRight) > 0 && mutableSailors > 0)
        {
            if (sailorLeft < sailorRight)
            {
                sailorLeft++;
                mSailors--;
            }

            if (sailorLeft > sailorRight)
            {
                sailorRight++;
                mSailors--;
            }
        }
        while (mSailors >= 2)
        {
            sailorLeft++;
            sailorRight++;
            mSailors -= 2;
        }
        return Math.min(sailorLeft, sailorRight);
    }

    /**
     * Determine on which side the boat should turn and call the respective method.
     */
    private void setTurnPossibilities()
    {
        trace();
        int maxLeftNb = immutableLeftSailor + mutableSailors;
        int maxRightNb = immutableRightSailor + mutableSailors;
        if (neededRotation > 0)
        {
            setLeftTurnPossibilities(maxLeftNb, maxRightNb);
        }
        else
        {
            setRightTurnPossibilities(maxLeftNb, maxRightNb);
        }
    }

    /**
     * Set all the oar configuration to turn left.
     */
    private void setLeftTurnPossibilities(int maxLeftNb, int maxRightNb)
    {
        trace();
        for (int rightNb = 0; rightNb <= maxRightNb; rightNb++)
        {
            for (int leftNb = 0; (leftNb < rightNb) && leftNb < (maxLeftNb - (rightNb
                - immutableRightSailor)); leftNb++)
            {
                leftTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, totalNbOfOar));
            }
        }
    }

    /**
     * Set all the oar configuration to turn right.
     */
    private void setRightTurnPossibilities(int maxLeftNb, int maxRightNb)
    {
        trace();
        for (int leftNb = 0; leftNb <= maxLeftNb; leftNb++)
        {
            for (int rightNb = 0; (rightNb < leftNb) && rightNb < (maxRightNb - (leftNb
                - immutableLeftSailor)); rightNb++)
            {
                rightTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, totalNbOfOar));
            }
        }
    }

    /**
     * Find the best oar configuration to get the closest to the goal.
     *
     * @return the wanted configuration.
     */
    public OarConfiguration findOptRowersConfiguration()
    {
        trace();
        if (Gouvernail.isValid(neededRotation))
        {
            return resolveBasedOnSpeed();
        }
        else
        {
            if (neededRotation > 0)
            {
                return resolveBasedOnRotation(leftTurnPossibilities);
            }
            else
            {
                return resolveBasedOnRotation(rightTurnPossibilities);
            }
        }
    }

    /**
     * Find the best oar configuration to reduce as possible the angle between the boat and the
     * checkpoint.
     *
     * @param turnPossibilities All the oar configuration to turn on the wanted side.
     * @return the optimal configuration.
     */
    private OarConfiguration resolveBasedOnRotation(List<OarConfiguration> turnPossibilities)
    {
        trace();
        OarConfiguration optimalConfiguration = turnPossibilities.get(0);
        double difference = Math.abs(neededRotation - optimalConfiguration.getAngleOfRotation());
        for (OarConfiguration configuration : turnPossibilities)
        {
            double tempDiff = Math.abs(neededRotation - configuration.getAngleOfRotation());
            if (tempDiff <= difference)
            {
                optimalConfiguration = configuration;
                difference = tempDiff;
            }
        }
        return optimalConfiguration;
    }

    /**
     * Find the best oar configuration to reduce as possible the distance between the boat and
     * the checkpoint.
     *
     * @return the optimal configuration.
     */
    private OarConfiguration resolveBasedOnSpeed()
    {
        trace();
        OarConfiguration optimalConfiguration = forwardPossibilities.get(0);
        double difference = distToCheckpoint - optimalConfiguration.getLinearSpeed();
        for (OarConfiguration configuration : forwardPossibilities)
        {
            double tempDiff = distToCheckpoint - configuration.getLinearSpeed();
            if (Math.abs(tempDiff) < Math.abs(difference))
            {
                optimalConfiguration = configuration;
                difference = tempDiff;
            }
        }
        if (difference < 0)
        {
            optimalConfiguration = neededRotation > 0 ?
                new OarConfiguration(0, 1, totalNbOfOar) :
                new OarConfiguration(1, 0, totalNbOfOar);
        }
        return optimalConfiguration;
    }
}