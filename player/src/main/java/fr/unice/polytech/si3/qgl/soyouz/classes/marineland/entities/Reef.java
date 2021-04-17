package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

public class Reef extends ShapedEntity implements Entity, Collidable
{
    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && obj instanceof Reef;
    }
}
