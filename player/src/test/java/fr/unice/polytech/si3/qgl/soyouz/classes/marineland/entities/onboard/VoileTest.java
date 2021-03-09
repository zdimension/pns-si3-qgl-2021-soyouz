package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VoileTest
{
    Voile voile;
    Voile voile2;

    @BeforeEach
    void init()
    {
        voile = new Voile(1,1,false);
        voile2 = new Voile(2,2,true);
    }

    @Test
    void isOpenedTest(){
        assertFalse(voile.isOpened());
        assertTrue(voile2.isOpened());
    }

    @Test
    void setOpened(){
        voile.setOpened(true);
        assertTrue(voile.isOpened());
    }

    @Test
    void equals(){
        boolean equals = voile.equals(voile);
        boolean notEquals = voile.equals(null);
        boolean notEquals2 = voile.equals(voile2);

        assertFalse(notEquals);
        assertFalse(notEquals2);
        assertTrue(equals);
    }

    @Test
    void hashCodeTest(){
        assertNotEquals(voile.hashCode(), voile2.hashCode());
    }

}
