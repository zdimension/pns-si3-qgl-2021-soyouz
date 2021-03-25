package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import static java.lang.Math.*;
import static java.lang.Math.pow;

public class Segment
{
    Point a;
    Point b;

    /**
     * Constructor.
     * @param a Point 1
     * @param b Point 2
     */
    public Segment(Point a, Point b)
    {
        this.a = a;
        this.b = b;
    }

    /**
     * Determine the length of the segment.
     * @return the distance
     */
    public double getLength()
    {
        return sqrt(pow(a.getX() - b.getX(), 2) + pow(a.getY() - b.getY(), 2));
    }

    /**
     * Check if given point is on the segment
     * @param r
     * @return a boolean
     */
    public boolean onSegment(Point r)
    {
        if (b.getX() <= Math.max(a.getX(), r.getX()) && b.getX() >= Math.min(a.getX(), r.getX()) &&
            b.getY() <= Math.max(a.getY(), r.getY()) && b.getY() >= Math.min(a.getY(), r.getY()))
            return true;

        return false;
    }


    /**
     * Getter.
     * @return first point of segment
     */
    public Point getA()
    {
        return a;
    }

    /**
     * Getter.
     * @return second point of segment
     */
    public Point getB()
    {
        return b;
    }
}
