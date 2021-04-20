package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RowersObjectiveTest
{
    RowersObjective roLeft;
    RowersObjective roRight;

    @BeforeEach
    void setUp()
    {
        OnboardEntity[] ent = {
            new Rame(0, 0),
            new Rame(0, 2),
            new Rame(1, 0),
            new Rame(1, 2),
            new Rame(2, 2)
        };
        Bateau ship = new Bateau("Peqoq", new Deck(3, 3), ent);
        List<Marin> immutableRower = new ArrayList<>();
        immutableRower.add(new Marin(0, 2, 2, "Tom"));
        immutableRower.add(new Marin(2, 1, 2, "Tim"));
        List<Marin> mutableRower = new ArrayList<>();
        mutableRower.add(new Marin(1, 0, 1, "Tam"));
        List<Marin> immutableRower2 = new ArrayList<>();
        immutableRower2.add(new Marin(3, 0, 0, "Tum"));
        immutableRower2.add(new Marin(4, 1, 0, "Tym"));
        List<Marin> mutableRower2 = new ArrayList<>();
        List<Marin> immutableRowerLeft = immutableRower.stream().filter(rower -> rower.getY() == 0)
            .collect(Collectors.toList());
        List<Marin> immutableRowerRight = immutableRower.stream().filter(rower -> rower.getY() > 0)
            .collect(Collectors.toList());
        List<Marin> immutableRower2Left =
            immutableRower2.stream().filter(rower -> rower.getY() == 0)
                .collect(Collectors.toList());
        List<Marin> immutableRower2Right =
            immutableRower2.stream().filter(rower -> rower.getY() > 0)
                .collect(Collectors.toList());
        mutableRower2.add(new Marin(5, 0, 1, "Tem"));
        roLeft = new RowersObjective(ship, mutableRower, immutableRowerLeft, immutableRowerRight,
            Pair.of(2, 1));
        roRight = new RowersObjective(ship, mutableRower2, immutableRower2Left,
            immutableRower2Right, Pair.of(1, 2));
    }

    @Test
    void isValidated()
    {
        assertFalse(roLeft.isValidated());
        assertFalse(roRight.isValidated());
        roLeft.resolve();
        roRight.resolve();
        assertTrue(roLeft.isValidated());
        assertTrue(roRight.isValidated());
    }

    @Test
    void resolve()
    {
        List<MoveAction> nbMoveActionRoLeft = Util.filterType(roLeft.resolve().stream()
            .filter(act -> act instanceof MoveAction), MoveAction.class).collect(Collectors.toList());
        long nbOarActionRoLeft =
            roLeft.resolve().stream().filter(act -> act instanceof OarAction).count();
        List<MoveAction> nbMoveActionRoRight = Util.filterType(roRight.resolve().stream()
            .filter(act -> act instanceof MoveAction), MoveAction.class).collect(Collectors.toList());
        long nbOarActionRoRight =
            roRight.resolve().stream().filter(act -> act instanceof OarAction).count();
        assertEquals(1, nbMoveActionRoLeft.size());
        assertEquals(3, nbOarActionRoLeft);
        assertEquals(1, nbMoveActionRoRight.size());
        assertEquals(3, nbOarActionRoRight);
        nbMoveActionRoLeft.forEach(act ->
        {
            assertEquals(-1, act.getYDistance());
            assertEquals(0, act.getXDistance());
        });
        nbMoveActionRoRight.forEach(act ->
        {
            assertEquals(1, act.getYDistance());
            assertEquals(0, act.getXDistance());
        });
    }
}