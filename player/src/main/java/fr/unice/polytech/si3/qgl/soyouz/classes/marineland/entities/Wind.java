package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The wind effect.
 */
public class Wind implements Entity
{
    private final double orientation;
    private final double strength;

    /**
     * Constructor.
     *
     * @param orientation The orientation of the blow.
     * @param strength The strength of the blow.
     */
    public Wind(@JsonProperty("orientation") double orientation, @JsonProperty("strength") double strength)
    {
        this.orientation = orientation;
        this.strength = strength;
    }

    /**
     * Determine how much speed will the wind add to the boat.
     *
     * @param nbOfSails   The number of sails.
     * @param openedSails The number of opened sails.
     * @param boat        Out boat.
     * @return the speed added by the wind.
     */
    public double windAdditionalSpeed(int nbOfSails, int openedSails, Bateau boat)
    {
        if (nbOfSails > 0)
        {
            return ((double) openedSails / nbOfSails) * strength * Math.cos(orientation - boat.getPosition().getOrientation());
        }
        return 0;
    }
}
