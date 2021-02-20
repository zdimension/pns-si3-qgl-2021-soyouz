package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;

/**
 * <p>
 * Turning the {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail} action. Needs the
 * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} to be located on a rudder.
 * </p>
 */
public class TurnAction extends GameAction{
    private double rotation;
    /**
     * Constructor.
     *
     * @param sailor A Sailor.
     */
    protected TurnAction(Marin sailor, Double rotation) {
        super(sailor, Gouvernail.class);
        this.rotation = rotation;
    }
}
