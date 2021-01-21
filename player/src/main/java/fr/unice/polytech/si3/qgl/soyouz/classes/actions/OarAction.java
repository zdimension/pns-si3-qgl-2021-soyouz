package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

@JsonTypeName("OAR")
public class OarAction extends GameAction
{
    public OarAction(Marin sailor) {
        super(sailor);
    }
}
