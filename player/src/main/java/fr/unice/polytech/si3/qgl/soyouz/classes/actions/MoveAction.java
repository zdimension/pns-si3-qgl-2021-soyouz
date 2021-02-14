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
    //TODO
    public MoveAction(
        @JsonProperty("sailor") Marin sailor,
        @JsonProperty("xdistance") int x,
        @JsonProperty("ydistance") int y) {
        super(sailor, Optional.empty());
        if (x + y <= 5) {
            xdistance = x;
            ydistance = y;
        }
    }

    public int getXDistance()
    {
        return xdistance;
    }

    public int getYDistance()
    {
        return ydistance;
    }

    public Pair<Integer,Integer> newPos(Pair<Integer,Integer> oldSailorPosition){
        return Pair.of(oldSailorPosition.first+ xdistance, oldSailorPosition.second+ ydistance);
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
}
