package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rudder entity.
 */
public class Gouvernail extends DeckEntity
{
    public static final double ALLOWED_ROTATION = Math.PI / 4;

    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    public Gouvernail(@JsonProperty("x") int x, @JsonProperty("y") int y)
    {
        super(x, y);
    }

    public static boolean isValid(double rotation)
    {
        return Math.abs(rotation) <= ALLOWED_ROTATION;
    }

    @Override
    public char getChar()
    {
        return 'G';
    }
}
