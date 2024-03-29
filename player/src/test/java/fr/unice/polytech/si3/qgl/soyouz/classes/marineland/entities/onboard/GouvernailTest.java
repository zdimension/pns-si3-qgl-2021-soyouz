package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
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
    void getPos()
    {
        assertNotEquals(PosOnShip.of(1, 3), rudder.getPos());
        assertEquals(PosOnShip.of(1, 2), rudder.getPos());
        assertNotEquals(PosOnShip.of(2, 2), rudder.getPos());
    }
}
