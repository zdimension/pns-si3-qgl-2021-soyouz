package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

/**
 * Super Class for every entities on the deck.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Rame.class, name = "oar")
})
public abstract class OnboardEntity
{
    private int x;
    private int y;

    /**
     * Getter.
     * @return the abscissa of the object.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Getter.
     * @return the ordinate of the object.
     */
    public int getY()
    {
        return y;
    }
}
