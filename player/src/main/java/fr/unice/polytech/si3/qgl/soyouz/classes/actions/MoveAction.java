package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * Action to move a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin}.
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
        Marin sailor,
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
     *
     * @return the new position of the sailor if moved according to this.
     */
    public Pair<Integer,Integer> newPos(){
        return Pair.of(getSailor().getPos().first+xdistance, getSailor().getPos().second+ydistance);
    }

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "MoveAction : x = "+xdistance+" y = "+ ydistance+" | sailor : "+getSailor().toString();
    }
}
