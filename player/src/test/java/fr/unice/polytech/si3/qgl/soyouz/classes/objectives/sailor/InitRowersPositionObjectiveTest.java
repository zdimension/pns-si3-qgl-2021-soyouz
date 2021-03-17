package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InitRowersPositionObjectiveTest
{
    Bateau boat;
    InitRowersPositionObjective obj;
    Marin[] marins;
    GameState state;

    @BeforeEach
    void init(){
        marins = new Marin[]{new Marin(1,0,0,"Marin1"),new Marin(2,2,0,"Marin2"),new Marin(3,3,0,"Marin3"), new Marin(4,4,0,"Marin4"),new Marin(5,1,0,"Marin5")};
        boat = new Bateau("Bato",
            new Deck(3,5),
            new OnboardEntity[]{ new Rame(1,0),new Rame(1,3), new Rame(2,0), new Rame(3,0), new Rame(3,3), new Gouvernail(4,2) });
        obj = new InitRowersPositionObjective(boat, Arrays.asList(marins));
        state = new GameState(new InitGameParameters(new RegattaGoal(new Checkpoint[]{new Checkpoint(new Position(0,0,0),new Circle(2.0))}),boat,marins), new NextRoundParameters(boat,null,null));
    }

    @Test
    void resolveTest(){
        List<GameAction> actions = obj.resolve(state);
        System.out.println(actions);
        assertEquals(2, actions.size());
    }

    @Test
    void isValidated(){
        assertFalse(obj.isValidated(state));
    }


}
