package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorYMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Class to handle every rower movement and row actions.
 */
public class RowersObjective implements OnBoardObjective
{
    private int nbOarLeftWanted;
    private int nbOarRightWanted;
    private final List<SailorYMovementObjective> movingRowers;
    private final List<Marin> rowingSailors;

    /**
     * Constructor.
     *
     * @param ship The ship.
     * @param mutableRowers All rowers able to move from a side to another.
     * @param leftImmutableRowers All sailors fixed to a specific oar on the left of the ship.
     * @param rightImmutableRowers All sailors fixed to a specific oar on the right of the ship.
     * @param rowerConfigurationWanted The wanted configuration of rowers.
     */
    public RowersObjective(Bateau ship, List<Marin> mutableRowers, List<Marin> leftImmutableRowers,
                   List<Marin> rightImmutableRowers, Pair<Integer, Integer> rowerConfigurationWanted)
    {
        nbOarLeftWanted = rowerConfigurationWanted.first;
        nbOarRightWanted = rowerConfigurationWanted.second;
        setupNbOarWantedOnEachSide(rowerConfigurationWanted, mutableRowers, leftImmutableRowers, rightImmutableRowers);
        movingRowers = new ArrayList<>();
        rowingSailors = new ArrayList<>();
        setupImmutableRowers(leftImmutableRowers, rightImmutableRowers);
        setupRowers(ship, mutableRowers);
    }

    /**
     * Determine how many oars will be used on each side based on the configuration wanted.
     *
     * @param rowerConfigurationWanted The configuration wanted.
     * @param rowers All rowers that can move.
     * @param leftImmutableRowers All sailors fixed to a specific oar on the left of the ship.
     * @param rightImmutableRowers All sailors fixed to a specific oar on the right of the ship.
     */
    private void setupNbOarWantedOnEachSide(Pair<Integer, Integer> rowerConfigurationWanted,
        List<Marin> rowers, List<Marin> leftImmutableRowers, List<Marin> rightImmutableRowers)
    {
        nbOarLeftWanted = rowerConfigurationWanted.first;
        nbOarRightWanted = rowerConfigurationWanted.second;
        while ((nbOarLeftWanted + nbOarRightWanted) >
            (rowers.size() + leftImmutableRowers.size() + rightImmutableRowers.size()))
        {
            if (nbOarLeftWanted > 0)
                nbOarLeftWanted--;
            if (nbOarRightWanted > 0)
                nbOarRightWanted--;
        }
    }

    /**
     * Setup every movable rowers to the wanted side.
     *
     * @param ship The ship.
     * @param rowers All mutable rowers.
     */
    private void setupRowers(Bateau ship, List<Marin> rowers)
    {
        trace();
        List<Marin> leftRowers = rowers.stream()
            .filter(sailor -> sailor.getY() == 0).collect(Collectors.toList());
        List<Marin> rightRowers = rowers.stream()
            .filter(sailor -> sailor.getY() == ship.getDeck().getWidth() - 1)
            .collect(Collectors.toList());
        List<Marin> middleRower = rowers.stream()
            .filter(sailor -> sailor.getY() < ship.getDeck().getWidth() - 1 && sailor.getY() > 0)
            .collect(Collectors.toList());
        makeLeftRowersRow(leftRowers);
        makeRightRowersRow(rightRowers);
        List<Marin> remainingRowers = Stream.of(leftRowers, rightRowers, middleRower)
            .flatMap(Collection::stream).collect(Collectors.toList());
        if (nbOarLeftWanted > 0 || nbOarRightWanted > 0)
            moveRowersToOars(ship, remainingRowers);
    }

    /**
     * Generate the movement objective to place the rower on its oar.
     *
     * @param ship The ship.
     * @param middleRowers Rowers at the middle of two oars.
     */
    private void moveRowersToOars(Bateau ship, List<Marin> middleRowers)
    {
        trace();
        while (nbOarRightWanted > 0 && !middleRowers.isEmpty())
        {
            movingRowers.add(new SailorYMovementObjective(middleRowers.get(0), ship.getDeck().getWidth() - 1));
            middleRowers.remove(0);
        }
        while (nbOarLeftWanted > 0 && !middleRowers.isEmpty())
        {
            movingRowers.add(new SailorYMovementObjective(middleRowers.get(0), 0));
            middleRowers.remove(0);
        }
    }

    /**
     * Setup all immutable rowers and make them oar.
     *
     * @param leftImmutableRowers All sailors fixed to a specific oar on the left of the ship.
     * @param rightImmutableRowers All sailors fixed to a specific oar on the right of the ship.
     */
    private void setupImmutableRowers(List<Marin> leftImmutableRowers, List<Marin> rightImmutableRowers)
    {
        trace();
        List<Marin> leftRowers = new ArrayList<>(leftImmutableRowers);
        List<Marin> rightRowers = new ArrayList<>(rightImmutableRowers);
        makeLeftRowersRow(leftRowers);
        makeRightRowersRow(rightRowers);
    }

    /**
     * Make all left side rowers row.
     *
     * @param leftRowers Rowers on the left side of the ship.
     */
    private void makeLeftRowersRow(List<Marin> leftRowers)
    {
        trace();
        if (nbOarLeftWanted >= leftRowers.size())
        {
            rowingSailors.addAll(leftRowers);
            nbOarLeftWanted -= leftRowers.size();
            leftRowers.clear();
        }
        else
        {
            while (nbOarLeftWanted > 0)
            {
                rowingSailors.add(leftRowers.get(0));
                leftRowers.remove(0);
                nbOarLeftWanted--;
            }
        }
    }

    /**
     * Make all right side rowers row.
     *
     * @param rightRowers Rowers on the right side of the ship.
     */
    private void makeRightRowersRow(List<Marin> rightRowers)
    {
        trace();
        if (nbOarRightWanted >= rightRowers.size())
        {
            rowingSailors.addAll(rightRowers);
            nbOarRightWanted -= rightRowers.size();
            rightRowers.clear();
        }
        else
        {
            while (nbOarRightWanted > 0)
            {
                rowingSailors.add(rightRowers.get(0));
                rightRowers.remove(0);
                nbOarRightWanted--;
            }
        }
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return movingRowers.stream().allMatch(SailorYMovementObjective::isValidated);
    }

    /**
     * Defines actions to perform. The state of the game is being updated too
     *
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve()
    {
        trace();
        List<GameAction> actions = new ArrayList<>();
        movingRowers.forEach(obj -> {
            actions.addAll(obj.resolve());
            if (obj.isValidated())
                rowingSailors.add(obj.getSailor());
        });
        if (!isValidated())
            return actions;
        rowingSailors.forEach(rower -> actions.add(new OarAction(rower)));
        return actions;
    }
}
