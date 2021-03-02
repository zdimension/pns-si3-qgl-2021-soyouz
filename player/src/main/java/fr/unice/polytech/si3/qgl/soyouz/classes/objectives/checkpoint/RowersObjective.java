package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.OarConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to determine the optimal OarConfiguration to be the closest possible to the objective.
 */
public class RowersObjective {

    private final double neededRotation;
    private final double distToCheckpoint;
    private final int totalNbOfOar;
    private final List<OarConfiguration> leftTurnPossibilities;
    private final List<OarConfiguration> rightTurnPossibilities;
    private final List<OarConfiguration> forwardPossibilities;

    /**
     * Constructor.
     *
     * @param neededRotation The angle between the boat and the checkpoint.
     * @param distToCheckpoint The distance between the boat and the checkpoint.
     * @param nbSailorReadyToOar The number of sailors able to oar.
     * @param nbOarOnLeft The number of oars on the left side of the boat.
     * @param nbOarOnRight The number of oars on the right side of the boat.
     */
    public RowersObjective(double neededRotation, double distToCheckpoint, int nbSailorReadyToOar, int nbOarOnLeft, int nbOarOnRight) {
        this.neededRotation = neededRotation;
        this.distToCheckpoint = distToCheckpoint;
        totalNbOfOar = nbOarOnLeft + nbOarOnRight;
        leftTurnPossibilities = new ArrayList<>();
        rightTurnPossibilities = new ArrayList<>();
        forwardPossibilities = new ArrayList<>();
        setOaringPossibilities(nbSailorReadyToOar, nbOarOnLeft, nbOarOnRight);
    }

    /**
     * Determine if the boat should go straight or turn and call the respective method.
     *
     * @param nbSailor The number of sailors ready to oar.
     * @param nbOarOnLeft The number of oars on the left side of the boat.
     * @param nbOarOnRight The number of oars on the right side of the boat.
     */
    private void setOaringPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        if (Gouvernail.ALLOWED_ROTATION.first < neededRotation && neededRotation < Gouvernail.ALLOWED_ROTATION.second)
            setForwardPossibilities(nbSailor);
        else
            setTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
    }

    /**
     * Initialise all the possibles oar configuration to go straight forward.
     *
     * @param nbSailor The number of sailors ready to oar.
     */
    private void setForwardPossibilities(int nbSailor) {
        for (int i = 2; i <= nbSailor; i += 2) {
            forwardPossibilities.add(new OarConfiguration(i / 2, i / 2, totalNbOfOar));
        }
    }

    /**
     * Determine on which side the boat should turn and call the respective method.
     *
     * @param nbSailor     The number of sailors ready to oar.
     * @param nbOarOnLeft  The number of oars on the left side of the boat.
     * @param nbOarOnRight The number of oars on the right side of the boat.
     */
    private void setTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        if (neededRotation > 0)
            setLeftTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
        else
            setRightTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
    }

    /**
     * Set all the oar configuration to turn left.
     *
     * @param nbSailor The number of sailors ready to oar.
     * @param nbOarOnLeft The number of oars on the left side of the boat.
     * @param nbOarOnRight The number of oars on the right side of the boat.
     */
    private void setLeftTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        for (int rightNb = 0; (rightNb <= nbOarOnRight) && (rightNb <= nbSailor); rightNb++) {
            for (int leftNb = 0; (leftNb < rightNb) && (leftNb <= nbOarOnLeft) && (leftNb + rightNb <= nbSailor); leftNb++)
                leftTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, totalNbOfOar));
        }
    }

    /**
     * Set all the oar configuration to turn right.
     *
     * @param nbSailor The number of sailors ready to oar.
     * @param nbOarOnLeft The number of oars on the left side of the boat.
     * @param nbOarOnRight The number of oars on the right side of the boat.
     */
    private void setRightTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        for (int leftNb = 0; (leftNb <= nbOarOnLeft) && (leftNb <= nbSailor); leftNb++) {
            for (int rightNb = 0; (rightNb < leftNb) && (rightNb <= nbOarOnRight) && (leftNb + rightNb <= nbSailor); rightNb++)
                rightTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, totalNbOfOar));
        }
    }

    /**
     * Find the best oar configuration to get the closest to the goal.
     *
     * @return the wanted configuration.
     */
    public OarConfiguration resolve() {
        if (Gouvernail.ALLOWED_ROTATION.first < neededRotation && neededRotation < Gouvernail.ALLOWED_ROTATION.second)
            return resolveBasedOnSpeed();
        else {
            if (neededRotation > 0)
                return resolveBasedOnRotation(leftTurnPossibilities);
            else return resolveBasedOnRotation(rightTurnPossibilities);
        }
    }

    /**
     * Find the best oar configuration to reduce as possible the angle between the boat and the checkpoint.
     *
     * @param turnPossibilities All the oar configuration to turn on the wanted side.
     * @return the optimal configuration.
     */
    private OarConfiguration resolveBasedOnRotation(List<OarConfiguration> turnPossibilities) {
        OarConfiguration optimalConfiguration = turnPossibilities.get(0);
        double difference = Math.abs(neededRotation - optimalConfiguration.getAngleOfRotation());
        for (OarConfiguration configuration : turnPossibilities) {
            double tempDiff = Math.abs(neededRotation - configuration.getAngleOfRotation());
            if (tempDiff <= difference) {
                optimalConfiguration = configuration;
                difference = tempDiff;
            }
        }
        return optimalConfiguration;
    }

    /**
     * Find the best oar configuration to reduce as possible the distance between the boat and the checkpoint.
     *
     * @return the optimal configuration.
     */
    private OarConfiguration resolveBasedOnSpeed() {
        OarConfiguration optimalConfiguration = forwardPossibilities.get(0);
        double difference = distToCheckpoint - optimalConfiguration.getLinearSpeed();
        for (OarConfiguration configuration : forwardPossibilities) {
            double tempDiff = distToCheckpoint - configuration.getLinearSpeed();
            if (Math.abs(tempDiff) < Math.abs(difference)) {
                optimalConfiguration = configuration;
                difference = tempDiff;
            }
        }
        if (difference < 0) {
            optimalConfiguration = neededRotation > 0 ?
                    new OarConfiguration(0, 1, totalNbOfOar) :
                    new OarConfiguration(1, 0, totalNbOfOar);
        }
        return optimalConfiguration;
    }
}