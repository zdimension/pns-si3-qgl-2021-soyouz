package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * A Helper that contains all data necessary, related to all sea entities.
 */
public class SeaDataHelper
{
    private Bateau ship;
    private Wind wind;
    private Position lastWatchPos;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param wind The wind.
     */
    public SeaDataHelper(Bateau ship, Wind wind)
    {
        this.ship = ship;
        this.wind = wind;
        lastWatchPos = ship.getPosition();
    }

    /**
     * Update the helper.
     *
     * @param state The current game state.
     */
    public void update(GameState state)
    {
        trace();
        this.wind = state.getNp().getWind();
        this.ship = state.getNp().getShip();
    }

    /**
     * Getter.
     *
     * @return The position where the last Watch action was thrown.
     */
    public Position getLastWatchPos()
    {
        return lastWatchPos;
    }

    /**
     * Setter.
     *
     * @param pos The position where a Watch action occured.
     */
    public void setLastWatchPos(Position pos)
    {
        lastWatchPos = pos;
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

}
