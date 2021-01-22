package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Optional;

/**
 * Rowing action. Need a Sailor on a oar to be done.
 */
@JsonTypeName("OAR")
public class OarAction extends GameAction
{
    /**
     * Constructor.
     * @param sailor The needed Sailor to perform the action.
     */
    public OarAction(Marin sailor) {
        super(sailor, Optional.of(Rame.class));
    }
}
