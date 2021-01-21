package fr.unice.polytech.si3.qgl.teamid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CockpitTest {

    Cockpit cockpit;

    @BeforeEach
    void setUp() {
        this.cockpit = new Cockpit();
    }

    @Test
    void nextRoundTest() {
        assertEquals("[]", this.cockpit.nextRound("{}"));
    }
}