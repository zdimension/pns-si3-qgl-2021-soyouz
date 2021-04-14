package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.OnBoardObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorXMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Initialise all sailors position on their dedicated entity based on their X position on the boat.
 */
public class InitSailorPositionObjective implements MovingObjective
{
    private final List<Marin> sailors;
    private final List<LineOnBoat> linesOnBoat;
    private final LineOnBoat lineWithRudder;
    private final List<LineOnBoat> linesWithSails;
    private final LineOnBoat lineWithWatch;
    private final List<MovingObjective> movingSailorsObjectives;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param sailors All sailors on the ship.
     */
    public InitSailorPositionObjective(Bateau ship, List<Marin> sailors)
    {
        movingSailorsObjectives = new ArrayList<>();
        this.sailors = getAllSailorsSortedByXPos(sailors);
        linesOnBoat = setLinesOnBoat(ship);
        lineWithRudder = linesOnBoat.stream().filter(line -> line.getRudder() != null)
            .collect(Collectors.toList()).get(0);
        linesWithSails = linesOnBoat.stream().filter(line -> line.getSail() != null)
            .collect(Collectors.toList());
        lineWithWatch = linesOnBoat.stream().filter(line -> line.getWatch() != null)
            .collect(Collectors.toList()).get(0);
        generateSubObjectives(ship);
    }

    /**
     * Determine how many sailors wii be exceeding the number of entities and move them around.
     * @param ship The ship.
     */
    private void handleUselessSailors(Bateau ship)
    {
        while (ship.getEntities().length < sailors.size())
        {
            LineOnBoat lineWithNothing = linesOnBoat.stream()
                .filter(line -> line.getOars().isEmpty()).collect(Collectors.toList()).get(0);
            movingSailorsObjectives.add(new SailorXMovementObjective(sailors.get(0), lineWithNothing.getX()));
            sailors.remove(0);
        }
    }

    /**
     * Determine all sub movement objectives for each sailors.
     *
     * @param ship The ship.
     */
    private void generateSubObjectives(Bateau ship)
    {
        handleUselessSailors(ship);
        generateMovingToRudderObjective();
        generateMovingToSailsObjective(determineHowManySailorsToSail(ship));
        generateMovingToWatchObjective(ship);
        movingSailorsObjectives.add(new InitRowersPositionObjective(sailors, linesOnBoat));
    }

    /**
     * Move a sailor to the watch if judged necessary.
     *
     * @param ship The ship.
     */
    private void generateMovingToWatchObjective(Bateau ship)
    {
        if (ship.getEntities().length < sailors.size())
        {
            Marin sailorCloseToWatch = findClosestSailorFromEntity(lineWithWatch.getWatch());
            movingSailorsObjectives.add(new SailorMovementObjective(sailorCloseToWatch, lineWithWatch.getWatch().getPos()));
            sailors.remove(sailorCloseToWatch);
        }
    }

    /**
     * Find the sailor that's the closest to the entity.
     *
     * @param entity The entity to reach.
     * @return the closest sailor.
     */
    private Marin findClosestSailorFromEntity(OnboardEntity entity)
    {
        Marin sailorCloseToEntity = sailors.get(0);
        int dist = entity.getPos().dist(sailorCloseToEntity.getPosOnShip());
        for (Marin sailor : sailors)
        {
            int distance = entity.getPos().dist(sailor.getPosOnShip());
            if (distance <= dist)
            {
                dist = distance;
                sailorCloseToEntity = sailor;
            }
        }
        return sailorCloseToEntity;
    }

    /**
     * Move a sailor to the rudder.
     */
    private void generateMovingToRudderObjective()
    {
        Marin sailorCloseToRudder = findClosestSailorFromEntity(lineWithRudder.getRudder());
        movingSailorsObjectives.add(new SailorMovementObjective(sailorCloseToRudder, lineWithRudder.getRudder().getPos()));
        sailors.remove(sailorCloseToRudder);
    }

    /**
     * Determine how many sailor we're able to send to the sail.
     *
     * @param ship The ship
     * @return the number wanted.
     */
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

    /**
     * Move all sail dedicated sailor to their sail.
     *
     * @param nbSailorToSail The ship.
     */
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
        return movingSailorsObjectives.stream().allMatch(OnBoardObjective::isValidated);
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