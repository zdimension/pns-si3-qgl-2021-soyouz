package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.checkpoint;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

public class OarConfiguration {

    Pair<Integer, Integer> sailorConfiguration;
    double angleOfRotation;
    double linearSpeed;

    public OarConfiguration(int activeLeftOar, int activeRightOar, int totalOarNb) {
        sailorConfiguration = Pair.of(activeLeftOar, activeRightOar);
        angleOfRotation = calculateAngleOfRotation(activeLeftOar, activeRightOar, totalOarNb);
        linearSpeed = calculateLinearSpeed(activeLeftOar + activeRightOar, totalOarNb);
    }

    /**
     * Determine the angle of rotation of a specific combination of oar.
     *
     * @param activeLeftOar The number of active oar on the left side of the boat.
     * @param activeRightOar The number of active oar on the right side of the boat.
     * @param totalOarNb The total number of oar.
     * @return the angle of the rotation.
     */
    private Double calculateAngleOfRotation(int activeLeftOar, int activeRightOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }

    /**
     * Determine the orientation and the linear speed of the boat in a specific configuration.
     *
     * @param activeOarNb The number of active oar.
     * @param totalOarNb The total number of oar.
     * @return a pair that contains all the information.
     */
    static Double calculateLinearSpeed(int activeOarNb, int totalOarNb){
        return (165.0 * activeOarNb / totalOarNb);
    }

    public Pair<Integer, Integer> getSailorConfiguration() {
        return this.sailorConfiguration;
    }

    public double getAngleOfRotation() {
        return this.angleOfRotation;
    }

    public double getLinearSpeed() {
        return this.linearSpeed;
    }
}
