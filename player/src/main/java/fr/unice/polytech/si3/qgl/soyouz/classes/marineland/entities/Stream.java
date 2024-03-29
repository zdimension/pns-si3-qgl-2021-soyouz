package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;

public class Stream extends ShapedEntity implements Entity
{
    private final double strength;
    private final Position projectedStrength;

    /**
     * Constructor.
     *
     * @param position The position of the stream.
     * @param shape The shape of the stream.
     * @param strength The strength of the stream.
     */
    @JsonCreator
    public Stream(
        @JsonProperty("position") Position position,
        @JsonProperty("shape") Shape shape,
        @JsonProperty("strength") double strength
    )
    {
        super(position, shape);
        this.strength = strength;
        this.projectedStrength = Point2d.fromPolar(strength, getPosition().getOrientation());
    }

    /**
     * Getter.
     *
     * @return the strength of the stream.
     */
    public double getStrength()
    {
        return strength;
    }

    /**
     * Getter.
     *
     * @return the projected strength on the boat.
     */
    @JsonIgnore
    public Position getProjectedStrength()
    {
        return projectedStrength;
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && obj instanceof Stream && ((Stream) obj).strength == strength;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
