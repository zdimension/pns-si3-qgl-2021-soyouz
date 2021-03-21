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
        assertFalse(voile.isOpenned());
        assertTrue(voile2.isOpenned());
    }

    @Test
    void setOpened(){
        voile.setOpenned(true);
        assertTrue(voile.isOpenned());
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
