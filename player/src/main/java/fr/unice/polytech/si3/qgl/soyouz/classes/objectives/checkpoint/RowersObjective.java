package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;

import java.util.ArrayList;
import java.util.List;

public class RowersObjective {

    private final double neededRotation;
    private final double distToCheckpoint;
    private final List<OarConfiguration> leftTurnPossibilities;
    private final List<OarConfiguration> rightTurnPossibilities;
    private final List<OarConfiguration> forwardPossibilities;

    public RowersObjective(double neededRotation, double distToCheckpoint, int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        this.neededRotation = neededRotation;
        this.distToCheckpoint = distToCheckpoint;
        leftTurnPossibilities = new ArrayList<>();
        rightTurnPossibilities = new ArrayList<>();
        forwardPossibilities = new ArrayList<>();
        setOaringPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
    }

    private void setOaringPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        if (Gouvernail.ALLOWED_ROTATION.first < neededRotation && neededRotation < Gouvernail.ALLOWED_ROTATION.second)
            setForwardPossibilities(nbSailor, nbOarOnLeft + nbOarOnRight);
        else
            setTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
    }

    private void setForwardPossibilities(int nbSailor, int nbOarTotal) {
        for (int i = 2; i <= nbSailor; i+=2) {
            forwardPossibilities.add(new OarConfiguration(i/2, i/2, nbOarTotal));
        }
    }

    /**
     * Determine every angle of rotation possible according to the game parameters.
     * Then they are stocked into two maps, one for each side (right/left).
     *
     * @param nbSailor The number of sailor on the boat.
     * @param nbOarOnLeft The number of oar on the left side of the boat.
     * @param nbOarOnRight The number of oar on the right side of the boat.
     */
    private void setTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        if (neededRotation > 0)
            setLeftTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
        else
            setRightTurnPossibilities(nbSailor, nbOarOnLeft, nbOarOnRight);
    }

    private void setLeftTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        for (int rightNb = 0; (rightNb <= nbOarOnRight) && (rightNb <= nbSailor); rightNb++) {
            for (int leftNb = 0; (leftNb < rightNb) && (leftNb <= nbOarOnLeft) && (leftNb + rightNb <= nbSailor); leftNb++)
                leftTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, nbOarOnLeft + nbOarOnRight));
        }
    }

    private void setRightTurnPossibilities(int nbSailor, int nbOarOnLeft, int nbOarOnRight) {
        for (int leftNb = 0; (leftNb <= nbOarOnLeft) && (leftNb <= nbSailor); leftNb++) {
            for (int rightNb = 0; (rightNb < leftNb) && (rightNb <= nbOarOnRight) && (leftNb + rightNb <= nbSailor); rightNb++)
                rightTurnPossibilities.add(new OarConfiguration(leftNb, rightNb, nbOarOnLeft + nbOarOnRight));
        }
    }

    public OarConfiguration resolve() {
        return null;
    }
}