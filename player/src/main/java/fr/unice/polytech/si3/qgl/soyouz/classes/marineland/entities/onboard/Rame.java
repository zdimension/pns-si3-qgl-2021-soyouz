package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Oar entity.
 */
public class Rame extends OnboardEntity
{
    public Rame(@JsonProperty("x") int x,@JsonProperty("y") int y) {
        super(x, y);
    }
}
