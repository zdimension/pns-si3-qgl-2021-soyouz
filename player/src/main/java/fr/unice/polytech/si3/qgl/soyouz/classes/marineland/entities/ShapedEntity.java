package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutreBateau.class, name = "ship"),
    @JsonSubTypes.Type(value = Stream.class, name = "stream"),
    @JsonSubTypes.Type(value = Reef.class, name = "reef")
})
public abstract class ShapedEntity
{
    private final Map<Position, Point2d[]> shellCache = new HashMap<>();
    private Position position;
    private Shape shape;

    ShapedEntity()
    {

    }

    /**
     * Constructor.
     *
     * @param position The position of the entity.
     * @param shape    The shape of the entity.
     */
    protected ShapedEntity(Position position, Shape shape)
    {
        this.position = position;
        this.shape = shape;
    }

    /**
     * Getter.
     *
     * @return the position of the entity.
     */
    public Position getPosition()
    {
        return position;
    }

    /**
     * Setters.
     *
     * @param position to set.
     */
    public void setPosition(Position position)
    {
        this.position = position;
    }

    /**
     * Getter.
     *
     * @return the shape of the entity.
     */
    public Shape getShape()
    {
        return shape;
    }

    /**
     * Setters.
     *
     * @param shape The shape to set.
     */
    public void setShape(Shape shape)
    {
        this.shape = shape;
    }

    /**
     * Transform a global point to a local point.
     *
     * @param pos The point.
     * @return the point in the local dimension.
     */
    public Point2d toLocal(Point2d pos)
    {
        return pos.sub(position).rotate(-position.getOrientation());
    }

    /**
     * Transform a local point to a global point.
     *
     * @param pos The point.
     * @return the point in the global dimension.
     */
    private Point2d toGlobal(Point2d pos)
    {
        return pos.rotate(position.getOrientation()).add(position);
    }

    /**
     * Determine if the shape contains a specific position.
     *
     * @param pos The position.
     * @return true if the position is contained by the shape, false otherwise.
     */
    public boolean contains(Position pos)
    {
        return shape.contains(toLocal(pos));
    }

    /**
     * Getter.
     *
     * @return a stream containing all the point that compose the shell of the shape.
     */
    @JsonIgnore
    public java.util.stream.Stream<Point2d> getShell()
    {
        return Arrays.stream(shellCache.computeIfAbsent(position,
            pos -> shape.getShell(80).map(this::toGlobal).toArray(Point2d[]::new)));
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof ShapedEntity
            && Objects.equals(((ShapedEntity) obj).position, position)
            && Objects.equals(((ShapedEntity) obj).shape, shape);
    }
}
