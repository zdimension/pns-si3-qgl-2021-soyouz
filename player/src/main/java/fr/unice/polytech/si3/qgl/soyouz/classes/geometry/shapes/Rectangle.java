package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

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
        super(orientation, new Point2d[]
            {
                new Point2d(-height / 2, -width / 2),
                new Point2d(height / 2, -width / 2),
                new Point2d(height / 2, width / 2),
                new Point2d(-height / 2, width / 2)
            });
        this.width = width;
        this.height = height;
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
        return Math.abs(pos.getX()) <= height / 2 && Math.abs(pos.getY()) <= width / 2;
    }

    @Override
    public double getMaxDiameter()
    {
        return Math.hypot(width, height);
    }
}
