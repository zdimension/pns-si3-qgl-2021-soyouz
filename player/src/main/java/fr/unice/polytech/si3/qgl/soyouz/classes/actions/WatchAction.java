package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import org.jetbrains.annotations.Nullable;

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
