package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Ship entity.
 */
@JsonSubTypes({
    @JsonSubTypes.Type(value = Bateau.class, name = "ship")
})
public class Bateau extends AutreBateau
{
    private final String name;
    private final Deck deck;
    private final OnboardEntity[] entities;

    /**
     * Constructor.
     *
     * @param name     The name of the boat.
     * @param deck     The deck of the boat.
     * @param entities The entities on board.
     */
    public Bateau(@JsonProperty("name") String name,
                  @JsonProperty("deck") Deck deck,
                  @JsonProperty("entities") OnboardEntity[] entities)
    {
        this.name = name;
        this.deck = deck;
        this.entities =
            Arrays.stream(entities).filter(Objects::nonNull).toArray(OnboardEntity[]::new);
    }

    /**
     * Getter.
     *
     * @return the name of the ship.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter.
     *
     * @return the deck of the ship.
     */
    public Deck getDeck()
    {
        return deck;
    }

    /**
     * Getter.
     *
     * @return the Entities around the ship.
     */
    public OnboardEntity[] getEntities()
    {
        return entities.clone();
    }

    /**
     * Getter.
     *
     * @return the number of Oar onboard.
     */
    @JsonIgnore
    public int getNumberOar()
    {
        return (int) Arrays.stream(getEntities()).filter(e -> e instanceof Rame).count();
    }

    /**
     * Getter.
     *
     * @return the number of Sails onboard.
     */
    @JsonIgnore
    public int getNumberSail()
    {
        return (int) Arrays.stream(getEntities()).filter(e -> e instanceof Voile).count();
    }

    /**
     * Determines which
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} is
     * set on a specific {@link fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point}.
     *
     * @param xPos The abscissa of the Point to analyse.
     * @param yPos The ordinate of the Point to analyse.
     * @return optional entity on the given cell.
     */
    public Optional<OnboardEntity> getEntityHere(int xPos, int yPos)
    {
        for (OnboardEntity ent : entities)
        {
            if (ent.getX() == xPos && ent.getY() == yPos)
            {
                return Optional.of(ent);
            }
        }
        return Optional.empty();
    }

    /**
     * Determine if a specific object is places at a specific position.
     *
     * @param xPos The potential abscissa of the object.
     * @param yPos The potential ordinate of the object.
     * @param cls  The Class of object researched.
     * @return true if there is the object, false otherwise.
     */
    public boolean hasAt(int xPos, int yPos, Class<?> cls)
    {
        return cls.isInstance(getEntityHere(xPos, yPos).orElse(null));
    }

    /**
     * Determines which
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} is
     * set on a specific {@link fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point}.
     *
     * @param pos The coords we want to analyse.
     * @return optional entity on the given cell.
     */
    public Optional<OnboardEntity> getEntityHere(Pair<Integer, Integer> pos)
    {
        return getEntityHere(pos.getFirst(), pos.getSecond());
    }

    /**
     * Get all the oars on each side.
     *
     * @return a Pair of oars (left, right).
     */
    public Pair<Integer, Integer> getNbOfOarOnEachSide()
    {
        var oars =
            Util.filterType(Arrays.stream(this.getEntities()), Rame.class).collect(Collectors.toList());
        int leftOar = 0;
        int rightOar = 0;
        for (var oar : oars)
        {
            if (oar.isLeft())
            {
                leftOar++;
            }
            else
            {
                rightOar++;
            }
        }
        return Pair.of(leftOar, rightOar);
    }

    /**
     * Find the first occurrence of a given entity.
     *
     * @param ent to find the position of.
     * @return the fist position found of the given entity.
     */
    public Pair<Integer, Integer> findFirstPosOfEntity(Class<? extends OnboardEntity> ent)
    {
        return Util.filterType(Arrays.stream(this.entities), ent).findFirst()
            .map(OnboardEntity::getPosCoord).orElse(null);
    }

    /**
     * Find the first occurrence of a given entity.
     *
     * @param ent to find the position of.
     * @param <T> the type of the entity.
     * @return the fist position found of the given entity.
     */
    public <T extends OnboardEntity> T findFirstEntity(Class<T> ent)
    {
        return Util.filterType(Arrays.stream(this.entities), ent).findFirst().orElse(null);
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString()
    {
        var len = 2 * deck.getLength() + 2;
        var wid = 2 * deck.getWidth() + 1;
        var str = new char[len][wid];

        for (char[] line : str)
        {
            Arrays.fill(line, ' ');
        }
        str[0][1] = '/';
        str[0][wid - 2] = '\\';
        for (int y = 0; y < wid; y++)
        {
            for (int x = 1; x < len; x++)
            {
                str[x][y] = ((y % 2 == 0) && (x % 2 == 0) ? '|' : ((y % 2 == 1) && (x % 2 == 1) ?
                    '_' : ' '));
            }
        }

        for (OnboardEntity ent : entities)
        {
            if (ent instanceof Rame)
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'R';
            }
            else if (ent instanceof Gouvernail)
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'G';
            }
            else if(ent instanceof Voile)
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'L';
            }
            else
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'E';
            }
            //Rame
            //Gouvernail
            //Vigie
            //Canon
        }

        var strBateau = Arrays.stream(str).map(String::new).collect(Collectors.joining("\n"));
        String info = name + " | life : " + getLife() + " Position : " + getPosition() + "\n";

        return info + strBateau;
    }
}
