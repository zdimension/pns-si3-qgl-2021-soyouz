package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WantedSailorConfigTest
{

    WantedSailorConfig wsc;

    @BeforeEach
    void setUp()
    {
        wsc = new WantedSailorConfig(Pair.of(2,2), Gouvernail.ALLOWED_ROTATION.first, Set.of(new PosOnShip(1,1)));
    }

    @Test
    void copy()
    {
        WantedSailorConfig wscCopy = WantedSailorConfig.copy(wsc);
        assertEquals(wsc, wscCopy);
    }

    @Test
    void getOarConfig()
    {
        assertNotEquals(Pair.of(1,2), wsc.getOarConfig());
        assertEquals(Pair.of(2,2), wsc.getOarConfig());
        assertNotEquals(Pair.of(2,3), wsc.getOarConfig());
    }

    //TODO NOM TROMPEUR ET OVERKILL
    //@Test
    //void getAbsConfig()
    //{
    //    Set<? extends OnboardEntity> rudder = wsc.getAbsEntityConfig();
    //    assertTrue(rudder.contains(wsc.getGouvernail()));
    //}

    //TODO NOM TROMPEUR ET OVERKILL
    @Test
    void getAbsConfigPos()
    {
        Set<PosOnShip> rudderPos = wsc.getAbsConfig();
        assertTrue(rudderPos.contains(new PosOnShip(1, 1)));
    }

    @Test
    void getRotation()
    {
        assertNotEquals(-0.78539816338, wsc.getRotation());
        assertEquals(Gouvernail.ALLOWED_ROTATION.first, wsc.getRotation());
        assertNotEquals(-0.78539816340, wsc.getRotation());
    }

    @Test
    void decrementOarUsage()
    {
        assertEquals(Pair.of(2,2), wsc.getOarConfig());
        boolean decrement = wsc.decrementOarUsage();
        assertTrue(decrement);
        assertEquals(Pair.of(1,1), wsc.getOarConfig());
        assertNotEquals(Pair.of(2,2), wsc.getOarConfig());
        decrement = wsc.decrementOarUsage();
        assertTrue(decrement);
        assertEquals(Pair.of(0,0), wsc.getOarConfig());
        assertNotEquals(Pair.of(1,1), wsc.getOarConfig());
        decrement = wsc.decrementOarUsage();
        assertFalse(decrement);
        assertEquals(Pair.of(0,0), wsc.getOarConfig());
        assertNotEquals(Pair.of(-1,-1), wsc.getOarConfig());
    }

    //@Test
    //void getGouvernail()
    //{
    //    assertEquals(new Gouvernail(1, 1), wsc.getGouvernail());
    //}

    @Test
    void testEquals()
    {
        boolean equals = wsc.equals(wsc);
        boolean equals2 = wsc.equals(new WantedSailorConfig(Pair.of(2,2), Gouvernail.ALLOWED_ROTATION.first, Set.of(new PosOnShip(1,1))));
        boolean notEquals = wsc.equals(new Gouvernail(1,1));
        boolean notEquals2 = wsc.equals(new WantedSailorConfig(Pair.of(2,2), Gouvernail.ALLOWED_ROTATION.first - 0.000001, Set.of(new PosOnShip(1,1))));
        assertTrue(equals);
        assertTrue(equals2);
        assertFalse(notEquals);
        assertFalse(notEquals2);
    }

    @Test
    void testHashCode()
    {
        assertEquals(wsc.hashCode(), wsc.hashCode());
        assertEquals(wsc.hashCode(), new WantedSailorConfig(Pair.of(2,2), Gouvernail.ALLOWED_ROTATION.first, Set.of(new PosOnShip(1,1))).hashCode());
        assertNotEquals(wsc.hashCode(), new WantedSailorConfig(Pair.of(2,2), Gouvernail.ALLOWED_ROTATION.first, Set.of(new PosOnShip(1,2))).hashCode());
    }
}