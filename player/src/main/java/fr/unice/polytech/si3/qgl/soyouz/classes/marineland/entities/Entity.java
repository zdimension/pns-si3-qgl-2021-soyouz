package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Bateau.class, name = "ship")
})
public abstract class Entity
{
}
