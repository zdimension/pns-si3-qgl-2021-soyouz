package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Polygon shape.
 */
public class Polygon implements Shape
{
    private final double orientation;
    private final Point2d[] vertices;
    private final Point2d[] lastShell;
    private double lastShipSize = Double.NaN;

    /**
     * Constructor.
     *
     * @param orientation The orientation of the polygon.
     * @param vertices    All points that compose the shape.
     */
    public Polygon(@JsonProperty("orientation") double orientation,
                   @JsonProperty("vertices") Point2d[] vertices)
    {
        this.orientation = orientation;
        this.vertices = vertices;
        this.lastShell = new Point2d[vertices.length];
    }

    /**
     * @param a start
     * @param b end
     * @param p point
     * @return the distance between the point p and the line segment [a;b]
     */
    static double distanceToLine(Point2d a, Point2d b, Point2d p)
    {
        return (b.getX() - a.getX()) * (p.getY() - a.getY())
            - (p.getX() - a.getX()) * (b.getY() - a.getY());
    }

    /**
     * @param a start
     * @param b end
     * @param p point
     * @return the side of the line segment [a;b] on which the point p lies
     */
    static boolean side(Point2d a, Point2d b, Point2d p)
    {
        return distanceToLine(a, b, p) > 0;
    }

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
    public Point2d[] getVertices()
    {
        return vertices.clone();
    }

    @Override
    public boolean contains(Point2d p)
    {
        // Copyright 2001, 2012, 2021 Dan Sunday
        // This code may be freely used and modified for any purpose
        // providing that this copyright notice is included with it.
        // There is no warranty for this code, and the author of it cannot
        // be held liable for any real or imagined damage from its use.
        // Users of this code must verify correctness for their application.

        var wn = 0; // the winding number counter
        var j = vertices.length - 1;

        // loop through all edges of the polygon
        for (var i = 0; i < vertices.length; j = i++)
        {
            if (p.isOnLine(vertices[i], vertices[j]))
            {
                return true;
            }
            if (vertices[i].getY() <= p.getY())
            {
                // start y <= P.y
                if (vertices[j].getY() > p.getY()) // an upward crossing
                {
                    if (distanceToLine(vertices[i], vertices[j], p) > 0) // P left of  edge
                    {
                        ++wn; // have  a valid up intersect
                    }
                }
            }
            else
            {
                // start y > P.y (no test needed)
                if (vertices[j].getY() <= p.getY()) // a downward crossing
                {
                    if (distanceToLine(vertices[i], vertices[j], p) < 0) // P right of  edge
                    {
                        --wn; // have  a valid down intersect
                    }
                }
            }
        }

        return wn != 0;
    }

    @Override
    public double getMaxDiameter()
    {
        // TODO: fails for excentered polygons, gives a value too big
        return Arrays.stream(vertices).mapToDouble(Point2d::norm).max().orElseThrow() * 2;
    }

    @Override
    public Stream<Point2d> getShell(double shipSize)
    {
        if (shipSize != lastShipSize)
        {
            System.arraycopy(vertices, 0, lastShell, 0, vertices.length);

            for (int i = 0; i < lastShell.length; i++)
            {
                var cur = lastShell[i];
                var ni = (i + 1) % lastShell.length;
                var nex = lastShell[ni];
                var dta = nex.sub(cur);

                var change = Point2d.fromPolar(shipSize, dta.angle()).ortho();
                if (cur.sub(change).normSquared() < cur.normSquared())
                {
                    change = change.mul(-1);
                }
                lastShell[i] = cur.sub(change);
                lastShell[ni] = nex.sub(change);
            }

            lastShipSize = shipSize;
        }

        return Arrays.stream(lastShell);
    }

    @Override
    public boolean linePassesThrough(Point2d a, Point2d b, double shipSize)
    {
        var pts = getShell(shipSize).toArray(Point2d[]::new);

        for (int i = 0; i < pts.length; i++)
        {
            var cur = pts[i];
            var nex = pts[(i + 1) % pts.length];

            if (side(cur, a, b) != side(nex, a, b) && side(cur, nex, a) != side(cur, nex, b))
            {
                return true;
            }
        }

        return false;
    }
}
