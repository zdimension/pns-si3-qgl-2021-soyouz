package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Super Class of every Entities in the sea.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutreBateau.class, name = "ship"),
    @JsonSubTypes.Type(value = Wind.class, name = "wind"),
    @JsonSubTypes.Type(value = Stream.class, name = "stream"),
    @JsonSubTypes.Type(value = Reef.class, name = "reef")
    //TODO AJOUTER RECIF
})
public interface Entity
{
}
