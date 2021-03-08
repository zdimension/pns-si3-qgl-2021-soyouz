package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class VoileTest
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
        assertFalse(voile.equals(voile2));
        assertFalse(voile.equals(null));
        assertTrue(voile.equals(voile));
    }

    @Test
    void hashCodeTest(){
        voile.hashCode();
        assertTrue(true);
    }

}
