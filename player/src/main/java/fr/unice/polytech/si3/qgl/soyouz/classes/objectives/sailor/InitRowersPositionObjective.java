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
    private final List<SailorXMovementObjective> sailorsToMove;
    private final List<Integer> linesOnBoat;
    private final List<Integer> linesWithTwoOarsOnBoat;
    private final List<Marin> sailorsSortedByX;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param rowers All sailors disposed to oar.
     */
    public InitRowersPositionObjective(Bateau ship, List<Marin> rowers)
    {
        sailorsToMove = new ArrayList<>();
        linesOnBoat = new ArrayList<>();
        linesWithTwoOarsOnBoat = new ArrayList<>();
        sailorsSortedByX = getAllSailorsSortedByXPos(rowers);
        determineLinesOnBoat(ship);

        int sailorExceeding = sailorsSortedByX.size() - linesOnBoat.size();
        sailorExceeding = Math.max(sailorExceeding, 0);

        generateSubObjectives(sailorExceeding);
    }

    /**
     * Generate all sub objectives AKA x-placement objectives.
     * @param nbSailorExceeding The number of sailor in addition to the number of lines.
     */
    private void generateSubObjectives(int nbSailorExceeding) {
        int nbSailorPlaced = 0;
        for (Integer line : linesOnBoat)
        {
            if (nbSailorPlaced >= sailorsSortedByX.size()) break;
            sailorsToMove.add(new SailorXMovementObjective(sailorsSortedByX.get(nbSailorPlaced), line));
            nbSailorPlaced++;
            if (nbSailorExceeding > 0 && linesWithTwoOarsOnBoat.contains(line))
            {
                sailorsToMove.add(new SailorXMovementObjective(sailorsSortedByX.get(nbSailorPlaced), line));
                nbSailorExceeding--;
                nbSailorPlaced++;
            }
        }
    }

    /**
     * Determine every lines (X) where there is an oar or two.
     * @param ship The ship.
     */
    private void determineLinesOnBoat(Bateau ship)
    {
        List<OnboardEntity> oars = Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Rame).collect(Collectors.toList());
        oars.forEach(oar -> {
            if (!linesOnBoat.contains(oar.getX()))
                linesOnBoat.add(oar.getX());
            else linesWithTwoOarsOnBoat.add(oar.getX());
        });
        Collections.sort(linesOnBoat);
        Collections.sort(linesWithTwoOarsOnBoat);
    }

    /**
     * Sort all sailor by their x position Asc.
     * @param sailors all sailors ready to oar.
     * @return a sorted list of sailors.
     */
    private List<Marin> getAllSailorsSortedByXPos(List<Marin> sailors)
    {
        return sailors.stream()
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