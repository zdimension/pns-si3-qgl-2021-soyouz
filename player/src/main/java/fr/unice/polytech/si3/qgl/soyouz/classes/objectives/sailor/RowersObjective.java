package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorYMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param immutableRowers All sailors fixed to a specific oar.
     * @param rowerConfigurationWanted The wanted configuration of rowers.
     */
    public RowersObjective(Bateau ship, List<Marin> mutableRowers, List<Marin> immutableRowers, Pair<Integer, Integer> rowerConfigurationWanted)
    {
        nbOarLeftWanted = rowerConfigurationWanted.first;
        nbOarRightWanted = rowerConfigurationWanted.second;
        setupNbOarWantedOnEachSide(rowerConfigurationWanted, mutableRowers, immutableRowers);
        movingRowers = new ArrayList<>();
        rowingSailors = new ArrayList<>();
        setupImmutableRowers(ship, immutableRowers);
        setupRowers(ship, mutableRowers);
    }

    /**
     * Determine how many oars will be used on each side based on the configuration wanted.
     *
     * @param rowerConfigurationWanted The configuration wanted.
     * @param rowers All rowers that can move.
     * @param immutableRowers All rowers that can't move.
     */
    private void setupNbOarWantedOnEachSide(Pair<Integer, Integer> rowerConfigurationWanted, List<Marin> rowers, List<Marin> immutableRowers)
    {
        nbOarLeftWanted = rowerConfigurationWanted.first;
        nbOarRightWanted = rowerConfigurationWanted.second;
        while ((nbOarLeftWanted + nbOarRightWanted) > (rowers.size() + immutableRowers.size()))
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
        if (nbOarLeftWanted > 0 || nbOarRightWanted > 0)
            moveRowersToOars(ship, middleRower);
    }

    /**
     * Generate the movement objective to place the rower on its oar.
     *
     * @param ship The ship.
     * @param middleRowers Rowers at the middle of two oars.
     */
    private void moveRowersToOars(Bateau ship, List<Marin> middleRowers)
    {
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
     * @param ship The ship.
     * @param immutableRowers Rowers that can't move.
     */
    private void setupImmutableRowers(Bateau ship, List<Marin> immutableRowers)
    {
        List<Marin> leftRowers = immutableRowers.stream()
            .filter(sailor -> sailor.getY() == 0).collect(Collectors.toList());
        List<Marin> rightRowers = immutableRowers.stream()
            .filter(sailor -> sailor.getY() == ship.getDeck().getWidth() - 1)
            .collect(Collectors.toList());
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
    //TODO : ICI SI LA CONFIG N'EST PAS ATTEIGNABLE IMMEDIATEMENT ALORS DEPLACE SEULEMENT LES MARINS
    //TODO METTRE EN PLACE UNE STRATEGIE OU L'ON SUPPRIME 1 MARIN DE CHAQUE COTE ?
}
