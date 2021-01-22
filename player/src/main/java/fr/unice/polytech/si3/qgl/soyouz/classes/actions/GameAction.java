package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;

import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class GameAction
{
    private Marin sailor;

    public final Optional<Class<? extends OnboardEntity>> entityNeeded;

    public int getSailorId()
    {
        return sailor.getId();
    }

    public Marin getSailor() {
        return sailor;
    }

    protected GameAction(Marin sailor,Optional<Class<? extends OnboardEntity>> ent) {
        this.sailor = sailor;
        this.entityNeeded = ent;
    }
}
