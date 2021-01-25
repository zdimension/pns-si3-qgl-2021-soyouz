package fr.unice.polytech.si3.qgl.soyouz.classes.parameters;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class NextRoundParametersTest {
    Cockpit cockpit;
    NextRoundParameters np;

    @BeforeEach
    void setUp() {
        this.cockpit = new Cockpit();
    }

    @Test
    void shipTest(){
        cockpit.nextRound("{\"ship\": {\n" +
                "    \"type\": \"ship\",\n" +
                "    \"life\": 100,\n" +
                "    \"position\": {\n" +
                "      \"x\": 10.654,\n" +
                "      \"y\": 3,\n" +
                "      \"orientation\": 2.05\n" +
                "    },\n" +
                "    \"name\": \"Les copaings d'abord!\",\n" +
                "    \"deck\": {\n" +
                "      \"width\": 2,\n" +
                "      \"length\": 1\n" +
                "    },\n" +
                "    \"entities\": [\n" +
                "      {\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0,\n" +
                "        \"type\": \"oar\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }}");
        np = cockpit.getNp();
        Bateau bateau = np.getShip();
        assertEquals("Les copaings d'abord!", bateau.getName());
        assertEquals(100, bateau.getLife());
        assertEquals(2, bateau.getEntities().length);
        //Boat position
        Position pos = new Position(10.654,3,2.05);
        assertTrue(bateau.getPosition().equals(pos));
        //Deck size
        assertEquals(2,bateau.getDeck().getWidth());
        assertEquals(1,bateau.getDeck().getLength());
    }
    //TODO visibleEntities for next release
}
