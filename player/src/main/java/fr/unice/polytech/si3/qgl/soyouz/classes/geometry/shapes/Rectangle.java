package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rectangle Shape.
 */
public class Rectangle implements Shape
{
    private final double width;
    private final double height;
    private final double orientation;

    /**
     * Constructor.
     *
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param orientation The orientation of the rectangle.
     */
    public Rectangle(@JsonProperty("width")double width,
                     @JsonProperty("height") double height,
                     @JsonProperty("orientation") double orientation)
    {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
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

    /**
     * Getter.
     *
     * @return the Orientation of the Rectangle.
     */
    public double getOrientation()
    {
        return orientation;
    }
}
