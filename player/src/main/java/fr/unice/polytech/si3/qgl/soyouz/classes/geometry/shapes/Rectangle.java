package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rectangle Shape.
 */
public class Rectangle implements Shape
{
    private double width;
    private double height;
    private double orientation;

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
