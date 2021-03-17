package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Circle shape.
 */
public class Circle implements Shape
{
    private double radius;

    public Circle(@JsonProperty("radius")double radius)
    {
        this.radius = radius;
    }

    /**
     * Getter.
     *
     * @return the radius of the circle.
     */
    public double getRadius()
    {
        return radius;
    }
}
