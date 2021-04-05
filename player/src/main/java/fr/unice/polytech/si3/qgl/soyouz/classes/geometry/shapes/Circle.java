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
    public Circle(@JsonProperty("radius")double radius)
    {
        super(0, IntStream.range(0, 180)
        .mapToObj(angle -> {
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

    /*@Override
    public Stream<Point2d> getShell(Point2d observer)
    {
        var dist = observer.norm();
        var a = Math.asin(radius / dist);
        var b = observer.angle();
        return Stream.of(
            new Point2d(radius * -Math.sin(b - a), radius * Math.cos(b - a)),
            new Point2d(radius * Math.sin(b + a), radius * -Math.cos(b + a))
        );
    }*/
}
