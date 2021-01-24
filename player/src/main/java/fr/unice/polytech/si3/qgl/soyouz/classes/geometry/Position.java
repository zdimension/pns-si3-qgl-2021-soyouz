package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * Position of a Shape : Defined by the coords of its central Point and its orientation.
 */
public class Position
{
    private double x;
    private double y;
    private double orientation;

    /**
     * Getter.
     * @return the abscissa of the central Point.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Getter.
     * @return the ordinate of the central Point.
     */
    public double getY()
    {
        return y;
    }

    /**
     * Getter.
     * @return the orientation of the Shape.
     */
    public double getOrientation()
    {
        return orientation;
    }

    //TODO pour l'instant tient uniquement compte de la distance depuis le centre
    public Pair<Double, Double> getDistance(Position pos){
        return Pair.of(Math.abs(this.getX() - pos.getX()), Math.abs(this.getY() - pos.getY()));
    }

    public Position getPositionPlusPath(double speed){
        return new Position(); //TODO
    }

    public boolean isPositionReachable(Position toReach, double speed){
        return true; //TODO
    }
}
