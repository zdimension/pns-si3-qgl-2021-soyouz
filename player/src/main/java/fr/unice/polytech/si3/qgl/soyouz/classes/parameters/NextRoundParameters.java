package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

public class NextRoundParameters
{
    private Bateau ship;
    private Entity[] visibleEntities;

    public Bateau getShip()
    {
        return ship;
    }

    public Entity[] getVisibleEntities()
    {
        return visibleEntities;
    }
}
