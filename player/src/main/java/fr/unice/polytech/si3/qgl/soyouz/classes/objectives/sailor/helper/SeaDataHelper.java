package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * A Helper that contains all data necessary, related to all sea entities.
 */
public class SeaDataHelper
{
    public final static int TURNS_BEFORE_WATCH = 5;
    private Bateau ship;
    private Wind wind;
    private ShapedEntity[] visibleEntities;
    private Position lastWatchPos;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param wind The wind.
     * @param visibleEntities all visible entities in the sea
     */
    public SeaDataHelper(Bateau ship ,Wind wind, ShapedEntity[] visibleEntities)
    {
        this.ship = ship;
        this.wind = wind;
        this.visibleEntities = visibleEntities;
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
        this.visibleEntities = state.getNp().getVisibleEntities();
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
     * @return The position where the last Watch action was thrown.
     */
    public Position getLastWatchPos()
    {
        return lastWatchPos;
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
    public ShapedEntity[] getVisibleEntities()
    {
        return visibleEntities;
    }
}
