package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

/**
 * Position of a Shape : Defined by the coords of its central Point and its orientation.
 */
public class Position
{
    private double x;
    private double y;
    private double orientation;

    public Position(){
        //TODO ?
    }
    //TODO ?
    public Position(@JsonProperty("x") double x,
                    @JsonProperty("y") double y,
                    @JsonProperty("orientation")double orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

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
        return Pair.of(abs(this.getX() - pos.getX()), abs(this.getY() - pos.getY()));
    }

    public Position getPositionPlusPath(double speed){
        return new Position(); //TODO
    }

    public boolean isPositionReachable(Position toReach, double speed){
        return true; //TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.x, x) == 0 &&
                Double.compare(position.y, y) == 0 &&
                Double.compare(position.orientation, orientation) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, orientation);
    }

    public boolean inLine(Position pos)
    {
        return abs(atan2(pos.getY() - this.getY(), pos.getX() - this.getX()) - this.getOrientation()) < 0.01;
    }
}
