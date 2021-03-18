package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InitSailorPositionObjective implements MovingObjective
{
    private final List<Marin> sailors;
    private final List<LineOnBoat> linesOnBoat;
    private final LineOnBoat lineWithRudder;
    private final List<LineOnBoat> linesWithSails;
    private final List<MovingObjective> movingSailorsObjectives;

    public InitSailorPositionObjective(Bateau ship, List<Marin> sailors)
    {
        movingSailorsObjectives = new ArrayList<>();
        this.sailors = getAllSailorsSortedByXPos(sailors);
        linesOnBoat = setLinesOnBoat(ship);
        lineWithRudder = linesOnBoat.stream().filter(line -> line.getRudder() != null)
            .collect(Collectors.toList()).get(0);
        linesWithSails = linesOnBoat.stream().filter(line -> line.getSail() != null)
            .collect(Collectors.toList());
        generateSubObjectives(ship);
    }

    private void generateSubObjectives(Bateau ship)
    {
        generateMovingToRudderObjective();
        generateMovingToSailsObjective(determineHowManySailorsToSail(ship));
        movingSailorsObjectives.add(new InitRowersPositionObjective(sailors, linesOnBoat));
    }

    private void generateMovingToRudderObjective()
    {
        Marin sailorCloseToRudder = sailors.get(0);
        int dist = lineWithRudder.getRudder().getPos().dist(sailorCloseToRudder.getPosOnShip());
        for (Marin sailor : sailors)
        {
            int distance = lineWithRudder.getRudder().getPos().dist(sailor.getPosOnShip());
            if (distance <= dist)
            {
                dist = distance;
                sailorCloseToRudder = sailor;
            }
        }
        movingSailorsObjectives.add(new SailorMovementObjective(sailorCloseToRudder, lineWithRudder.getRudder().getPos()));
        sailors.remove(sailorCloseToRudder);
    }

    private int determineHowManySailorsToSail(Bateau ship)
    {
        int nbOars = ship.getNumberOar();
        int nbSails = linesWithSails.size();
        switch (nbSails)
        {
            case 1:
                return sailors.size() % 2 == 1 ? 1 : 0;
            case 2:
                if (sailors.size() % 2 == 0)
                {
                    if (sailors.size() > nbOars)
                        return 2;
                    else
                        return 0;
                }
                else return 1;
            default:
                return 0;
        }
    }

    private void generateMovingToSailsObjective(int nbSailorToSail)
    {
        for (int i = 0; i < nbSailorToSail; i++)
        {
            Marin sailorCloseToSail = sailors.get(0);
            int dist = linesWithSails.get(i).getSail().getPos().dist(sailorCloseToSail.getPosOnShip());
            for (Marin sailor : sailors)
            {
                int distance = linesWithSails.get(i).getSail().getPos().dist(sailor.getPosOnShip());
                if (distance <= dist)
                {
                    dist = distance;
                    sailorCloseToSail = sailor;
                }
            }
            movingSailorsObjectives.add(new SailorMovementObjective(sailorCloseToSail, linesWithSails.get(i).getSail().getPos()));
            sailors.remove(sailorCloseToSail);
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
     * Set all LineOnBoat objects for the current ship.
     *
     * @param ship The ship.
     * @return all lines that composes the ship.
     */
    private List<LineOnBoat> setLinesOnBoat(Bateau ship)
    {
        List<LineOnBoat> lines = new ArrayList<>();
        for (int i = 0; i < ship.getDeck().getLength(); i++)
            lines.add(new LineOnBoat(ship, i));
        Collections.sort(lines);
        return lines;
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        long notDone = movingSailorsObjectives.stream().filter(obj -> !obj.isValidated()).count();
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
        movingSailorsObjectives.forEach(obj -> {
            if (!obj.isValidated())
                moveActions.addAll(obj.resolve());
        });
        return moveActions;
    }
}
