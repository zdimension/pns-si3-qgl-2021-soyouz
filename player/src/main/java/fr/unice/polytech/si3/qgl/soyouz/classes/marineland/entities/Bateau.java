package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;

import java.util.Optional;

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

    public OnboardEntity[] getEntities()
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

    /**
     *
     * @param xPos
     * @param yPos
     * @return optional entity on the given cell
     */
    public Optional<OnboardEntity> getEntityHere(int xPos, int yPos){
        for(OnboardEntity ent : entities)
            if(ent.getX() == xPos && ent.getY() == yPos)
                return Optional.of(ent);
        return Optional.empty();
    }

    public Optional<OnboardEntity> getEntityHere(Pair<Integer, Integer> pos){
        return getEntityHere(pos.getFirst(), pos.getSecond());
    }
}
