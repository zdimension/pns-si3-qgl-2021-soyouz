package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowersConfigHelperTest
{
    RowersConfigHelper leftTurn;
    RowersConfigHelper straightForward;
    RowersConfigHelper smallLeft;
    RowersConfigHelper smallRight;
    RowersConfigHelper rightTurn;
    RowersConfigHelper notBalancedLeft;
    RowersConfigHelper notBalancedRight;

    @BeforeEach
    void setUp()
    {
        leftTurn = new RowersConfigHelper(Math.PI/2, 200, 3, 2, 2, 8);
        straightForward = new RowersConfigHelper(0, 500, 3, 2, 2, 8);
        smallLeft = new RowersConfigHelper(Math.PI/10, 10, 3, 2, 2, 8);
        smallRight = new RowersConfigHelper(-Math.PI/10, 10, 3, 2, 2, 8);
        rightTurn = new RowersConfigHelper(-Math.PI/2, 200, 3, 2, 2, 8);
        notBalancedLeft = new RowersConfigHelper(0, 500, 3, 3, 1, 8);
        notBalancedRight = new RowersConfigHelper(0, 500, 3, 1, 3, 8);
    }

    @Test
    void findOptRowersConfiguration()
    {
        assertEquals(Pair.of(1,5), leftTurn.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(3,3), straightForward.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(0,1), smallLeft.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(1,0), smallRight.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(5,1), rightTurn.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(3,3), notBalancedLeft.findOptRowersConfiguration().getSailorConfiguration());
        assertEquals(Pair.of(3,3), notBalancedRight.findOptRowersConfiguration().getSailorConfiguration());
    }
}