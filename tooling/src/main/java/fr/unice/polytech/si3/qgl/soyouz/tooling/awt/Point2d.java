package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

public class Point2d
{
    public double x;
    public double y;

    public Point2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Point2d add(Point2d other)
    {
        return new Point2d(x + other.x, y + other.y);
    }

    public Point2d sub(Point2d other)
    {
        return new Point2d(x - other.x, y - other.y);
    }

    public Point2d mul(double d)
    {
        return new Point2d(d * x, d * y);
    }

    public Point2d invY()
    {
        return new Point2d(x, -y);
    }
}
