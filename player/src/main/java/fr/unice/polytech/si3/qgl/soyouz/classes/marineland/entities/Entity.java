package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @Type(value = Rame.class, name = "oar")
})
public abstract class Entity
{
    private int x;
    private int y;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
