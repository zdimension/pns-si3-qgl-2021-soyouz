package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Optional;

/**
 * Action to move a Sailor.
 */
public class MoveAction extends GameAction{
    private int xDistance;
    private int yDistance;

    /**
     * Constructor.
     *
     * @param sailor The sailor that we want to move.
     * @param x Horizontal movement.
     * @param y Vertical movement.
     */
    //TODO
    public MoveAction(Marin sailor, int x, int y) {
        super(sailor, Optional.empty());
        if (x + y <= 5) {
            xDistance = x;
            yDistance = y;
        }
    }

    /**
     * Getters.
     *
     * @return the X number of horizontal cells to cross.
     */
    public int getXDistance()
    {
        return xDistance;
    }

    /**
     * Getters.
     *
     * @return the Y number of vertical cells to cross.
     */
    public int getYDistance()
    {
        return yDistance;
    }

    /**
     * Getters.
     *
     * @return the new position of the sailor after moving him.
     */
    public Pair<Integer,Integer> newPos(){
        return Pair.of(getSailor().getPos().first+xDistance, getSailor().getPos().second+yDistance);
    }

    /**
     * Method to find if two Move Action are equals or not, only based on their translations.
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
        return this.xDistance == move.getXDistance() && this.yDistance == move.getYDistance() ;
    }
}
