package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

/**
 * The Deck of a boat.
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
     * @return the Width of the Deck.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Getter.
     *
     * @return the Length of the Deck.
     */
    public int getLength()
    {
        return length;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "width=" + width +
                ", length=" + length +
                '}';
    }
}
