package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Specific type to stock all entities for a specific X line on the ship.
 */
public class LineOnBoat implements Comparable<LineOnBoat>
{
    private final List<Rame> oars;
    private final int x;
    private Gouvernail rudder;
    private Voile sail;
    private Vigie watch;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param x    The abscissa of the line.
     */
    public LineOnBoat(Bateau ship, int x)
    {
        this.x = x;
        oars = new ArrayList<>();
        rudder = null;
        sail = null;
        watch = null;

        List<OnboardEntity> ent = Arrays.stream(ship.getEntities())
            .filter(entity -> entity.getX() == x).collect(Collectors.toList());

        ent.forEach(entity ->
        {
            if (entity instanceof Rame)
            {
                oars.add((Rame) entity);
            }
            else if (entity instanceof Gouvernail)
            {
                rudder = (Gouvernail) entity;
            }
            else if (entity instanceof Voile)
            {
                sail = (Voile) entity;
            }
            else if (entity instanceof Vigie)
            {
                watch = (Vigie) entity;
            }
        });
    }

    /**
     * Getter.
     *
     * @return the list of oars on the line.
     */
    public List<Rame> getOars()
    {
        return oars;
    }

    /**
     * Getter.
     *
     * @return the rudder if there is one.
     */
    public Gouvernail getRudder()
    {
        return rudder;
    }


    /**
     * Getter.
     *
     * @return the sail if there is one.
     */
    public Voile getSail()
    {
        return sail;
    }

    /**
     * Getter.
     *
     * @return the watch if there is one.
     */
    public Vigie getWatch()
    {
        return watch;
    }

    /**
     * Getter.
     *
     * @return the abscissa of the line.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Generic equals override.
     *
     * @param o The second object.
     * @return if two lines are equal or not.
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof LineOnBoat))
        {
            return false;
        }
        if (o == this)
        {
            return true;
        }
        return ((LineOnBoat) o).x == this.x;
    }

    /**
     * Compare two lines based on their abscissa.
     *
     * @param line The second Line.
     * @return the result of the comparison.
     */
    @Override
    public int compareTo(LineOnBoat line)
    {
        if (this.x == line.x)
        {
            return 0;
        }
        return this.x < line.x ? -1 : 1;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
