package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineOnBoat implements Comparable<LineOnBoat>
{
    private final List<Rame> oars;
    private Gouvernail rudder;
    private Voile sail;
    private final int x;

    public LineOnBoat(Bateau ship, int x)
    {
        this.x = x;
        oars = new ArrayList<>();
        rudder = null;
        sail = null;

        List<OnboardEntity> ent = Arrays.stream(ship.getEntities())
            .filter(entity -> entity.getX() == x).collect(Collectors.toList());

        ent.forEach(entity -> {
            if (entity instanceof Rame)
                oars.add((Rame) entity);
            else if (entity instanceof Gouvernail)
                rudder = (Gouvernail) entity;
            else if (entity instanceof Voile)
                sail = (Voile) entity;
        });
    }

    public List<Rame> getOars()
    {
        return oars;
    }

    public Gouvernail getRudder()
    {
        return rudder;
    }

    public Voile getSail()
    {
        return sail;
    }

    public int getX()
    {
        return x;
    }

    @Override
    public int compareTo(LineOnBoat line)
    {
        if (this.x == line.x)
            return 0;
        return this.x < line.x ? -1 : 1;
    }
}
