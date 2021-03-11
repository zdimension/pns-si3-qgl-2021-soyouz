package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Point defined by its abscissa x and ordinate y.
 */
public class Point
{
    private double x;
    private double y;

    public Point(@JsonProperty("x")double x, @JsonProperty("y")double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter.
     *
     * @return the abscissa of the Point.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Getter.
     *
     * @return the ordinate of the Point.
     */
    public double getY()
    {
        return y;
    }
}
