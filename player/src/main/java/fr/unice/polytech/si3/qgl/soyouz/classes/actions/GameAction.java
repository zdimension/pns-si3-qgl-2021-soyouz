package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class GameAction
{
    private Marin sailor;

    public int getSailorId()
    {
        return sailor.getId();
    }

    Marin getSailor() {
        return sailor;
    }

    public GameAction(Marin sailor) {
        this.sailor = sailor;
    }
}
