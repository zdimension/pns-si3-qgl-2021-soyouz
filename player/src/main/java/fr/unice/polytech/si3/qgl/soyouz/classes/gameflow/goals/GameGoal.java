package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;

/**
 * The GameMode.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "mode", defaultImpl = Void.class)
@JsonSubTypes({
    @Type(value = RegattaGoal.class, name = "REGATTA")
})
public interface GameGoal
{
    /**
     * Getters.
     *
     * @return a new objective based on the current game mode.
     */
    @JsonIgnore
    RootObjective getObjective();
}
