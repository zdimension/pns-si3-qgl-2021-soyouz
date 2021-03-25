package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindTest
{

    Wind wind;

    @BeforeEach
    void setUp()
    {
        wind = new Wind(1, 100);
    }

    @Test
    void getOrientation()
    {
        assertEquals(1, wind.getOrientation());
    }

    @Test
    void getStrength()
    {
        assertEquals(100, wind.getStrength());
    }
}