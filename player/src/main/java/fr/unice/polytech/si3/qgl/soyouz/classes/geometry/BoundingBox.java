package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

/**
 * A class to define a list of point that create a box around a shape.
 */
public class BoundingBox
{
    public final double minX;
    public final double maxX;
    public final double minY;
    public final double maxY;

    /**
     * Constructor.
     *
     * @param minX Min abscissa.
     * @param maxX Max abscissa.
     * @param minY Min ordinate.
     * @param maxY Max ordinate.
     */
    public BoundingBox(double minX, double maxX, double minY, double maxY)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Determine if a point is contained in the box.
     *
     * @param p The point.
     * @return true if it is, false otherwise.
     */
    public boolean contains(Point2d p)
    {
        return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
    }
}
