package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.Pair;

public class Position
{
    private double x;
    private double y;
    private double orientation;

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getOrientation()
    {
        return orientation;
    }

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
