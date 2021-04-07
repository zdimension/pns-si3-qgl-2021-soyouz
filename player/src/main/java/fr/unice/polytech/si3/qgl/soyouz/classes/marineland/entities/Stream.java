package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;

public class Stream extends ShapedEntity implements Entity
{
    private double strength;

    public double getStrength()
    {
        return strength;
    }

    public Position getProjectedStrength()
    {
        var angle = getPosition().getOrientation();
        return new Position(strength * Math.cos(angle), strength * Math.sin(angle), 0);
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && obj instanceof Stream && ((Stream) obj).strength == strength;
    }
}
