package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.BoundingBox;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.stream.IntStream;

/**
 * Circle shape.
 */
public class Circle extends Polygon implements Shape
{
    private static final int VERTEX_COUNT = 16;
    private final double radius;
    private final double radiusSquared;
    private final double diameter;

    /**
     * Constructor.
     *
     * @param radius The radius of the circle.
     */
    public Circle(@JsonProperty("radius") double radius)
    {
        super(0, getPoints(radius), Pair.of(new BoundingBox(-radius, radius, -radius, radius),
            Point2d.ZERO));
        this.radius = radius;
        this.radiusSquared = radius * radius;
        this.diameter = radius * 2;
    }

    /**
     * Get a discretization of the circle.
     *
     * @param radius The radius of the Circle.
     * @return a list of points.
     */
    private static Point2d[] getPoints(double radius)
    {
        return IntStream.range(0, VERTEX_COUNT)
            .mapToObj(angle ->
            {
                var rad = 2 * angle * Math.PI / VERTEX_COUNT;
                return new Point2d(radius * Math.cos(rad), radius * Math.sin(rad));
            }).toArray(Point2d[]::new);
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

    /**
     * Determine if a point is inside the circle.
     *
     * @param pos The position of the point.
     * @return true if the circle contains the point, false otherwise.
     */
    @Override
    public boolean contains(Point2d pos)
    {
        return pos.normSquared() <= radiusSquared;
    }

    /**
     * Getters.
     *
     * @return the diameter of the circle.
     */
    @Override
    public double getMaxDiameter()
    {
        return diameter;
    }

    /**
     * Determine if a line is cutting through the circle.
     *
     * @param e A point frome the line.
     * @param l Another point from the line.
     * @param shipSize The size of the ship.
     * @return true if the line cross the circle, false otherwise.
     */
    @Override
    public boolean linePassesThrough(Point2d e, Point2d l, double shipSize)
    {
        var rad = radius + shipSize;
        var d = l.sub(e);

        double a = d.dot(d);
        double b = 2 * e.dot(d);
        double c = e.dot(e) - rad * rad;

        double discriminant = b * b - 4 * a * c;

        if (discriminant >= 0)
        {
            discriminant = Math.sqrt(discriminant);

            double t1 = (-b - discriminant) / (2 * a);

            if (t1 >= 0 && t1 <= 1)
            {
                return true;
            }

            double t2 = (-b + discriminant) / (2 * a);

            return t2 >= 0 && t2 <= 1;
        }

        return false;
    }

    /**
     * Getter.
     *
     * @param shipSize The size of the ship.
     * @return a list of relative point that compose the circle shape.
     */
    @Override
    protected Point2d[] getShellInternal(double shipSize)
    {
        return getPoints(radius + shipSize);
    }
}
