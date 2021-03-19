package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.RegattaObjective;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RegattaObjectiveTest
{

    Cockpit cockpit;
    GameState gameState;
    RegattaObjective regattaObjective;

    @BeforeEach
    void init()
    {
        cockpit = new Cockpit();
        cockpit.initGame(
            "{\n" +
                "    \"goal\": {\n" +
                "        \"mode\": \"REGATTA\",\n" +
                "        \"checkpoints\": [\n" +
                "            {\n" +
                "                \"position\": {\n" +
                "                    \"x\": 1000,\n" +
                "                    \"y\": 0,\n" +
                "                    \"orientation\": 0\n" +
                "                },\n" +
                "                \"shape\": {\n" +
                "                    \"type\": \"circle\",\n" +
                "                    \"radius\": 50\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"position\": {\n" +
                "                    \"x\": 2000,\n" +
                "                    \"y\": 0,\n" +
                "                    \"orientation\": 0\n" +
                "                },\n" +
                "                \"shape\": {\n" +
                "                    \"type\": \"circle\",\n" +
                "                    \"radius\": 50\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"shipCount\": 1,\n" +
                "    \"ship\": {\n" +
                "        \"type\": \"ship\",\n" +
                "        \"life\": 100,\n" +
                "        \"position\": {\n" +
                "            \"x\": 0,\n" +
                "            \"y\": 0,\n" +
                "            \"orientation\": 0\n" +
                "        },\n" +
                "        \"name\": \"Les copaings d'abord!\",\n" +
                "        \"deck\": {\n" +
                "            \"width\": 3,\n" +
                "            \"length\": 4\n" +
                "        },\n" +
                "        \"entities\": [\n" +
                "            {\n" +
                "                \"x\": 0,\n" +
                "                \"y\": 0,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"x\": 0,\n" +
                "                \"y\": 2,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"x\": 1,\n" +
                "                \"y\": 0,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"x\": 1,\n" +
                "                \"y\": 2,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"x\": 2,\n" +
                "                \"y\": 0,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"x\": 2,\n" +
                "                \"y\": 2,\n" +
                "                \"type\": \"oar\"\n" +
                "            },\n" +
                "            {\n" +
                "               \"x\": 3,\n" +
                "               \"y\": 1,\n" +
                "               \"type\": \"rudder\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"shape\": {\n" +
                "            \"type\": \"rectangle\",\n" +
                "            \"width\": 3,\n" +
                "            \"height\": 4,\n" +
                "            \"orientation\": 0\n" +
                "        }\n" +
                "    },\n" +
                "    \"sailors\": [\n" +
                "        {\n" +
                "            \"x\": 0,\n" +
                "            \"y\": 0,\n" +
                "            \"id\": 0,\n" +
                "            \"name\": \"Edward Teach\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"x\": 0,\n" +
                "            \"y\": 1,\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Edward Pouce\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"x\": 1,\n" +
                "            \"y\": 0,\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"Tom Pouce\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"x\": 1,\n" +
                "            \"y\": 1,\n" +
                "            \"id\": 3,\n" +
                "            \"name\": \"Tom Teach\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"x\": 2,\n" +
                "            \"y\": 2,\n" +
                "            \"id\": 4,\n" +
                "            \"name\": \"Jack Pouce\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n"
        );
    }

    void setupObjectiveInLine()
    {
        cockpit.nextRound("{\n" +
            "    \"ship\": {\n" +
            "        \"type\": \"ship\",\n" +
            "        \"life\": 100,\n" +
            "        \"position\": {\n" +
            "            \"x\": 0,\n" +
            "            \"y\": 0,\n" +
            "            \"orientation\": 0\n" +
            "        },\n" +
            "        \"name\": \"Les copaings d'abord!\",\n" +
            "        \"deck\": {\n" +
            "            \"width\": 3,\n" +
            "            \"length\": 4\n" +
            "        },\n" +
            "        \"entities\": [\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"x\": 3,\n" +
            "               \"y\": 1,\n" +
            "               \"type\": \"rudder\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"visibleEntities\": []\n" +
            "}\n");

        gameState = new GameState(cockpit.getIp(), cockpit.getNp());
        regattaObjective = new RegattaObjective((RegattaGoal)cockpit.getIp().getGoal(), cockpit.getIp());
    }

    void setupObjectiveOnLeft()
    {
        cockpit.nextRound("{\n" +
            "    \"ship\": {\n" +
            "        \"type\": \"ship\",\n" +
            "        \"life\": 100,\n" +
            "        \"position\": {\n" +
            "            \"x\": 1000,\n" +
            "            \"y\": -1000,\n" +
            "            \"orientation\": -2\n" +
            "        },\n" +
            "        \"name\": \"Les copaings d'abord!\",\n" +
            "        \"deck\": {\n" +
            "            \"width\": 3,\n" +
            "            \"length\": 4\n" +
            "        },\n" +
            "        \"entities\": [\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"x\": 3,\n" +
            "               \"y\": 1,\n" +
            "               \"type\": \"rudder\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"visibleEntities\": []\n" +
            "}\n");

        gameState = new GameState(cockpit.getIp(), cockpit.getNp());
        regattaObjective = new RegattaObjective((RegattaGoal)cockpit.getIp().getGoal(), cockpit.getIp());
    }

    void setupObjectiveOnRight()
    {
        cockpit.nextRound("{\n" +
            "    \"ship\": {\n" +
            "        \"type\": \"ship\",\n" +
            "        \"life\": 100,\n" +
            "        \"position\": {\n" +
            "            \"x\": 1000,\n" +
            "            \"y\": 1000,\n" +
            "            \"orientation\": 2\n" +
            "        },\n" +
            "        \"name\": \"Les copaings d'abord!\",\n" +
            "        \"deck\": {\n" +
            "            \"width\": 3,\n" +
            "            \"length\": 4\n" +
            "        },\n" +
            "        \"entities\": [\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"x\": 3,\n" +
            "               \"y\": 1,\n" +
            "               \"type\": \"rudder\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"visibleEntities\": []\n" +
            "}\n");

        gameState = new GameState(cockpit.getIp(), cockpit.getNp());
        regattaObjective = new RegattaObjective((RegattaGoal)cockpit.getIp().getGoal(), cockpit.getIp());
    }

    void setupObjectiveOnBoat()
    {
        cockpit.nextRound("{\n" +
            "    \"ship\": {\n" +
            "        \"type\": \"ship\",\n" +
            "        \"life\": 100,\n" +
            "        \"position\": {\n" +
            "            \"x\": 1000,\n" +
            "            \"y\": 0,\n" +
            "            \"orientation\": 0\n" +
            "        },\n" +
            "        \"name\": \"Les copaings d'abord!\",\n" +
            "        \"deck\": {\n" +
            "            \"width\": 3,\n" +
            "            \"length\": 4\n" +
            "        },\n" +
            "        \"entities\": [\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"x\": 3,\n" +
            "               \"y\": 1,\n" +
            "               \"type\": \"rudder\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"visibleEntities\": []\n" +
            "}\n");

        gameState = new GameState(cockpit.getIp(), cockpit.getNp());
        regattaObjective = new RegattaObjective((RegattaGoal)cockpit.getIp().getGoal(), cockpit.getIp());
    }

    void setupSecondObjectiveOnBoat()
    {
        cockpit.nextRound("{\n" +
            "    \"ship\": {\n" +
            "        \"type\": \"ship\",\n" +
            "        \"life\": 100,\n" +
            "        \"position\": {\n" +
            "            \"x\": 2000,\n" +
            "            \"y\": 0,\n" +
            "            \"orientation\": 0\n" +
            "        },\n" +
            "        \"name\": \"Les copaings d'abord!\",\n" +
            "        \"deck\": {\n" +
            "            \"width\": 3,\n" +
            "            \"length\": 4\n" +
            "        },\n" +
            "        \"entities\": [\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 0,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 1,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 0,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"x\": 2,\n" +
            "                \"y\": 2,\n" +
            "                \"type\": \"oar\"\n" +
            "            },\n" +
            "            {\n" +
            "               \"x\": 3,\n" +
            "               \"y\": 1,\n" +
            "               \"type\": \"rudder\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"visibleEntities\": []\n" +
            "}\n");

        gameState = new GameState(cockpit.getIp(), cockpit.getNp());
        regattaObjective = new RegattaObjective((RegattaGoal)cockpit.getIp().getGoal(), cockpit.getIp());
    }

    @Test
    void resolveWhenCpInLine()
    {
        setupObjectiveInLine();
        Bateau ship = cockpit.getIp().getShip();
        ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(5, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        assertEquals(4, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(2, oarUsedOnLeft);
        assertEquals(2, oarUsedOnRight);
    }

    @Test
    void resolveWhenCpOnLeft()
    {
        setupObjectiveOnLeft();
        Bateau ship = cockpit.getIp().getShip();
        ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(4, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        assertEquals(3, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(0, oarUsedOnLeft);
        assertEquals(3, oarUsedOnRight);
    }

    @Test
    void resolveWhenCpOnRight()
    {
        setupObjectiveOnRight();
        Bateau ship = cockpit.getIp().getShip();
        ArrayList<GameAction> gameActions = new ArrayList<>(regattaObjective.resolve(gameState));
        assertEquals(4, gameActions.size());
        List<GameAction> oarAction = gameActions.stream().filter(a -> a instanceof OarAction).collect(Collectors.toList());
        List<GameAction> rudderAction = gameActions.stream().filter(a -> a instanceof TurnAction).collect(Collectors.toList());
        assertEquals(3, oarAction.size());
        assertEquals(1, rudderAction.size());
        assertEquals(0, gameActions.size() - oarAction.size() - rudderAction.size());
        List<Marin> sailorsOaring = oarAction.stream().map(GameAction::getSailor).collect(Collectors.toList());
        int oarUsedOnLeft = 0;
        int oarUsedOnRight = 0;
        for (Marin sailor : sailorsOaring) {
            Optional<OnboardEntity> ent = ship.getEntityHere(sailor.getPos());
            if (ent.isPresent() && ent.get().getY() == 0) oarUsedOnLeft++;
            if (ent.isPresent() && ent.get().getY() == ship.getDeck().getWidth() - 1) oarUsedOnRight++;
        }
        assertEquals(3, oarUsedOnLeft);
        assertEquals(0, oarUsedOnRight);
    }

    @Test
    void isValidated()
    {
        setupObjectiveInLine();
        assertFalse(regattaObjective.isValidated(gameState));
    }

    @Test
    void updateWhenNotOnCp() throws NoSuchFieldException, IllegalAccessException
    {
        setupObjectiveInLine();
        Field checkpointNumber = RegattaObjective.class.getDeclaredField("numCheckpoint");
        checkpointNumber.setAccessible(true);
        int numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
        regattaObjective.resolve(gameState);
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
    }

    @Test
    void updateWhenOnCp() throws NoSuchFieldException, IllegalAccessException
    {
        setupObjectiveOnBoat();
        Field checkpointNumber = RegattaObjective.class.getDeclaredField("numCheckpoint");
        checkpointNumber.setAccessible(true);
        int numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
        regattaObjective.resolve(gameState);
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(1, numCheckpoint);
        setupSecondObjectiveOnBoat();
        regattaObjective.resolve(gameState);
        regattaObjective.update(gameState);
        numCheckpoint = checkpointNumber.getInt(regattaObjective);
        assertEquals(0, numCheckpoint);
    }
}