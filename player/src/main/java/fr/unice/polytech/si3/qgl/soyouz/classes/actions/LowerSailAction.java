package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;

/**
 * <p>
 * Lowering the sail action. Needs the {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} to be located on
 * a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile}.
 * </p>
 */
public class LowerSailAction extends GameAction{

    /**
     * Constructor.
     *
     * @param sailor A Sailor.
     */
    public LowerSailAction(Marin sailor) {
        super(sailor, Voile.class);
    }
}
