package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

/**
 * The Deck of a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau}.
 */
public class Deck
{
    /**
     * Y axis
     */
    private int width;
    /**
     * X axis
     */
    private int length;

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
