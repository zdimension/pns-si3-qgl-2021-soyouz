package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrigonometryTest {

    @Test
    void oarLinearSpeed() {
        assertEquals(Pair.of(20.0, (double) (165 * 3 / 6)), Trigonometry.oarLinearSpeed(20, 3, 6));
    }
}