package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

import java.util.Objects;

/**
 * Super Class for every entities on the deck.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Rame.class, name = "oar"),
    @JsonSubTypes.Type(value = Gouvernail.class, name = "rudder"),
    @JsonSubTypes.Type(value = Voile.class, name = "sail"),
    @JsonSubTypes.Type(value = Vigie.class, name = "watch")
})
public abstract class OnboardEntity
{
    protected int x;
    protected int y;

    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    protected OnboardEntity(@JsonProperty("x") int x,
                            @JsonProperty("y") int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter.
     *
     * @return the abscissa of the object.
     */
    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Getter.
     *
     * @return the ordinate of the object.
     */
    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * Getters.
     *
     * @return the position of the entity.
     */
    public PosOnShip getPos()
    {
        return new PosOnShip(getX(), getY());
    }

    /**
     * Method to determine if two entities are equals.
     *
     * @param o The second entity.
     * @return true if they are equals, false otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardEntity that = (OnboardEntity) o;
        return x == that.x && y == that.y;
    }

    /**
     * Method to create a unique code to identify the entity.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }
}
