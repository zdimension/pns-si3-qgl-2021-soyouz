package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

/**
 * He Ho Freshwater sailor !
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Marin
{
    public static final int MAX_MOVE = 5;
    private int id;
    private String name;
    private int x;
    private int y;

    @JsonCreator
    private Marin()
    {
    }

    /**
     * Constructor.
     *
     * @param id   Its Id.
     * @param x    Its base x position.
     * @param y    Its base y position.
     * @param name Its name.
     */
    public Marin(int id,
                 int x,
                 int y,
                 String name)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     * Getter.
     *
     * @return the Id of this.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Getter.
     *
     * @return the x position of this on the
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Setters.
     *
     * @param x The abscissa to set.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Getter.
     *
     * @return the y position of this on the
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Setters.
     *
     * @param y The ordinate to set.
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * Getter.
     *
     * @return the position of the sailor.
     */
    @JsonIgnore
    public PosOnShip getPos()
    {
        return PosOnShip.of(getX(), getY());
    }

    /**
     * Getter.
     *
     * @return the Name of this.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Moves this sailor the number of cells specified. This sailor can move up to 5 cells.
     *
     * @param pos to move.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public PosOnShip moveRelative(PosOnShip pos)
    {
        if (!isRelPosReachable(pos))
        {
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        }
        this.x += pos.getX();
        this.y += pos.getY();
        return this.getPos();
    }

    /**
     * Moves this sailor at the specified cell. This sailor can move up to 5 cells.
     *
     * @param pos to reach.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public PosOnShip moveAbsolute(PosOnShip pos)
    {
        if (!this.isAbsPosReachable(pos))
        {
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        }
        this.x = pos.getX();
        this.y = pos.getY();
        return this.getPos();
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param pos relative position
     * @return if this sailor can move the number of cells specified
     */
    public boolean isRelPosReachable(PosOnShip pos)
    {
        return pos.norm() <= MAX_MOVE;
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param xPos cell on X axis
     * @param yPos cell on Y axis
     * @return if the absolute position is reachable for this sailor
     */
    public boolean isAbsPosReachable(int xPos, int yPos)
    {
        return Math.abs(this.x - xPos) + Math.abs(this.y - yPos) <= MAX_MOVE;
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param pos a Pair of coords (X,Y).
     * @return if the absolute position is reachable for this sailor.
     */
    public boolean isAbsPosReachable(PosOnShip pos)
    {
        return isAbsPosReachable(pos.getFirst(), pos.getSecond());
    }

    /**
     * Determine the number of rounds to reach an entity.
     *
     * @param xPos The X of the entity.
     * @param yPos The Y of the entity.
     * @return a number of turn.
     */
    public int numberExtraRoundsToReachEntity(int xPos, int yPos)
    {
        return (Math.abs(this.x - xPos) + Math.abs(this.y - yPos)) / MAX_MOVE;
    }

    /**
     * Determine the number of rounds to reach an entity.
     *
     * @param pos The position of the entity.
     * @return a number of turn.
     */
    public int numberExtraRoundsToReachEntity(PosOnShip pos)
    {
        return numberExtraRoundsToReachEntity(pos.getFirst(), pos.getSecond());
    }

    /**
     * Generic equals override.
     *
     * @param obj The second object.
     * @return if the two instance are equals or not.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Marin))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        var mar = (Marin) obj;
        return this.id == mar.id;
    }

    /**
     * Generic hash method override.
     *
     * @return the hash code associated to the current object.
     */
    @Override
    public int hashCode()
    {
        return id;
    }

    /**
     * Generic toString method override.
     *
     * @return the string associated to the current object.
     */
    @Override
    public String toString()
    {
        return "Marin{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", x=" + x +
            ", y=" + y +

            '}';
    }
}
