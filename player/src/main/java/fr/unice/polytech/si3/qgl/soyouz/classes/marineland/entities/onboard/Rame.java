package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Oar entity.
 */
public class Rame extends OnboardEntity
{
    /**
     * Constructor.
     *
     * @param x the abscissa of the oar on the deck.
     * @param y the ordinate of the oar on the deck.
     */
    public Rame(@JsonProperty("x") int x,@JsonProperty("y") int y) {
        super(x, y);
    }
}
