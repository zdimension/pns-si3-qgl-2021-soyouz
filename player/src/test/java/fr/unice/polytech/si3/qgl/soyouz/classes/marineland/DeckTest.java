package fr.unice.polytech.si3.qgl.soyouz.classes.marineland;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest
{

    Deck deck;

    @BeforeEach
    void init()
    {
        deck = new Deck(2, 1);
    }

    @Test
    void getWidth()
    {
        assertEquals(2, deck.getWidth());
    }

    @Test
    void getLength()
    {
        assertEquals(1, deck.getLength());
    }

    @Test
    void toStringTest()
    {
        assertEquals("Deck{width=2, length=1}", deck.toString());
    }
}