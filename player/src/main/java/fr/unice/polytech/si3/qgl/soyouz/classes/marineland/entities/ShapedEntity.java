package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutreBateau.class, name = "ship"),
    @JsonSubTypes.Type(value = Stream.class, name = "stream"),
    @JsonSubTypes.Type(value = Reef.class, name = "reef")
})
public abstract class ShapedEntity
{
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
        return pos.sub(position).rotate(-position.getOrientation() - Math.PI / 2);
    }

    public Point2d toGlobal(Point2d pos)
    {
        return pos.rotate(Math.PI / 2 + position.getOrientation()).add(position);
    }

    public boolean contains(Position pos)
    {
        return shape.contains(toLocal(pos));
    }

    public java.util.stream.Stream<Point2d> getShell(Position observer, double shipSize)
    {
        return shape.getShell(toLocal(observer)).map(pos -> toGlobal(pos.add(Point2d.fromPolar(shipSize, pos.angle()))));
    }
}
