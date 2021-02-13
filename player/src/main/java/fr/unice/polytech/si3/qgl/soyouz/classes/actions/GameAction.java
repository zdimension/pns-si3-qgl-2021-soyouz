package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Basis class for all actions that can be performed on board.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveAction.class, name = "MOVING"),
    @JsonSubTypes.Type(value = OarAction.class, name = "OAR")
})
public abstract class GameAction
{
    @JsonIgnore
    private final Marin sailor;

    @JsonIgnore
    public final Class<? extends OnboardEntity> entityNeeded;

    /**
     * Getter.
     *
     * @return the attached Sailor Id.
     */
    public int getSailorId()
    {
        return sailor.getId();
    }

    /**
     * Getter.
     *
     * @return the attached Sailor.
     */
    public Marin getSailor() {
        return sailor;
    }

    public Class<? extends OnboardEntity> getEntityNeeded() {
        return entityNeeded;
    }

    /**
     * Constructor.
     *
     * @param sailor A Sailor.
     * @param ent The wanted entity to make the boat progress (Optional).
     */
    protected GameAction(Marin sailor,@Nullable Class<? extends OnboardEntity> ent) {
        this.sailor = sailor;
        this.entityNeeded = ent;
    }
}
