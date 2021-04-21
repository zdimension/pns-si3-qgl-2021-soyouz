package fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.BoundingBox;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Polygon shape.
 */
public class Polygon implements Shape
{
    private final double orientation;
    private final Point2d[] vertices;
    private final Point2d center;
    private final Map<Integer, Point2d[]> shellCache = new HashMap<>();
    private final BoundingBox boundingBox;

    /**
     * Constructor.
     *
     * @param orientation The orientation of the polygon.
     * @param vertices    All points that compose the shape.
     */
    public Polygon(@JsonProperty("orientation") double orientation,
                   @JsonProperty("vertices") Point2d[] vertices)
    {
        this(orientation, vertices, getPolygonBoundingBox(vertices));
    }

    public Polygon(double orientation, Point2d[] vertices, Pair<BoundingBox, Point2d> boundingBox)
    {
        this.orientation = orientation;
        this.vertices = vertices;
        this.boundingBox = boundingBox.first;
        this.center = boundingBox.second;
    }

    private static Pair<BoundingBox, Point2d> getPolygonBoundingBox(Point2d[] vertices)
    {
        var min = new Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        var max = new Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        var sum = new Point2d(0, 0);
        for (Point2d vertex : vertices)
        {
            min = min.min(vertex);
            max = max.max(vertex);
            sum = sum.add(vertex);
        }
        return Pair.of(
            new BoundingBox(min.x, max.x, min.y, max.y),
            sum.mul(1d / vertices.length)
        );
    }

    /**
     * @param a start
     * @param b end
     * @param p point
     * @return the distance between the point p and the line segment [a;b]
     */
    private static double distanceToLine(Point2d a, Point2d b, Point2d p)
    {
        return (b.x - a.x) * (p.y - a.y)
            - (p.x - a.x) * (b.y - a.y);
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
        if (!boundingBox.contains(p))
        {
            return false;
        }

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

    Point2d[] getShellInternal(double shipSize)
    {
        var shell = new Point2d[vertices.length];

        for (int i = 0; i < shell.length; i++)
        {
            shell[i] = vertices[i].add(Point2d.fromPolar(shipSize,
                vertices[i].sub(center).angle()));
        }

        return shell;
    }

    @Override
    public Stream<Point2d> getShell(double shipSize)
    {
        return Arrays.stream(getShellArray(shipSize));
    }

    private Point2d[] getShellArray(double shipSize)
    {
        return shellCache.computeIfAbsent((int) shipSize, this::getShellInternal);
    }

    @Override
    public boolean linePassesThrough(Point2d a, Point2d b, double shipSize)
    {
        if (a.x > boundingBox.maxX && b.x > boundingBox.maxX ||
            a.x < boundingBox.minX && b.x < boundingBox.minX ||
            a.y > boundingBox.maxY && b.y > boundingBox.maxY ||
            a.y < boundingBox.minY && b.y < boundingBox.minY)
        {
            return false;
        }

        var pts = getShellArray(shipSize);

        var j = vertices.length - 1;
        for (int i = 0; i < pts.length; j = i++)
        {
            var cur = pts[i];
            var nex = pts[j];

            if (distanceToLine(cur, a, b) * distanceToLine(nex, a, b) < 0 &&
                distanceToLine(cur, nex, a) * distanceToLine(cur, nex, b) < 0)
            {
                return true;
            }
        }

        return false;
    }
}
