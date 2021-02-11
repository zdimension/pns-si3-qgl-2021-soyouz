package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * He Ho Freshwater sailor !
 */
public class Marin {
    private int id;
    private int x;
    private int y;
    private String name;

    public static final int maxMove = 5;

    /**
     * Getter.
     * @return the Id of the Sailor.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter.
     * @return the x position of the Sailor on the Deck.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter.
     * @return the y position of the Sailor on the Deck.
     */
    public int getY() {
        return y;
    }

    /**
     * Getter.
     * @return the coords of the Sailor on the Deck.
     */
    @JsonIgnore
    public Pair<Integer, Integer> getGridPosition() {
        return Pair.of(x, y);
    }

    /**
     * Getter.
     * @return the Name of the Sailor.
     */
    public String getName() {
        return name;
    }

    /**
     * Constructor.
     * @param id Its Id.
     * @param x Its base x position.
     * @param y Its base y position.
     * @param name Its name.
     */
    public Marin(@JsonProperty("id") int id,
                 @JsonProperty("x") int x,
                 @JsonProperty("y") int y,
                 @JsonProperty("name") String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    //TODO checker si c'est sur le bateau ailleurs
    /**
     * Moves this sailor the number of cells specified. This sailor can move up to 5 cells.
     *
     * @param xDist to move on X axis.
     * @param yDist to move on Y axis.
     * @return new absolute position.
     * @throws IllegalArgumentException if it wants to move further than 5 cells.
     */
    public Pair<Integer, Integer> moveRelative(int xDist, int yDist) throws IllegalArgumentException {
        if (!isRelPosReachable(xDist, yDist))
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
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
    public Pair<Integer, Integer> moveRelative(Pair<Integer, Integer> dist) throws IllegalArgumentException {
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
    public Pair<Integer, Integer> moveAbsolute(int xPos, int yPos) throws  IllegalArgumentException{
        if(!this.isAbsPosReachable(xPos, yPos))
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
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
    public Pair<Integer, Integer> moveAbsolute(Pair<Integer, Integer> pos) throws  IllegalArgumentException{
        return moveAbsolute(pos.getFirst(), pos.getSecond());
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param xDist distance on X axis
     * @param yDist distance on X axis
     * @return if this sailor can move the number of cells specified
     */
    public boolean isRelPosReachable(int xDist, int yDist){
        return Math.abs(xDist) + Math.abs(yDist) <= maxMove;
    }

    /**
     * Determine if a Cell is reachable.
     *
     * @param xPos cell on X axis
     * @param yPos cell on Y axis
     * @return if the absolute position is reachable for this sailor
     */
    public boolean isAbsPosReachable(int xPos, int yPos){
        return Math.abs(this.x - xPos) + Math.abs(this.y - yPos) <= maxMove;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Marin))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        var mar = (Marin) obj;
        return this.id == mar.id && this.x == mar.x && this.y == mar.y && this.name.equals(mar.name);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
