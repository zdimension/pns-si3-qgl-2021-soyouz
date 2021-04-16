package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OarConfigurationTest
{

    OarConfiguration configurationLeft;
    OarConfiguration configurationStraight;
    OarConfiguration configurationRight;

    @BeforeEach
    void init()
    {
        configurationLeft = new OarConfiguration(1, 3, 6);
        configurationStraight = new OarConfiguration(2, 2, 6);
        configurationRight = new OarConfiguration(3, 1, 6);
    }

    @Test
    void getSailorConfiguration()
    {
        assertEquals(PosOnShip.of(1,3), configurationLeft.getSailorConfiguration());
        assertNotEquals(PosOnShip.of(2,3), configurationLeft.getSailorConfiguration());
        assertEquals(PosOnShip.of(2,2), configurationStraight.getSailorConfiguration());
        assertNotEquals(PosOnShip.of(2,3), configurationStraight.getSailorConfiguration());
        assertEquals(PosOnShip.of(3,1), configurationRight.getSailorConfiguration());
        assertNotEquals(PosOnShip.of(3,2), configurationRight.getSailorConfiguration());
    }

    @Test
    void getAngleOfRotation()
    {
        assertEquals(1.0471975511965976, configurationLeft.getAngleOfRotation());
        assertEquals(0, configurationStraight.getAngleOfRotation());
        assertEquals(-1.0471975511965976, configurationRight.getAngleOfRotation());
        assertEquals(-(configurationRight.getAngleOfRotation()), configurationLeft.getAngleOfRotation());
    }

    @Test
    void getLinearSpeed()
    {
        assertEquals(110.0, configurationLeft.getLinearSpeed());
        assertEquals(configurationLeft.getLinearSpeed(), configurationStraight.getLinearSpeed());
        assertEquals(configurationStraight.getLinearSpeed(), configurationRight.getLinearSpeed());
        assertEquals(configurationRight.getLinearSpeed(), configurationLeft.getLinearSpeed());
    }
}