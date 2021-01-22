package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

/**
 * Stock all current parameters to Play the next round.
 */
public class NextRoundParameters
{
    private Bateau ship;
    private Entity[] visibleEntities;

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
