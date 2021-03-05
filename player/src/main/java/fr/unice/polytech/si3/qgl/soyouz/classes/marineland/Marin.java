package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * He Ho Freshwater sailor !
 */
public class Marin
{
    public static final int MAX_MOVE = 5;
    private final int id;
    private final String name;
    private int x;
    private int y;

    /**
     * Constructor.
     *
     * @param id   Its Id.
     * @param x    Its base x position.
     * @param y    Its base y position.
     * @param name Its name.
     */
    public Marin(@JsonProperty("id") int id,
                 @JsonProperty("x") int x,
                 @JsonProperty("y") int y,
                 @JsonProperty("name") String name)
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
    public Pair<Integer, Integer> getPos()
    {
        return Pair.of(getX(), getY());
    }

    /**
     * Getter.
     *
     * @return the coordinates of this on the
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
     */
    @JsonIgnore
    public Pair<Integer, Integer> getGridPosition()
    {
        return Pair.of(x, y);
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
     * @param xDist to move on X axis.
     * @param yDist to move on Y axis.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public Pair<Integer, Integer> moveRelative(int xDist, int yDist)
    {
        if (!isRelPosReachable(xDist, yDist))
        {
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        }
        this.x += xDist;
        this.y += yDist;
        return this.getGridPosition();
    }

    /**
     * Moves this sailor the number of cells specified. This sailor can move up to 5 cells.
     *
     * @param dist to move on x and y axis.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public Pair<Integer, Integer> moveRelative(Pair<Integer, Integer> dist)
    {
        return moveRelative(dist.getFirst(), dist.getSecond());
    }

    /**
     * Moves this sailor at the specified cell. This sailor can move up to 5 cells.
     *
     * @param xPos to reach on X axis.
     * @param yPos to reach on Y axis.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public Pair<Integer, Integer> moveAbsolute(int xPos, int yPos)
    {
        if (!this.isAbsPosReachable(xPos, yPos))
        {
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        }
        this.x = xPos;
        this.y = yPos;
        return this.getGridPosition();
    }

    /**
     * Moves this sailor at the specified cell. This sailor can move up to 5 cells.
     *
     * @param pos to move on x and y axis.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public Pair<Integer, Integer> moveAbsolute(Pair<Integer, Integer> pos)
    {
        return moveAbsolute(pos.getFirst(), pos.getSecond());
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param xDist distance on X axis
     * @param yDist distance on X axis
     * @return if this sailor can move the number of cells specified
     */
    public boolean isRelPosReachable(int xDist, int yDist)
    {
        return Math.abs(xDist) + Math.abs(yDist) <= MAX_MOVE;
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
    public boolean isAbsPosReachable(Pair<Integer, Integer> pos)
    {
        return isAbsPosReachable(pos.getFirst(), pos.getSecond());
    }

    public int numberExtraRoundsToReachEntity(int xPos, int yPos)
    {
        return Math.abs(this.x - xPos) + Math.abs(this.y - yPos) / MAX_MOVE;
    }

    public int numberExtraRoundsToReachEntity(Pair<Integer, Integer> pos)
    {
        return numberExtraRoundsToReachEntity(pos.getFirst(), pos.getSecond());
    }

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
        return this.id == mar.id; //&& this.x == mar.x && this.y == mar.y && this.name.equals(mar
        // .name);
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
