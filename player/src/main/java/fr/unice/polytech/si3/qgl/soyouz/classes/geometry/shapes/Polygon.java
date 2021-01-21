package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point;

public class Polygon extends Shape
{
    private double orientation;
    private Point[] vertices;

    public double getOrientation()
    {
        return orientation;
    }

    public Point[] getVertices()
    {
        return vertices.clone();
    }
}
