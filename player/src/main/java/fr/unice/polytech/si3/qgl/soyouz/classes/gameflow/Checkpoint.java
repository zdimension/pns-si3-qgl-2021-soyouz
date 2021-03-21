package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.ShapedEntity;

/**
 * Checkpoints : defined by a shape, a size and a position. Need to be crossed by the ship in
 * order to win.
 */
public class Checkpoint extends ShapedEntity
{
    /**
     * Constructor.
     *
     * @param position The position of the checkpoint.
     * @param shape The shape of the checkpoint.
     */
    public Checkpoint(@JsonProperty("position")Position position, @JsonProperty("shape")Shape shape)
    {
        super(position, shape);
    }
}
