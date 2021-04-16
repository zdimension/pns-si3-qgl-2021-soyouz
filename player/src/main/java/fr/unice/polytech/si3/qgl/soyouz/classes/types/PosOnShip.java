package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Objects;

/**
 * PosOnShip replaces the Pair &lt Integer, Integer &gt type when used to describe a position on the
 * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck} for readability
 */
public class PosOnShip
{

    private final int x;
    private final int y;

    /**
     * Constructor.
     *
     * @param x The abscissa.
     * @param y The ordinate.
     */
    public PosOnShip(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor.
     *
     * @param ent An entity.
     */
    public PosOnShip(OnboardEntity ent)
    {
        this.x = ent.getX();
        this.y = ent.getY();
    }

    public static PosOnShip of(int x, int y)
    {
        return new PosOnShip(x, y);
    }

    public int getFirst()
    {
        return getX();
    }

    public int getSecond()
    {
        return getY();
    }

    /**
     * Getter.
     *
     * @return the Abscissa.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Getter.
     *
     * @return the Ordinate.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Determine the distance between two positions.
     *
     * @param pos2 The second position.
     * @return a distance.
     */
    public int dist(PosOnShip pos2)
    {
        return Math.abs(x - pos2.getX()) + Math.abs(y - pos2.getY());
    }

    public int norm()
    {
        return Math.abs(x) + Math.abs(y);
    }

    /**
     * Generic toString override.
     *
     * @return a string describing the object.
     */
    @Override
    public String toString()
    {
        return "PosOnShip{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }

    /**
     * Generic equals override.
     *
     * @param obj The second object.
     * @return if they are equals or not.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PosOnShip))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        var pos = (PosOnShip) obj;
        return this.x == pos.x && this.y == pos.y;
    }

    /**
     * Generic hash override.
     *
     * @return the code related to the current object.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    public PosOnShip add(PosOnShip delta)
    {
        return new PosOnShip(x + delta.x, y + delta.y);
    }
}
