package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class GameAction
{
    private int sailorId;

    public int getSailorId()
    {
        return sailorId;
    }
}
