package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Optional;

/**
 * Action to move a Sailor.
 */
public class MoveAction extends GameAction{
    private int xDistance;
    private int yDistance;
    //TODO
    public MoveAction(Marin sailor, int x, int y) {
        super(sailor, Optional.empty());
        if (x + y <= 5) {
            xDistance = x;
            yDistance = y;
        }
    }

    public int getXDistance()
    {
        return xDistance;
    }

    public int getYDistance()
    {
        return yDistance;
    }

    public Pair<Integer,Integer> newPos(Pair<Integer,Integer> oldSailorPosition){
        return Pair.of(oldSailorPosition.first+xDistance, oldSailorPosition.second+yDistance);
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
        return this.xDistance == move.getXDistance() && this.yDistance == move.getYDistance() ;
    }
}
