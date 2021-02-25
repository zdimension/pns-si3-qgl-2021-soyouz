package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;

public class LowerSailAction extends GameAction{
    /**
     * Constructor.
     *
     * @param sailor A Sailor.
     *
     */
    protected LowerSailAction(Marin sailor) {
        super(sailor, Voile.class);
    }
}
