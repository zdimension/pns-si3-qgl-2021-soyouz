package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Arrays;
import java.util.Optional;

/**
 * Ship entity.
 */
public class Bateau extends Entity
{
    private int life;
    private Position position;
    private String name;
    private Deck deck;
    private OnboardEntity[] entities;
    private Shape shape;

    /**
     * Getter.
     * @return the health of the ship.
     */
    public int getLife()
    {
        return life;
    }

    /**
     * Getter.
     * @return the position of the ship.
     */
    public Position getPosition()
    {
        return position;
    }

    /**
     * Getter.
     * @return the name of the ship.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter.
     * @return the deck of the ship.
     */
    public Deck getDeck()
    {
        return deck;
    }

    /**
     * Getter.
     * @return the Entities around the ship.
     */
    public OnboardEntity[] getEntities()
    {
        return entities.clone();
    }

    /**
     * Getter.
     * @return the number of Oar onboard.
     */
    public int getNumberOar(){
        return (int) Arrays.stream(getEntities()).filter(e -> e instanceof Rame).count();
    } //TODO : A verifier je ne suis pas dutout un AS en stream #Alexis

    /**
     * Getter.
     * @return the Shape of the Boat.
     */
    public Shape getShape()
    {
        return shape;
    }

    /**
     * Determine which Entity is set on a specific Point.
     *
     * @param xPos The abscissa of the Point to analyse.
     * @param yPos The ordinate of the Point to analyse.
     * @return optional entity on the given cell.
     */
    public Optional<OnboardEntity> getEntityHere(int xPos, int yPos){
        for(OnboardEntity ent : entities)
            if(ent.getX() == xPos && ent.getY() == yPos)
                return Optional.of(ent);
        return Optional.empty();
    }

    /**
     * Determine which Entity is set on a specific Point.
     * @param pos The coords we want to analyse.
     * @return optional entity on the given cell.
     */
    public Optional<OnboardEntity> getEntityHere(Pair<Integer, Integer> pos){
        return getEntityHere(pos.getFirst(), pos.getSecond());
    }
}
