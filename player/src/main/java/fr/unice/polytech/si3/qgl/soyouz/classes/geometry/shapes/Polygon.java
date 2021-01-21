package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point;

@JsonTypeName("̂polygon")
public class Polygon extends Shape
{
    private double orientation;
    private Point[] vertices;
}
