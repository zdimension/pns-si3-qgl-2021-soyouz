package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

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
    boolean contains(Point2d p);

    double getMaxDiameter();
}
