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

    /**
     * Add two points together.
     *
     * @param other The second point.
     * @return the sum of the two points.
     */
    public Point2d add(Point2d other)
    {
        return new Point2d(x + other.x, y + other.y);
    }

    /**
     * Substract two points together.
     *
     * @param other The second point.
     * @return the sub of the two points.
     */
    public Point2d sub(Point2d other)
    {
        return new Point2d(x - other.x, y - other.y);
    }

    /**
     * Multiply a point by a value.
     *
     * @param d The multiplicator.
     * @return the multiplication of the points by the value.
     */
    public Point2d mul(double d)
    {
        return new Point2d(d * x, d * y);
    }

    /**
     * Getter.
     *
     * @return the square of the norm of the point.
     */
    public double normSquared()
    {
        return Math.pow(x, 2) + Math.pow(y, 2);
    }

    /**
     * Getter.
     *
     * @return the norm of the point.
     */
    public double norm()
    {
        return Math.sqrt(normSquared());
    }

    /**
     * Rotate the point.
     *
     * @param angle The angle of rotation.
     * @return the point after the rotation.
     */
    public Point2d rotate(double angle)
    {
        if (angle == 0)
        {
            return this;
        }
        var cos = Math.cos(angle);
        var sin = Math.sin(angle);
        return new Point2d(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Scalar product of two points.
     *
     * @param b The second point.
     * @return the result.
     */
    public double dot(Point2d b)
    {
        return x * b.x + y * b.y;
    }

    /**
     * Determine if the point is on a line or not.
     *
     * @param v A point of the line.
     * @param w A second point of the line.
     * @return true if the point is on the line, false otherwise.
     */
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

    /**
     * Get a new point with the smallests coords from two other points.
     *
     * @param other The second point.
     * @return a new point.
     */
    public Point2d min(Point2d other)
    {
        return new Point2d(Math.min(x, other.x), Math.min(y, other.y));
    }

    /**
     * Get a new point with the greatest coords from two other points.
     *
     * @param other The second point.
     * @return a new point.
     */
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

    /**
     * Determine the angle between the point and the center.
     *
     * @return the angle.
     */
    public double angle()
    {
        return Math.atan2(y, x);
    }

    /**
     * Transform a point into a position.
     *
     * @return the position.
     */
    public Position toPosition()
    {
        return new Position(x, y, 0);
    }

    /**
     * Getter.
     *
     * @return the point at the opposite ordinate.
     */
    public Point2d ortho()
    {
        return new Point2d(-y, x);
    }

    /**
     * Determine the distance between two points.
     *
     * @param other The second point.
     * @return the distance.
     */
    public double distance(Point2d other)
    {
        return sub(other).norm();
    }

    /**
     * Determine the point at the center of two points.
     *
     * @param cur The second point.
     * @return the point in the middle.
     */
    public Point2d mid(Point2d cur)
    {
        return sub(cur).mul(0.5).add(cur);
    }
}
