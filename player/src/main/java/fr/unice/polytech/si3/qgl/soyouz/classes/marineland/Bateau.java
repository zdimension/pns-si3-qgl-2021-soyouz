package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;

public class Bateau
{
    private String type;
    private int life;
    private Position position;
    private String name;
    private Deck deck;
    private Entity[] entities;
    private Shape shape;

    public String getType()
    {
        return type;
    }

    public int getLife()
    {
        return life;
    }

    public Position getPosition()
    {
        return position;
    }

    public String getName()
    {
        return name;
    }

    public Deck getDeck()
    {
        return deck;
    }

    public Entity[] getEntities()
    {
        return entities.clone();
    }

    public Shape getShape()
    {
        return shape;
    }
}
