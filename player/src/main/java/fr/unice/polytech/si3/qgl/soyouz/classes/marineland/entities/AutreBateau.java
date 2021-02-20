package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Arrays;
import java.util.Optional;

/**
 * Ship entity.
 */
public class AutreBateau extends Entity
{
    private int life;
    private Position position;
    private Shape shape;

    /**
     * Getter.
     *
     * @return the health of the ship.
     */
    public int getLife()
    {
        return life;
    }

    /**
     * Getter.
     *
     * @return the position of the ship.
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
     * @return the Shape of the Boat.
     */
    public Shape getShape()
    {
        return shape;
    }
}
