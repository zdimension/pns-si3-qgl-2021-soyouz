package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(1, rudder.getX());
    }

    @Test
    void getY()
    {
    }

    @Test
    void getPosCoord()
    {
    }

    @Test
    void getPos()
    {
    }

    @Test
    void isUsed()
    {
    }

    @Test
    void setUsed()
    {
    }
}