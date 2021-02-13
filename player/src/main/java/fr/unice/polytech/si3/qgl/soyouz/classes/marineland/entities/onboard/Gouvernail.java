package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rudder entity.
 */
public class Gouvernail extends OnboardEntity{
    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    public Gouvernail(@JsonProperty("x") int x,@JsonProperty("y") int y) {
        super(x, y);
    }
}
