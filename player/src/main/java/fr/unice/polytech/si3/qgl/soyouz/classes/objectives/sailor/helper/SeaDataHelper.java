package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Wind;

import java.util.List;

public class SeaDataHelper
{
    private Bateau boat;
    private Entity[] visibleEntities;
    private Wind wind;

    public SeaDataHelper(Bateau boat,Entity[] visibleEntities, Wind wind)
    {
        this.boat = boat;
        this.visibleEntities = visibleEntities;
        this.wind = wind;
    }

    public Entity[] getVisibleEntities()
    {
        return visibleEntities;
    }

    public Wind getWind()
    {
        return wind;
    }
}
