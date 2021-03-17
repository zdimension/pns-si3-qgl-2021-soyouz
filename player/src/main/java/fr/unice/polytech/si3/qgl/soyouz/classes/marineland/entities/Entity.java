package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;

/**
 * Super Class of every Entities in the sea.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutreBateau.class, name = "ship"),
    @JsonSubTypes.Type(value = Wind.class, name = "wind")
    //TODO AJOUTER RECIF
})
public interface Entity
{
}
