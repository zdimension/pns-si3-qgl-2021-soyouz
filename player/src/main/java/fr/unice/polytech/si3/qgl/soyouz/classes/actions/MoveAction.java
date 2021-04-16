package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

/**
 * Action to move a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin}.
 */
public class MoveAction extends GameAction
{
    private int xdistance;
    private int ydistance;

    /**
     * Constructor.
     *
     * @param sailor The sailor that we want to move.
     * @param x      Horizontal movement.
     * @param y      Vertical movement.
     */
    public MoveAction(
        @JsonProperty("sailorId") Marin sailor,
        @JsonProperty("xdistance") int x,
        @JsonProperty("ydistance") int y)
    {
        super(sailor, null);
        if (x + y <= 5)
        {
            xdistance = x;
            ydistance = y;
        }
    }

    /**
     * Getters.
     *
     * @return the X number of horizontal cells to cross.
     */
    public int getXDistance()
    {
        return xdistance;
    }

    /**
     * Getters.
     *
     * @return the Y number of vertical cells to cross.
     */
    public int getYDistance()
    {
        return ydistance;
    }

    /**
     * Determine the sailor's position after a potential movement.
     *
     * @return the new position of the sailor if moved according to this.
     */
    public PosOnShip newPos()
    {
        return getSailor().getPos().add(getDelta());
    }

    public PosOnShip getDelta()
    {
        return new PosOnShip(xdistance, ydistance);
    }

    /**
     * Generic equals method override.
     *
     * @param obj Another object.
     * @return true if this and obj are equals, false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MoveAction))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        var move = (MoveAction) obj;
        return this.xdistance == move.getXDistance() && this.ydistance == move.getYDistance();
    }

    /**
     * Generic hash method override.
     *
     * @return the hashcode linked to the current object.
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString()
    {
        return "MoveAction : x = " + xdistance + " y = " + ydistance + " | sailor : " + getSailor().toString();
    }
}
