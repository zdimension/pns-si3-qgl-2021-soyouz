package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

public class Segment
{
    private Point2d a;
    private Point2d b;

    /**
     * Constructor.
     * @param a Point2d 1
     * @param b Point2d 2
     */
    public Segment(Point2d a, Point2d b)
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
        return b.sub(a).norm();
    }

    /**
     * Check if given point is on the segment
     * @param r
     * @return a boolean
     */
    public boolean onSegment(Point2d r)
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
    public Point2d getA()
    {
        return a;
    }

    /**
     * Getter.
     * @return second point of segment
     */
    public Point2d getB()
    {
        return b;
    }
}
