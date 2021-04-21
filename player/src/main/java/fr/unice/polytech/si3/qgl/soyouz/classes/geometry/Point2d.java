package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Point defined by its abscissa x and ordinate y.
 */
public class Point2d
{
    public static final Point2d ZERO = new Point2d(0, 0);

    public final double x;
    public final double y;

    /**
     * Constructor.
     *
     * @param x The abscissa of the point.
     * @param y The ordinate of the point.
     */
    public Point2d(@JsonProperty("x") double x, @JsonProperty("y") double y)
    {
        this.x = x;
        this.y = y;
    }

    public static Position fromPolar(double r, double theta)
    {
        return new Position(r * Math.cos(theta), r * Math.sin(theta), 0);
    }

    /**
     * Getter.
     *
     * @return the abscissa of the Point.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Getter.
     *
     * @return the ordinate of the Point.
     */
    public double getY()
    {
        return y;
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

    public double normSquared()
    {
        return Math.pow(x, 2) + Math.pow(y, 2);
    }

    public double norm()
    {
        return Math.sqrt(normSquared());
    }

    public Point2d rotate(double angle)
    {
        if (angle == 0)
            return this;
        var cos = Math.cos(angle);
        var sin = Math.sin(angle);
        return new Point2d(x * cos - y * sin, x * sin + y * cos);
    }

    public double dot(Point2d b)
    {
        return x * b.x + y * b.y;
    }

    public boolean isOnLine(Point2d v, Point2d w)
    {
        final float tol = 1e-8f;
        var l2 = (w.sub(v)).normSquared(); // i.e. |w-v|^2 -  avoid a sqrt
        if (l2 == 0.0) return (v.sub(this)).norm() <= tol; // v == w case
        // Consider the line extending the segment, parameterized as v + t (w - v).
        // We find projection of point p onto the line.
        // It falls where t = [(p-v) . (w-v)] / |w-v|^2
        // We clamp t from [0,1] to handle points outside the segment vw.
        var t = Math.max(0, Math.min(1, (this.sub(v)).dot(w.sub(v)) / l2));
        var projection = v.add(w.sub(v).mul(t)); // Projection falls on the segment
        return (this.sub(projection)).norm() <= tol;
    }

    public Point2d min(Point2d other)
    {
        return new Point2d(Math.min(x, other.x), Math.min(y, other.y));
    }

    public Point2d max(Point2d other)
    {
        return new Point2d(Math.max(x, other.x), Math.max(y, other.y));
    }

    @Override
    public String toString()
    {
        return "{" +
            "x=" + x +
            ", y=" + y +
            "}";
    }

    public double angle()
    {
        return Math.atan2(y, x);
    }

    public Position toPosition()
    {
        return new Position(x, y, 0);
    }

    public Point2d ortho()
    {
        return new Point2d(-y, x);
    }

    public double distance(Point2d other)
    {
        return sub(other).norm();
    }

    public Point2d mid(Point2d cur)
    {
        return sub(cur).mul(0.5).add(cur);
    }
}
