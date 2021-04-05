package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

/**
 * Circle shape.
 */
public class Circle implements Shape
{
    private final double radius;

    /**
     * Constructor.
     *
     * @param radius The radius of the circle.
     */
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

    @Override
    public boolean contains(Point2d pos)
    {
        return pos.normSquared() <= Math.pow(radius, 2);
    }

    @Override
    public double getMaxDiameter()
    {
        return radius * 2;
    }
}
