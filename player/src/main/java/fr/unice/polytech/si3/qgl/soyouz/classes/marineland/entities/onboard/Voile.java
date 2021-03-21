package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sail entity.
 */
public class Voile extends OnboardEntity
{

    private boolean openned;


    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    public Voile(@JsonProperty("x") int x, @JsonProperty("y") int y)
    {
        super(x, y);
        this.openned = false;
    }


    /**
     * Getters.
     *
     * @return true if opened, false otherwise.
     */
    public boolean isOpenned()
    {
        return openned;
    }

    /**
     * Setters.
     *
     * @param openned True if opened, false if closed.
     */
    public void setOpenned(boolean openned)
    {
        this.openned = openned;
    }

    /**
     * Generic Equals method override.
     *
     * @param o The second entity.
     * @return true if the two objects are equals, false otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Generic hash method override.
     *
     * @return the hash code associated to the current object.
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
