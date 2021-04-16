package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;

public class Stream extends ShapedEntity implements Entity
{
    private double strength;

    public double getStrength()
    {
        return strength;
    }

    @JsonIgnore
    public Position getProjectedStrength()
    {
        return Point2d.fromPolar(strength, getPosition().getOrientation());
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && obj instanceof Stream && ((Stream) obj).strength == strength;
    }
}
