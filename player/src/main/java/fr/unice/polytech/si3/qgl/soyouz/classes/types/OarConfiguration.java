package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * Class to stock all data related to an oar configuration.
 */
public class OarConfiguration
{

    private final Pair<Integer, Integer> sailorConfiguration;
    private final double angleOfRotation;
    private final double linearSpeed;

    /**
     * Constructor.
     *
     * @param activeLeftOar  The number of rowers on the left.
     * @param activeRightOar The number of rowers on the right.
     * @param totalOarNb     The total number of oars on the boat.
     */
    public OarConfiguration(int activeLeftOar, int activeRightOar, int totalOarNb)
    {
        sailorConfiguration = Pair.of(activeLeftOar, activeRightOar);
        angleOfRotation = calculateAngleOfRotation(activeLeftOar, activeRightOar, totalOarNb);
        linearSpeed = calculateLinearSpeed(activeLeftOar + activeRightOar, totalOarNb);
    }

    /**
     * Determine the angle of rotation of a specific combination of oar.
     *
     * @param activeLeftOar  The number of active oar on the left side of the boat.
     * @param activeRightOar The number of active oar on the right side of the boat.
     * @param totalOarNb     The total number of oar.
     * @return the angle of the rotation.
     */
    private Double calculateAngleOfRotation(int activeLeftOar, int activeRightOar, int totalOarNb)
    {
        return Math.PI * (activeRightOar - activeLeftOar) / totalOarNb;
    }

    /**
     * Determine the orientation and the linear speed of the boat in a specific configuration.
     *
     * @param activeOarNb The number of active oar.
     * @param totalOarNb  The total number of oar.
     * @return a pair that contains all the information.
     */
    private double calculateLinearSpeed(int activeOarNb, int totalOarNb)
    {
        return (165.0 * activeOarNb / totalOarNb);
    }

    /**
     * Getters.
     *
     * @return the pair of rowers (left, right).
     */
    public Pair<Integer, Integer> getSailorConfiguration()
    {
        return this.sailorConfiguration;
    }

    /**
     * Getters.
     *
     * @return the angle in rad associated to the rowers configuration.
     */
    public double getAngleOfRotation()
    {
        return this.angleOfRotation;
    }

    /**
     * Getters.
     *
     * @return the speed associated to the rowers configuration.
     */
    public double getLinearSpeed()
    {
        return this.linearSpeed;
    }
}
