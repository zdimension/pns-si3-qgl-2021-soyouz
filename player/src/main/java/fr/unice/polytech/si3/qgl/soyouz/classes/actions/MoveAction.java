package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Optional;

/**
 * Action to move a Sailor.
 */
public class MoveAction extends GameAction{
    private int xdistance;
    private int ydistance;
    /**
     * Constructor.
     *
     * @param sailor The sailor that we want to move.
     * @param x Horizontal movement.
     * @param y Vertical movement.
     */
    public MoveAction(
        @JsonProperty("sailor") Marin sailor,
        @JsonProperty("xdistance") int x,
        @JsonProperty("ydistance") int y) {
        super(sailor, null);
        if (x + y <= 5) {
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
     * Getters.
     *
     * @return the new position of the sailor after moving him.
     */
    public Pair<Integer,Integer> newPos(){
        return Pair.of(getSailor().getPos().first+xdistance, getSailor().getPos().second+ydistance);
    }

    /**
     * Find if two Move Action are equals or not, only based on their translations.
     *
     * @param obj The other Move Action.
     * @return true if they are equals, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MoveAction))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        var move = (MoveAction) obj;
        return this.xdistance == move.getXDistance() && this.ydistance == move.getYDistance() ;
    }
}
