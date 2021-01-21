package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

@JsonSubTypes({
    @JsonSubTypes.Type(value = Rame.class, name = "oar")
})
public abstract class OnboardEntity extends Entity
{
    private int x;
    private int y;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
