package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

import java.util.Optional;

/**
 * Action to move a Sailor.
 */
@JsonTypeName("MOVING")
public class MoveAction extends GameAction{

    //TODO
    public MoveAction(Marin sailor) {
        super(sailor, Optional.empty());
    }
}
