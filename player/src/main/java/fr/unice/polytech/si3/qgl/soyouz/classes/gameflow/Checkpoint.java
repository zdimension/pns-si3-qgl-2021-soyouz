package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;

/**
 * Checkpoints : defined by a shape, a size and a position. Need to be crossed by the ship in
 * order to win.
 */
public class Checkpoint
{
    private Position position;
    private Shape shape;

    public Checkpoint(@JsonProperty("position")Position position, @JsonProperty("shape")Shape shape)
    {
        this.position = position;
        this.shape = shape;
    }

    /**
     * Getter.
     *
     * @return the position of the checkpoint.
     */
    public Position getPosition()
    {
        return position;
    }

    /**
     * Getter.
     *
     * @return the shape of the checkpoint.
     */
    public Shape getShape()
    {
        return shape;
    }
}
