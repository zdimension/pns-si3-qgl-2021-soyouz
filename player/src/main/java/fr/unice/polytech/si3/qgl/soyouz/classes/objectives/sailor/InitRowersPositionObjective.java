package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.Objective;

import java.util.*;
import java.util.stream.Collectors;

public class InitRowersPositionObjective implements Objective
{
    List<SailorXMovementObjective> sailorsToMove;

    public InitRowersPositionObjective(Bateau ship, Marin[] rowers)
    {
        sailorsToMove = new ArrayList<>();
        //TODO CREER UN OBJET LINEONBOAT AVEC LES ENTITEES
        List<Integer> linesOnBoat = determineLinesOnBoat(ship);
        List<Marin> sailorsSortedByX = getAllSailorsSortedByXPos(rowers);
        int nbSailorPlaced = 0;

        for (int i = 0; i < sailorsSortedByX.size() && i < linesOnBoat.size(); i++)
        {
            sailorsToMove.add(new SailorXMovementObjective(sailorsSortedByX.get(i), linesOnBoat.get(i)));
            nbSailorPlaced++;
        }
        //TODO A REVISER POUR UN PLACEMENT PLUS OPTI DES MARINS RESTANTS
        for (int i = 0; i < sailorsSortedByX.size() - nbSailorPlaced; i++){
            sailorsToMove.add(new SailorXMovementObjective(sailorsSortedByX.get(sailorsSortedByX.size() - (i + 1)),
                linesOnBoat.get(linesOnBoat.size() - i - 1)));
        }
    }

    private List<Integer> determineLinesOnBoat(Bateau ship)
    {
        List<Integer> lines = new ArrayList<>();
        List<OnboardEntity> oars = Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Rame).collect(Collectors.toList());
        oars.forEach(oar -> {
            if (!lines.contains(oar.getX()))
                lines.add(oar.getX());
        });
        Collections.sort(lines);
        return lines;
    }

    private List<Marin> getAllSailorsSortedByXPos(Marin[] sailors)
    {
        return Arrays.stream(sailors)
            .sorted(Comparator.comparing(Marin::getX))
            .collect(Collectors.toList());
    }

    /**
     * Determine if the goal is reached.
     *
     * @param state of the game
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated(GameState state)
    {
        long notDone = sailorsToMove.stream().filter(obj -> !obj.isValidated(state)).count();
        return notDone == 0;
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @param state of the game
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve(GameState state)
    {
        List<GameAction> moveActions = new ArrayList<>();
        sailorsToMove.forEach(obj -> {
            if (!obj.isValidated(state))
                moveActions.addAll(obj.resolve(state));
        });
        return moveActions;
    }
}
