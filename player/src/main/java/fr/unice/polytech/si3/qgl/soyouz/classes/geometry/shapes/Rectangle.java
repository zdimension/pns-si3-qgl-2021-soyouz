package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.BoundingBox;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

/**
 * Rectangle Shape.
 */
public class Rectangle extends Polygon implements Shape
{
    private final double width;
    private final double height;

    /**
     * Constructor.
     *
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param orientation The orientation of the rectangle.
     */
    public Rectangle(@JsonProperty("width") double width,
                     @JsonProperty("height") double height,
                     @JsonProperty("orientation") double orientation)
    {
        super(orientation, getPoints(width / 2, height / 2), Pair.of(new BoundingBox(
            -height / 2, height / 2, -width / 2, width / 2
        ), Point2d.ZERO));
        this.width = width;
        this.height = height;
    }

    private static Point2d[] getPoints(double width, double height)
    {
        return new Point2d[]
            {
                new Point2d(-height, -width),
                new Point2d(height, -width),
                new Point2d(height, width),
                new Point2d(-height, width)
            };
    }

    /**
     * Getter.
     *
     * @return the Width of the Rectangle.
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * Getter.
     *
     * @return the Height of the Rectangle.
     */
    public double getHeight()
    {
        return height;
    }

    @Override
    public boolean contains(Point2d pos)
    {
        return Math.abs(pos.x) <= height / 2 && Math.abs(pos.y) <= width / 2;
    }

    @Override
    public double getMaxDiameter()
    {
        return Math.hypot(width, height);
    }

    @Override
    protected Point2d[] getShellInternal(double shipSize)
    {
        return getPoints(width / 2 + shipSize, height / 2 + shipSize);
    }
}
