package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Stock all current parameters to Play the next round.
 */
public class NextRoundParameters
{
    private final Bateau ship;
    private final Wind wind;
    private final ShapedEntity[] visibleEntities;

    /**
     * Constructor.
     *
     * @param ship            The current state of the ship.
     * @param wind            The current state of the wind.
     * @param visibleEntities The current state of onboard entities.
     */
    public NextRoundParameters(@JsonProperty("ship") Bateau ship,
                               @JsonProperty("wind") Wind wind,
                               @JsonProperty("visibleEntities") ShapedEntity[] visibleEntities)
    {
        this.ship = ship;
        this.wind = wind;
        this.visibleEntities = visibleEntities;
    }

    /**
     * Getter.
     *
     * @return our Ship.
     */
    public Bateau getShip()
    {
        return ship;
    }

    /**
     * Getter.
     *
     * @return all Entities around and close enough to be seen.
     */
    public ShapedEntity[] getVisibleEntities()
    {
        return visibleEntities;
    }

    public Stream<Reef> getReef()
    {
        return Util.filterType(Arrays.stream(visibleEntities), Reef.class);
    }

    public Stream<ShapedEntity> getVisibleShapes()
    {
        return Arrays.stream(visibleEntities);
    }

    /**
     * Getter.
     *
     * @return the wind if there is a blow this turn.
     */
    public Wind getWind()
    {
        return wind;
    }
}
