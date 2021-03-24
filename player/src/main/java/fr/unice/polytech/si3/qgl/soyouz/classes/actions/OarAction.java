package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

/**
 * <p>
 * Rowing action. Needs the {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} to
 * be located on
 * a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame}.
 * </p>
 */
public class OarAction extends GameAction
{
    /**
     * Constructor.
     *
     * @param sailor to perform the action.
     */
    public OarAction(@JsonProperty("sailorId") Marin sailor)
    {
        super(sailor, Rame.class);
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString()
    {
        return "OarAction : sailor : " + getSailor().toString();
    }
}
