package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WindTest
{

    Wind wind;
    Wind wind2;
    Wind wind3;
    Bateau boat;
    double epsilon = 0.0001;

    @BeforeEach
    void init()
    {
        wind = new Wind(0,1);
        wind2 = new Wind(Math.PI,1);
        wind3 = new Wind(Math.PI/2,1);
        boat = new Bateau("Bato",new Deck(5,5),new OnboardEntity[]{new Voile(1,1,false) });
        boat.setPosition(new Position(0,0,0));
    }

    @Test
    void windAdditionalSpeedWithWindAt0()
    {
        assertEquals(0,wind.windAdditionalSpeed(0,0,null));
        assertEquals(0,wind.windAdditionalSpeed(1,0,boat));
        assertEquals(1,wind.windAdditionalSpeed(1,1,boat));

        boat.setPosition(new Position(0,0,Math.PI));
        assertTrue(1 - Math.abs(wind.windAdditionalSpeed(1,1,boat)) <= epsilon); // return around -1, using epsilon to compare doubles
        boat.setPosition(new Position(0,0,Math.PI/2));
        assertTrue(Math.abs(wind.windAdditionalSpeed(1,1,boat)) <= epsilon); // return around 0, using epsilon to compare doubles
    }

    @Test
    void windAdditionalSpeedWithWindAtPI()
    {
        assertTrue(1 - Math.abs(wind2.windAdditionalSpeed(1,1,boat)) <= epsilon); // return around -1, using epsilon to compare doubles

    }

    @Test
    void windAdditionalSpeedWithWindAtPIOn2()
    {
        boat.setPosition(new Position(0,0,-Math.PI/2));
        assertTrue(1 - Math.abs(wind3.windAdditionalSpeed(1,1,boat)) <= epsilon); // return around -1, using epsilon to compare doubles

    }
}
