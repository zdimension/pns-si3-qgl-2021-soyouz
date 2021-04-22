package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Ship entity.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class Bateau extends AutreBateau
{
    private final String name;
    private final Deck deck;
    private final OnboardEntity[] entities;

    Bateau(@JsonProperty("name") String name,
           @JsonProperty("deck") Deck deck,
           @JsonProperty("entities") OnboardEntity[] entities,
           @JsonProperty("type") String type) //NOSONAR
    {
        this(name, deck, entities);
    }

    /**
     * Constructor.
     *
     * @param name     The name of the boat.
     * @param deck     The deck of the boat.
     * @param entities The entities on board.
     */
    public Bateau(String name, Deck deck, OnboardEntity[] entities)
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
        return (int) Util.filterType(Arrays.stream(getEntities()), Rame.class).count();
    }

    /**
     * Getter.
     *
     * @return the number of Sails onboard.
     */
    @JsonIgnore
    public int getNumberSail()
    {
        return (int) Util.filterType(Arrays.stream(getEntities()), Voile.class).count();
    }

    /**
     * Determine if a specific object is places at a specific position.
     *
     * @param pos The coords we want to analyse.
     * @param cls The Class of object researched.
     * @return true if there is the object, false otherwise.
     */
    public boolean hasAt(PosOnShip pos, Class<?> cls)
    {
        return cls.isInstance(getEntityHere(pos).orElse(null));
    }

    /**
     * Determines which
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} is
     * set on a specific {@link Point2d}.
     *
     * @param pos The coords we want to analyse.
     * @return optional entity on the given cell.
     */
    public Optional<OnboardEntity> getEntityHere(PosOnShip pos)
    {
        return Arrays.stream(entities).filter(ent -> ent.getPos().equals(pos)).findFirst();
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
    public PosOnShip findFirstPosOfEntity(Class<? extends OnboardEntity> ent)
    {
        return Util.filterType(Arrays.stream(this.entities), ent).findFirst()
            .map(OnboardEntity::getPos).orElse(null);
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
            else if (ent instanceof Voile)
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'L';
            }
            else if (ent instanceof Vigie)
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'V';
            }
            else
            {
                str[ent.getX() * 2 + 2][ent.getY() * 2 + 1] = 'E';
            }
            //Canon
        }

        var strBateau = Arrays.stream(str).map(String::new).collect(Collectors.joining("\n"));
        String info = name + " | life : " + getLife() + " Position : " + getPosition() + "\n";

        return info + strBateau;
    }

    /**
     * Generic equals method override.
     *
     * @return true if equals, false otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bateau bateau = (Bateau) o;
        return Objects.equals(name, bateau.name) && Objects.equals(deck, bateau.deck) && Arrays.equals(entities, bateau.entities);
    }

    /**
     * Generic hash method override.
     *
     * @return the hashcode.
     */
    @Override
    public int hashCode()
    {
        int result = Objects.hash(super.hashCode(), name, deck);
        result = 31 * result + Arrays.hashCode(entities);
        return result;
    }
}
