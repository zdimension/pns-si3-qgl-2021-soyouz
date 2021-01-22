package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;

import java.util.Optional;

/**
 * Basis class for all actions that can be performed on board.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class GameAction
{
    private Marin sailor;

    public final Optional<Class<? extends OnboardEntity>> entityNeeded;

    /**
     * Getter.
     * @return the attached Sailor Id.
     */
    public int getSailorId()
    {
        return sailor.getId();
    }

    /**
     * Getter.
     * @return the attached Sailor.
     */
    public Marin getSailor() {
        return sailor;
    }

    /**
     * Constructor.
     * @param sailor A Sailor.
     * @param ent The wanted entity to make the boat progress (Optional).
     */
    protected GameAction(Marin sailor,Optional<Class<? extends OnboardEntity>> ent) {
        this.sailor = sailor;
        this.entityNeeded = ent;
    }
}
