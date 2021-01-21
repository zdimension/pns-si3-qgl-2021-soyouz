package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

public class Marin
{
    private int id;
    private int x;
    private int y;
    private String name;

    public int getId()
    {
        return id;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String getName()
    {
        return name;
    }

    public Marin(int id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }
}
