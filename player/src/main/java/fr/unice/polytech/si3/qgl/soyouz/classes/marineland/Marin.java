package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import fr.unice.polytech.si3.qgl.soyouz.classes.Pair;

public class Marin {
    private int id;
    private int x;
    private int y;
    private String name;

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Pair<Integer, Integer> getGridPosition() {
        return Pair.of(x, y);
    }

    public String getName() {
        return name;
    }

    public Marin(int id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    //TODO checker si c'est sur le bateau ailleurs
    /**
     * Moves this sailor the number of cells specified. This sailor can move up to 5 cells.
     *
     * @param xDist to move on X axis
     * @param yDist to move on Y axis
     * @return new absolute position
     * @throws IllegalArgumentException
     */
    public Pair<Integer, Integer> moveRelative(int xDist, int yDist) throws IllegalArgumentException {
        if (Math.abs(xDist) + Math.abs(yDist) > 5)
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        this.x += xDist;
        this.y += yDist;
        return this.getGridPosition();
    }

    public Pair<Integer, Integer> moveRelative(Pair<Integer, Integer> dist) throws IllegalArgumentException {
        return moveRelative(dist.getFirst(), dist.getSecond());
    }

    /**
     * Moves this sailor at the specified cell. This sailor can move up to 5 cells.
     *
     * @param xPos to reach on X axis
     * @param yPos to reach on Y axis
     * @return new absolute position
     * @throws IllegalArgumentException
     */
    public Pair<Integer, Integer> moveAbsolute(int xPos, int yPos) throws  IllegalArgumentException{
        if(Math.abs(this.x - xPos) + Math.abs(this.y - yPos) > 5)
            throw new IllegalArgumentException("Sailor must move 5 cells or lower");
        this.x = xPos;
        this.y = yPos;
        return this.getGridPosition();
    }

    public Pair<Integer, Integer> moveAbsolute(Pair<Integer, Integer> pos) throws  IllegalArgumentException{
        return moveAbsolute(pos.getFirst(), pos.getSecond());
    }
}
