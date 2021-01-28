package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

/**
 * Stock all current parameters to Play the next round.
 */
public class NextRoundParameters
{
    private final Bateau ship;
    private final Wind wind;
    private final Entity[] visibleEntities;

    public NextRoundParameters(@JsonProperty("ship") Bateau ship,
                               @JsonProperty("wind") Wind wind,
                               @JsonProperty("visibleEntities") Entity[] visibleEntities)
    {
        this.ship = ship;
        this.wind = wind;
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

    /**
     * Getter.
     * @return the wind if there is a blow this turn.
     */
    public Wind getWind() {
        return wind;
    }
}
