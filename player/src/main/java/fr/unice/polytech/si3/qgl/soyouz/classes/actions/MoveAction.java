package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

import java.util.Optional;

/**
 * Action to move a Sailor.
 */
public class MoveAction extends GameAction{
    private int xdistance;
    private int ydistance;
    //TODO
    public MoveAction(Marin sailor) {
        super(sailor, Optional.empty());
    }

    public int getXdistance()
    {
        return xdistance;
    }

    public int getYdistance()
    {
        return ydistance;
    }
}
