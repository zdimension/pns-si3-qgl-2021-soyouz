package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Objects;

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

    public Pair<Integer,Integer> getPos(){return Pair.of(getX(),getY());}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardEntity that = (OnboardEntity) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
