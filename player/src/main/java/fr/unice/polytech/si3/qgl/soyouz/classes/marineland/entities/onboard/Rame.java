package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Oar entity.
 */
public class Rame extends DeckEntity
{
    private final boolean isLeft;

    /**
     * Constructor.
     *
     * @param x the abscissa of the oar on the deck.
     * @param y the ordinate of the oar on the deck.
     */
    public Rame(@JsonProperty("x") int x, @JsonProperty("y") int y)
    {
        super(x, y);
        isLeft = y == 0;
    }

    /**
     * Getter.
     *
     * @return if the oar is on the left side of the boat.
     */
    public boolean isLeft()
    {
        return isLeft;
    }

    /**
     * Generic equals override.
     *
     * @param o The second entity.
     * @return if the entities are equal or not.
     */
    @Override
    public boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Generic hash override.
     *
     * @return the code associated to the current object.
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public char getChar()
    {
        return 'R';
    }
}
