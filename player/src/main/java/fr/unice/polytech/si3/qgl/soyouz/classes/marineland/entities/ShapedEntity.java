package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

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

    protected ShapedEntity()
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

    public void setShape(Shape shape)
    {
        this.shape = shape;
    }

    public Point2d toLocal(Point2d pos)
    {
        return pos.sub(position).rotate(-position.getOrientation());
    }

    public Point2d toGlobal(Point2d pos)
    {
        return pos.rotate(position.getOrientation()).add(position);
    }

    public boolean contains(Position pos)
    {
        return shape.contains(toLocal(pos));
    }

    public java.util.stream.Stream<Point2d> getShell()
    {
        return Arrays.stream(shellCache.computeIfAbsent(position,
            pos -> shape.getShell(80).map(this::toGlobal).toArray(Point2d[]::new)));
    }

    public boolean equals(Object obj)
    {
        return obj instanceof ShapedEntity
            && Objects.equals(((ShapedEntity) obj).position, position)
            && Objects.equals(((ShapedEntity) obj).shape, shape);
    }
}
