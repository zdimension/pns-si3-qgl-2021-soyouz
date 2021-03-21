package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Deck of a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau}.
 */
public class Deck
{
    private final int width;
    private final int length;

    /**
     * Constructor.
     *
     * @param width Y axis.
     * @param length X axis.
     */
    public Deck(@JsonProperty("width")int width,@JsonProperty("length") int length)
    {
        this.width = width;
        this.length = length;
    }

    /**
     * Getter.
     *
     * @return the Width of this.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Getter.
     *
     * @return the Length of this.
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString()
    {
        return "Deck{" +
            "width=" + width +
            ", length=" + length +
            '}';
    }
}
