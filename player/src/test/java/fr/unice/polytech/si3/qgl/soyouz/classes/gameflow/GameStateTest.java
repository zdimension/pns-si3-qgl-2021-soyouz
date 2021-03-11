package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GameStateTest
{

    InitGameParameters ip;
    NextRoundParameters np;
    GameState gs;

    @BeforeEach
    void init()
    {
        ip = new InitGameParameters(null,null,null);
        np = new NextRoundParameters(null, null, null);
        gs = new GameState(ip, np);
    }

    @Test
    void getIpTest()
    {
        assertEquals(ip, gs.getIp());
        assertNotEquals(new InitGameParameters(null,null,null), gs.getIp());
    }

    @Test
    void getNpTest()
    {
        assertEquals(np, gs.getNp());
        assertNotEquals(new NextRoundParameters(null, null, null), gs.getNp());
    }
}