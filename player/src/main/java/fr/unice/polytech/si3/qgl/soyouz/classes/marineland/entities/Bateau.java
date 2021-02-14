package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Ship entity.
 */
@JsonSubTypes({
    @JsonSubTypes.Type(value = Bateau.class, name = "ship")
})
public class Bateau extends AutreBateau
{
    private String name;
    private Deck deck;
    private OnboardEntity[] entities;

    public Bateau(@JsonProperty("name") String name,
                  @JsonProperty("deck") Deck deck,
                  @JsonProperty("entities") OnboardEntity[] entities)
    {
        this.name = name;
        this.deck = deck;
        this.entities = Arrays.stream(entities).filter(Objects::nonNull).toArray(OnboardEntity[]::new);
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
    @JsonIgnore
    public int getNumberOar(){
        return (int) Arrays.stream(getEntities()).filter(e -> e instanceof Rame).count();
    } //TODO : A verifier je ne suis pas dutout un AS en stream #Alexis

    /**
     *
     * @param posObs position of obstacle
     * @return true if the obstacle is on the left of the boat
     */
    public boolean isPositionOnLeft(Position posObs){
        var angle =
                Math.atan((posObs.getX() - (this.getPosition().getX()+Math.cos(this.getPosition().getOrientation())))
                        /(1+(this.getPosition().getY() + Math.sin(this.getPosition().getOrientation()))*posObs.getY()));
        return  angle >= 0 && angle <= Math.PI;
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

    public boolean hasAt(int xPos, int yPos, Class<?> cls)
    {
        return cls.isInstance(getEntityHere(xPos, yPos).orElse(null));
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
