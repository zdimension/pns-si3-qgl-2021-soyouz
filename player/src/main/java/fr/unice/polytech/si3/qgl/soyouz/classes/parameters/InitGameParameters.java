package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.GameGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;

import java.util.Arrays;
import java.util.Optional;

/**
 * Stock all parameters given to init the Game.
 */
public class InitGameParameters
{
    private final GameGoal goal;
    private final Bateau ship;
    private final Marin[] sailors;
    private final int shipCount;

    /**
     * Construcor.
     *
     * @param goal The game goal.
     * @param ship The ship.
     * @param sailors The list of sailors on the ship.
     */
    public InitGameParameters(@JsonProperty("goal")GameGoal goal,
                              @JsonProperty("ship") Bateau ship,
                              @JsonProperty("sailors") Marin[] sailors)
    {
        this.goal = goal;
        this.ship = ship;
        this.sailors = sailors;
        this.shipCount = 1;
    }

    /**
     * Getter.
     *
     * @return the Goal of the game.
     */
    public GameGoal getGoal()
    {
        return goal;
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
     * @return our crew members.
     */
    public Marin[] getSailors()
    {
        return sailors.clone();
    }

    /**
     * Getter.
     *
     * @return the number of ships in competition.
     */
    public int getShipCount()
    {
        return shipCount;
    }

    /**
     * Getters.
     *
     * @param id The id of the wanted sailor.
     * @return the sailor attached to this id.
     */
    public Optional<Marin> getSailorById(int id)
    {
        return Arrays.stream(sailors).filter(m -> m.getId() == id).findFirst();
    }
}
