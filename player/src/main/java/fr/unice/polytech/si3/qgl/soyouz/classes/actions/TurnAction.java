package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public TurnAction(Marin sailor, @JsonProperty("rotation") Double rotation) {
        super(sailor, Gouvernail.class);
        this.rotation = rotation;
    }

    /**
     * Getters.
     *
     * @return the rotation associated to this action.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString() {
        return "TurnAction : rotation = "+rotation+" | sailor : "+getSailor().toString();
    }
}
