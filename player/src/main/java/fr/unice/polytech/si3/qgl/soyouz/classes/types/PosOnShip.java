package fr.unice.polytech.si3.qgl.soyouz.classes.types;

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

    public PosOnShip(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public PosOnShip(Pair<Integer, Integer> pos)
    {
        this.x = pos.first;
        this.y = pos.second;
    }

    public PosOnShip(OnboardEntity ent)
    {
        this.x = ent.getX();
        this.y = ent.getY();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public Pair<Integer, Integer> getPosCoord()
    {
        return Pair.of(getX(), getY());
    }

    @Override
    public String toString()
    {
        return "PosOnShip{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }

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

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }
}
