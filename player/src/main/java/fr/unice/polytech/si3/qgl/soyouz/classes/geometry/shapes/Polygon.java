package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point;

/**
 * Polygon shape.
 */
public class Polygon implements Shape
{
    private double orientation;
    private Point[] vertices;

    /**
     * Getter.
     *
     * @return the Orientation of the Polygon.
     */
    public double getOrientation()
    {
        return orientation;
    }

    /**
     * Getter.
     *
     * @return all Points that compose the Polygon.
     */
    public Point[] getVertices()
    {
        return vertices.clone();
    }
}
