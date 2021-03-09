package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ComputeMoveSailorTest
{

    ComputeMoveSailor cms;
    Rame oar1, oar2, oar3, oar4, oar5, oar6;
    Gouvernail rudder;
    Voile sail1, sail2;
    Marin sailor;
    
    @BeforeEach
    void setUp()
    {
        oar1 = new Rame(1, 0);
        oar2 = new Rame(2, 0);
        oar3 = new Rame(3, 0);
        oar4 = new Rame(1, 3);
        oar5 = new Rame(2, 3);
        oar6 = new Rame(7, 4);
        rudder = new Gouvernail(0, 0);
        sail1 = new Voile(2, 2, false);
        sail2 = new Voile(3, 3, false);
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        List<OnboardEntity> entities = new ArrayList<>();
        entities.add(oar1);
        entities.add(oar2);
        entities.add(oar3);
        entities.add(oar4);
        entities.add(oar5);
        entities.add(oar6);
        entities.add(rudder);
        entities.add(sail1);
        entities.add(sail2);
        cms = new ComputeMoveSailor(sailor, entities);
    }

    @Test
    void getSailor()
    {
        assertEquals(sailor, cms.getSailor());
    }

    @Test
    void getOars()
    {
        assertEquals(5, cms.getOars().size());
        assertTrue(cms.getOars().contains(oar1));
        assertTrue(cms.getOars().contains(oar2));
        assertTrue(cms.getOars().contains(oar3));
        assertTrue(cms.getOars().contains(oar4));
        assertTrue(cms.getOars().contains(oar5));
        assertFalse(cms.getOars().contains(oar6));
    }

    @Test
    void getReachableEntities()
    {
        assertEquals(7, cms.getReachableEntities().size());
        assertTrue(cms.getReachableEntities().contains(oar1));
        assertTrue(cms.getReachableEntities().contains(oar2));
        assertTrue(cms.getReachableEntities().contains(oar3));
        assertTrue(cms.getReachableEntities().contains(oar4));
        assertTrue(cms.getReachableEntities().contains(oar5));
        assertFalse(cms.getReachableEntities().contains(oar6));
        assertTrue(cms.getReachableEntities().contains(rudder));
        assertTrue(cms.getReachableEntities().contains(sail1));
        assertFalse(cms.getReachableEntities().contains(sail2));
    }

    //TODO NOM TROMPEUR
    @Test
    void getReachableSingleEntities()
    {
        assertEquals(1, cms.getReachableSingleEntities().size());
        assertFalse(cms.getReachableSingleEntities().contains(oar1));
        assertFalse(cms.getReachableSingleEntities().contains(oar2));
        assertFalse(cms.getReachableSingleEntities().contains(oar3));
        assertFalse(cms.getReachableSingleEntities().contains(oar4));
        assertFalse(cms.getReachableSingleEntities().contains(oar5));
        assertFalse(cms.getReachableSingleEntities().contains(oar6));
        assertTrue(cms.getReachableSingleEntities().contains(rudder));
        assertFalse(cms.getReachableSingleEntities().contains(sail1));
        assertFalse(cms.getReachableSingleEntities().contains(sail2));
    }

    @Test
    void numberRoundsToReachEntity()
    {
        assertEquals(0, cms.numberRoundsToReachEntity(oar1));
        assertEquals(0, cms.numberRoundsToReachEntity(oar2));
        assertEquals(0, cms.numberRoundsToReachEntity(oar3));
        assertEquals(0, cms.numberRoundsToReachEntity(oar4));
        assertEquals(0, cms.numberRoundsToReachEntity(oar5));
        assertEquals(0, cms.numberRoundsToReachEntity(sail1));
        assertEquals(0, cms.numberRoundsToReachEntity(rudder));
        assertEquals(0, cms.numberRoundsToReachEntity(rudder.getPosCoord()));
        assertEquals(1, cms.numberRoundsToReachEntity(sail2));
        assertEquals(1, cms.numberRoundsToReachEntity(sail2.getPosCoord()));
        assertEquals(2, cms.numberRoundsToReachEntity(oar6.getPosCoord()));
        assertEquals(2, cms.numberRoundsToReachEntity(oar6));
    }
}