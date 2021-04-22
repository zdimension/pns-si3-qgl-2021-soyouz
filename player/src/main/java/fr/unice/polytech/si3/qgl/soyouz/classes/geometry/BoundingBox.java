package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

public class BoundingBox
{
    public final double minX;
    public final double maxX;
    public final double minY;
    public final double maxY;

    public BoundingBox(double minX, double maxX, double minY, double maxY)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public boolean contains(Point2d p)
    {
        return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
    }
}
