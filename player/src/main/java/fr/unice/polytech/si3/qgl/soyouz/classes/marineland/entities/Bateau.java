package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;

public class Bateau extends Entity
{
    private int life;
    private Position position;
    private String name;
    private Deck deck;
    private OnboardEntity[] entities;
    private Shape shape;

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

    public int getNumberOar(){
        return 2;
    } //TODO

    public Shape getShape()
    {
        return shape;
    }
}
