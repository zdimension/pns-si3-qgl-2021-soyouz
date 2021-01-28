package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private boolean isUsed;

    public OnboardEntity(@JsonProperty("x") int x,
                         @JsonProperty("y")int y) {
        this.x = x;
        this.y = y;
        this.isUsed = false;
    }

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

    /**
     * Getter.
     * @return the used state of the object.
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * Setter.
     * @param used the value to set the use state.
     */
    public void setUsed(boolean used) {
        isUsed = used;
    }
}
