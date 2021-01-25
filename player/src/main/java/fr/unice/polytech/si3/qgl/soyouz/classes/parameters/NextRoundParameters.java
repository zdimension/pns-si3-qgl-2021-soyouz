package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

/**
 * Stock all current parameters to Play the next round.
 */
public class NextRoundParameters
{
    private final Bateau ship;
    private final Entity[] visibleEntities;

    public NextRoundParameters(@JsonProperty("ship") Bateau ship,
                               @JsonProperty("visibleEntities") Entity[] visibleEntities)
    {
        this.ship = ship;
        this.visibleEntities = visibleEntities;
    }

    /**
     * Getter.
     * @return our Ship.
     */
    public Bateau getShip()
    {
        return ship;
    }

    /**
     * Getter.
     * @return all Entities around and close enough to be seen.
     */
    public Entity[] getVisibleEntities()
    {
        return visibleEntities;
    }
}
