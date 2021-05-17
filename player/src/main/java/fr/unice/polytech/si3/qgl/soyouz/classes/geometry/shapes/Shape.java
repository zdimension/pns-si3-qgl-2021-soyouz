package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

import java.util.stream.Stream;

/**
 * SuperClass of every geometrics Shapes.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @Type(value = Circle.class, name = "circle"),
    @Type(value = Polygon.class, name = "polygon"),
    @Type(value = Rectangle.class, name = "rectangle")
})
public interface Shape
{
    /**
     * Determine if a point is inside the shape.
     *
     * @param p The point.
     * @return true if it is, false otherwise.
     */
    boolean contains(Point2d p);

    /**
     * Getter.
     *
     * @return an approximation of the diameter of the shape.
     */
    @JsonIgnore
    double getMaxDiameter();

    /**
     * Getter.
     *
     * @param shipSize The size of the ship.
     * @return a stream of points that compose the shell of the shape.
     */
    @JsonIgnore
    Stream<Point2d> getShell(double shipSize);

    /**
     * Determine if a line is cutting through the shape.
     *
     * @param a A point frome the line.
     * @param b Another point from the line.
     * @param shipSize The size of the ship.
     * @return true if the line cross the circle, false otherwise.
     */
    boolean linePassesThrough(Point2d a, Point2d b, double shipSize);
}
