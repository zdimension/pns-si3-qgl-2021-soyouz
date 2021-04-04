package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The wind effect.
 */
public class Wind
{
    @JsonAlias("direction")
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
     * Getter.
     *
     * @return the orientation of the wind.
     */
    public double getOrientation()
    {
        return orientation;
    }

    /**
     * Getter.
     *
     * @return the strength of the wind.
     */
    public double getStrength()
    {
        return strength;
    }
}
