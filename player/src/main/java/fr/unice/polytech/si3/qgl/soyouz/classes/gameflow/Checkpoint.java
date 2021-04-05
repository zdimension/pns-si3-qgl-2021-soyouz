package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;

/**
 * Checkpoints : defined by a shape, a size and a position. Need to be crossed by the ship in
 * order to win.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Checkpoint extends ShapedEntity
{
    /**
     * Constructor.
     *
     * @param position The position of the checkpoint.
     * @param shape The shape of the checkpoint.
     */
    @JsonCreator
    public Checkpoint(@JsonProperty("position")Position position, @JsonProperty("shape")Shape shape, @JsonProperty("type") String type)
    {
        super(position, shape);
    }

    public Checkpoint(Position position, Shape shape)
    {
        this(position, shape, "checkpoint");
    }
}
