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

public class RowersObjective implements OnBoardObjective
{
    //TODO LES MARINS POUVANT BOUGER SONT SEULEMENT LES MARINS AYANT 2 RAMES SUR LEUR LIGNES
    private int nbOarLeftWanted;
    private int nbOarRightWanted;
    private final List<SailorYMovementObjective> movingRowers;
    private final List<Marin> rowingSailors;

    public RowersObjective(Bateau ship, List<Marin> rowers, List<Marin> immutableRowers, Pair<Integer, Integer> rowerConfigurationWanted)
    {
        nbOarLeftWanted = rowerConfigurationWanted.first;
        nbOarRightWanted = rowerConfigurationWanted.second;
        setupNbOarWantedOnEachSide(rowerConfigurationWanted, rowers, immutableRowers);
        movingRowers = new ArrayList<>();
        rowingSailors = new ArrayList<>();
        setupImmutableRowers(ship, immutableRowers);
        setupRowers(ship, rowers);
    }

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

    private void moveRowersToOars(Bateau ship, List<Marin> middleRowers)
    {
        while (nbOarRightWanted > 0 && middleRowers.size() > 0)
        {
            movingRowers.add(new SailorYMovementObjective(middleRowers.get(0), ship.getDeck().getWidth() - 1));
            middleRowers.remove(0);
        }
        while (nbOarLeftWanted > 0 && middleRowers.size() > 0)
        {
            movingRowers.add(new SailorYMovementObjective(middleRowers.get(0), 0));
            middleRowers.remove(0);
        }
    }

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

    //TODO VOIR SI SONARQUBE DETECTE DUPLICATION OU NON
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
            if (obj.isValidated())
                rowingSailors.add(obj.getSailor());
            actions.addAll(obj.resolve());
        });
        if (!isValidated())
            return actions;
        rowingSailors.forEach(rower -> {
            actions.add(new OarAction(rower));
        });
        return actions;
    }
    //TODO : ICI SI LA CONFIG N'EST PAS ATTEIGNABLE IMMEDIATEMENT ALORS DEPLACE SEULEMENT LES MARINS
    //TODO METTRE EN PLACE UNE STRATEGIE OU L'ON SUPPRIME 1 MARIN DE CHAQUE COTE ?
}
