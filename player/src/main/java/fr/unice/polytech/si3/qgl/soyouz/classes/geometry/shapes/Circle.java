package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Circle shape.
 */
public class Circle extends Polygon implements Shape
{
    private final double radius;

    /**
     * Constructor.
     *
     * @param radius The radius of the circle.
     */
    public Circle(@JsonProperty("radius") double radius)
    {
        super(0, IntStream.range(0, 180)
            .mapToObj(angle ->
            {
                var rad = 2 * angle * Math.PI / 180d;
                return new Point2d(radius * Math.cos(rad), radius * Math.sin(rad));
            }).toArray(Point2d[]::new));
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

    @Override
    public Stream<Point2d> getShell(Point2d observer)
    {
        var dist = observer.norm();
        if (dist <= radius)
            return Stream.empty();
        var a = Math.asin(radius / dist);
        var b = observer.angle();
        return Stream.of(
            new Point2d(radius * -Math.sin(b - a), radius * Math.cos(b - a)),
            new Point2d(radius * Math.sin(b + a), radius * -Math.cos(b + a))
        );
    }

    @Override
    public boolean linePassesThrough(Point2d e, Point2d l)
    {
        var d = l.sub(e);

        double a = d.dot(d);
        double b = 2 * e.dot(d);
        double c = e.dot(e) - radius * radius;

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

            if (t2 >= 0 && t2 <= 1)
            {
                return true;
            }
        }

        return false;
    }
}
