package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * Rudder entity.
 */
public class Gouvernail extends OnboardEntity
{

    public static final Pair<Double, Double> ALLOWED_ROTATION = Pair.of(-0.78539816339,
        0.78539816339);

    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    public Gouvernail(@JsonProperty("x") int x, @JsonProperty("y") int y)
    {
        super(x, y);
    }
}
