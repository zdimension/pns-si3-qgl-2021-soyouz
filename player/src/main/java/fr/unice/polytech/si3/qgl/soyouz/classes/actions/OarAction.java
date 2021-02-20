package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Optional;

/**
 * <p>
 * Rowing action. Needs the {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} to be located on
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
    public OarAction(Marin sailor) {
        super(sailor, Rame.class);
    }

    @Override
    public String toString() {
        return "OarAction : sailor : "+getSailor().toString();
    }
}
