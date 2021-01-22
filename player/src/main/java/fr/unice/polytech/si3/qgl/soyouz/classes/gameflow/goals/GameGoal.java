package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The GameMode.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "mode", defaultImpl = Void.class)
@JsonSubTypes({
    @Type(value = RegattaGoal.class, name = "REGATTA")
})
public abstract class GameGoal
{
}
