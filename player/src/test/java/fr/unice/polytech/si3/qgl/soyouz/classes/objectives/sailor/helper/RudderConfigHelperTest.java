package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RudderConfigHelperTest
{
    RudderConfigHelper inferiorRotation;
    RudderConfigHelper inRangeRotation;
    RudderConfigHelper superiorRotation;

    @BeforeEach
    void setUp()
    {
        inferiorRotation = new RudderConfigHelper(-Math.PI);
        inRangeRotation = new RudderConfigHelper(Math.PI / 5);
        superiorRotation = new RudderConfigHelper(Math.PI);
    }

    @Test
    void findOptRudderRotation()
    {
        assertEquals(-Gouvernail.ALLOWED_ROTATION, inferiorRotation.findOptRudderRotation());
        assertEquals(Math.PI / 5, inRangeRotation.findOptRudderRotation());
        assertEquals(Gouvernail.ALLOWED_ROTATION, superiorRotation.findOptRudderRotation());
    }
}