package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorXMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;

import java.util.*;
import java.util.stream.Collectors;

public class InitRowersPositionObjective implements MovingObjective
{
    private final List<MovingObjective> sailorMoveObjectives;
    private final List<Marin> sailorsSortedByX;
    private final List<LineOnBoat> linesOnBoatWithOars;
    private final List<LineOnBoat> linesOnBoatWithOneOars;

    /**
     * Constructor.
     *
     * @param rowers All sailors disposed to oar.
     */
    public InitRowersPositionObjective(List<Marin> rowers, List<LineOnBoat> linesOnBoat)
    {
        sailorMoveObjectives = new ArrayList<>();

        linesOnBoatWithOars = new ArrayList<>();
        linesOnBoatWithOneOars = new ArrayList<>();
        setLinesOnBoatWithOars(linesOnBoat);

        sailorsSortedByX = getAllSailorsSortedByXPos(rowers);

        int sailorExceeding = sailorsSortedByX.size() - linesOnBoatWithOars.size();
        sailorExceeding = Math.max(sailorExceeding, 0);

        generateSubObjectives(sailorExceeding);

    }

    /**
     * Determine all lines with one or two oars.
     *
     * @param linesOnBoat All the lines that compose the Deck.
     */
    private void setLinesOnBoatWithOars(List<LineOnBoat> linesOnBoat)
    {
        linesOnBoatWithOars.addAll(linesOnBoat.stream().filter(line -> line.getOars().size() > 0).collect(Collectors.toList()));
        linesOnBoatWithOneOars.addAll(linesOnBoat.stream().filter(line -> line.getOars().size() == 1).collect(Collectors.toList()));
        Collections.sort(linesOnBoatWithOars);
        Collections.sort(linesOnBoatWithOneOars);
    }

    /**
     * Generate all sub objectives AKA x-placement objectives.
     * @param nbSailorExceeding The number of sailor in addition to the number of lines.
     */
    private void generateSubObjectives(int nbSailorExceeding) {
        int nbSailorPlaced = 0;
        for (LineOnBoat line : linesOnBoatWithOars)
        {
            if (nbSailorPlaced >= sailorsSortedByX.size()) return;
            if (linesOnBoatWithOneOars.contains(line))
            {
                sailorMoveObjectives.add(new SailorMovementObjective(
                    sailorsSortedByX.get(nbSailorPlaced), line.getOars().get(0).getPos()));
                nbSailorPlaced++;
            }
            else
            {
                sailorMoveObjectives.add(new SailorXMovementObjective(
                    sailorsSortedByX.get(nbSailorPlaced), line.getX()));
                nbSailorPlaced++;
                if (nbSailorExceeding > 0)
                {
                    sailorMoveObjectives.add(new SailorXMovementObjective(
                        sailorsSortedByX.get(nbSailorPlaced), line.getX()));
                    nbSailorPlaced++;
                    nbSailorExceeding--;
                }
            }

        }
    }

    /**
     * Sort all sailor by their x position Asc.
     *
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
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        long notDone = sailorMoveObjectives.stream().filter(obj -> !obj.isValidated()).count();
        return notDone == 0;
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve()
    {
        List<GameAction> moveActions = new ArrayList<>();
        sailorMoveObjectives.forEach(obj -> {
            if (!obj.isValidated())
                moveActions.addAll(obj.resolve());
        });
        return moveActions;
    }
}