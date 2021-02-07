package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

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
}
