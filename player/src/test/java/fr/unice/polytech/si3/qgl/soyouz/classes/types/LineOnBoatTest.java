package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LineOnBoatTest
{
    LineOnBoat lineWithTwoOarsAndARudder;
    LineOnBoat lineWithOneOarAndASail;
    LineOnBoat lineWithTwoOars;
    LineOnBoat lineWithOneOar;

    @BeforeEach
    void init()
    {
        OnboardEntity[] ent = {
            new Rame(0, 0),
            new Rame(0, 4),
            new Gouvernail(0, 2),
            new Rame(1,0),
            new Voile(1, 2,false),
            new Rame(2, 0),
            new Rame(2, 4),
            new Rame(3, 0)
        };
        Bateau ship = new Bateau("bateau", new Deck(5, 9), ent);
        lineWithTwoOarsAndARudder = new LineOnBoat(ship, 0);
        lineWithOneOarAndASail = new LineOnBoat(ship, 1);
        lineWithTwoOars = new LineOnBoat(ship, 2);
        lineWithOneOar = new LineOnBoat(ship, 3);
    }

    @Test
    void getOars()
    {
        assertEquals(2, lineWithTwoOarsAndARudder.getOars().size());
        assertEquals(1, lineWithOneOarAndASail.getOars().size());
        assertEquals(2, lineWithTwoOars.getOars().size());
        assertEquals(1, lineWithOneOar.getOars().size());
    }

    @Test
    void getRudder()
    {
        assertEquals(new Gouvernail(0, 2), lineWithTwoOarsAndARudder.getRudder());
        assertNull(lineWithOneOarAndASail.getRudder());
        assertNull(lineWithTwoOars.getRudder());
        assertNull(lineWithOneOar.getRudder());
    }

    @Test
    void getSail()
    {
        assertNull(lineWithTwoOarsAndARudder.getSail());
        assertEquals(new Voile(1, 2,false), lineWithOneOarAndASail.getSail());
        assertNull(lineWithTwoOars.getSail());
        assertNull(lineWithOneOar.getSail());
    }

    @Test
    void equalsTest()
    {
        assertEquals(lineWithOneOarAndASail, lineWithOneOarAndASail);
        assertNotEquals(lineWithOneOar, lineWithTwoOars);
        boolean notEquals = lineWithOneOar.equals("Hello");
        assertFalse(notEquals);
    }

    @Test
    void compareTo()
    {
        List<LineOnBoat> lines = new ArrayList<>();
        lines.add(lineWithTwoOars);
        lines.add(lineWithOneOarAndASail);
        lines.add(lineWithOneOar);
        lines.add(lineWithTwoOarsAndARudder);
        Collections.sort(lines);
        assertEquals(lineWithTwoOarsAndARudder, lines.get(0));
        assertEquals(lineWithOneOarAndASail, lines.get(1));
        assertEquals(lineWithTwoOars, lines.get(2));
        assertEquals(lineWithOneOar, lines.get(3));
        assertEquals(0, lines.get(0).compareTo(lines.get(0)));
    }

    @Test
    void hashCodeTest()
    {
        assertNotEquals(lineWithOneOar.hashCode(), lineWithTwoOars.hashCode());
    }
}