package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Optional;

@JsonTypeName("OAR")
public class OarAction extends GameAction
{
    public OarAction(Marin sailor) {
        super(sailor, Optional.of(Rame.class));
    }
}
