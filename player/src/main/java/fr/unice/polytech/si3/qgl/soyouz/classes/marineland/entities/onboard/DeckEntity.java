package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class DeckEntity extends OnboardEntity
{
    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    protected DeckEntity(int x, int y)
    {
        super(x, y);
    }

    @JsonIgnore
    public char getChar()
    {
        return 'E';
    }
}
