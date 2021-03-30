package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

/**
 * A Helper that contains all data necessary, related to all sea entities.
 */
public class SeaDataHelper
{
    private Bateau ship;
    private Wind wind;
    private Entity[] visibleEntities;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param wind The wind.
     * @param visibleEntities all visible entities in the sea
     */
    public SeaDataHelper(Bateau ship ,Wind wind, Entity[] visibleEntities)
    {
        this.ship = ship;
        this.wind = wind;
        this.visibleEntities = visibleEntities;
    }

    /**
     * Update the helper.
     *
     * @param state The current game state.
     */
    public void update(GameState state)
    {
        this.wind = state.getNp().getWind();
        this.ship = state.getNp().getShip();
        this.visibleEntities = state.getNp().getVisibleEntities();
    }

    /**
     * Getter.
     *
     * @return the wind.
     */
    public Wind getWind()
    {
        return wind;
    }

    /**
     * Getter.
     *
     * @return the ship.
     */
    public Bateau getShip()
    {
        return ship;
    }

    /**
     * Getter
     * @return the visible sea entities
     */
    public Entity[] getVisibleEntities()
    {
        return visibleEntities;
    }

    //TODO : Need to verify if this works as intended
    /**
     * Determine if a reef is nearby our boat
     * @param boat
     * @return
     */
    public boolean isAReefNearby(Bateau boat){
        if (visibleEntities.length==0)
            return false;
        for (Entity entity : visibleEntities){
            if (entity instanceof Reef){
                Reef reef = (Reef) entity;
                Double distance = reef.getPosition().getLength(boat.getPosition());
                if (distance<200){
                    return true;
                }
            }
        }
        return false;
    }
}
