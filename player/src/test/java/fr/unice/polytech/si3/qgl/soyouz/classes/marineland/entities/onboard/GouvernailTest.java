package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GouvernailTest
{
    Gouvernail rudder;

    @BeforeEach
    void setUp()
    {
        rudder = new Gouvernail(1, 2);
    }

    @Test
    void getX()
    {
        assertNotEquals(0, rudder.getX());
        assertEquals(1, rudder.getX());
        assertNotEquals(2, rudder.getX());
    }

    @Test
    void getY()
    {
        assertNotEquals(1, rudder.getY());
        assertEquals(2, rudder.getY());
        assertNotEquals(3, rudder.getY());
    }

    @Test
    void getPosCoord()
    {
        assertNotEquals(Pair.of(1,3), rudder.getPosCoord());
        assertEquals(Pair.of(1,2), rudder.getPosCoord());
        assertNotEquals(Pair.of(2,2), rudder.getPosCoord());
    }

    @Test
    void getPos()
    {
        assertNotEquals(new PosOnShip(1,3), rudder.getPos());
        assertEquals(new PosOnShip(1,2), rudder.getPos());
        assertNotEquals(new PosOnShip(2,2), rudder.getPos());
    }

    @Test
    void rangeTest()
    {
        assertEquals(-0.78539816339,Gouvernail.ALLOWED_ROTATION.first);
        assertEquals(0.78539816339,Gouvernail.ALLOWED_ROTATION.second);
    }
}
