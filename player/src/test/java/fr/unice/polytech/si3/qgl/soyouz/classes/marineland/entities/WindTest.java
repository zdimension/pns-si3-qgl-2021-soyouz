package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class WindTest
{

    Wind wind;

    @BeforeEach
    void init()
    {
        wind = new Wind(0,2);
    }

    @Test
    void windAdditionnalSpeed()
    {
        assertEquals(0,wind.windAdditionnalSpeed(0,0,null));
    }
}
