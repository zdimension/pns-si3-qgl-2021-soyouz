package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;

/**
 * <p>
 * Using the
 * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie} action
 * . Needs the
 * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} to be located on a Watch.
 * </p>
 */
public class WatchAction extends GameAction
{

    /**
     * Constructor.
     *
     * @param sailor A Sailor.
     */
    public WatchAction(@JsonProperty("sailorId") Marin sailor)
    {
        super(sailor, Vigie.class);
    }
}
