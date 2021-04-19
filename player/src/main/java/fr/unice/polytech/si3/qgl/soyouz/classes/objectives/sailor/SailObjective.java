package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LiftSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LowerSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Sail objective.
 */
public class SailObjective implements OnBoardObjective
{
    private final List<Marin> sailors;
    private final List<MovingObjective> movement;
    private final List<Voile> sailsToOpen;
    private final List<Voile> sailsToClose;

    /**
     * Constructor.
     *
     * @param ship            The ship.
     * @param nbSailOpenedOpt The number of sails to open.
     * @param sailors         The sails sailors.
     */
    public SailObjective(Bateau ship, int nbSailOpenedOpt, List<Marin> sailors)
    {
        this.sailors = sailors;
        sailsToOpen = new ArrayList<>();
        sailsToClose = new ArrayList<>();
        movement = new ArrayList<>();
        setupSails(ship, nbSailOpenedOpt);
        setMovement();
    }


    /**
     * Determine how many sails will be opened/closed.
     *
     * @param ship            The ship.
     * @param nbSailOpenedOpt The number of sails that should be opened.
     */
    private void setupSails(Bateau ship, int nbSailOpenedOpt)
    {
        trace();
        var grouped = Util.filterType(Arrays.stream(ship.getEntities()), Voile.class)
            .collect(Collectors.groupingBy(Voile::isOpenned));
        var openedSails = grouped.getOrDefault(true, new ArrayList<>());
        var closedSails = grouped.getOrDefault(false, new ArrayList<>());
        int diff = nbSailOpenedOpt - openedSails.size();
        while (diff != 0)
        {
            if (diff > 0)
            {
                sailsToOpen.add(closedSails.get(0));
                openedSails.add(closedSails.get(0));
                closedSails.remove(0);
                diff--;
            }
            if (diff < 0)
            {
                sailsToClose.add(openedSails.get(0));
                closedSails.add(openedSails.get(0));
                openedSails.remove(0);
                diff++;
            }
        }
    }

    /**
     * Move the Sail sailors to the sails if needed.
     */
    private void setMovement()
    {
        trace();
        List<Marin> sailor = Util.sortByX(sailors.stream()).collect(Collectors.toList());
        Util.sortByX(sailsToOpen.stream()).forEach(sail ->
        {
            if (sailor.stream().noneMatch(s -> s.getPos().equals(sail.getPos())) && !sailor.isEmpty())
            {
                movement.add(new SailorMovementObjective(sailor.get(0), sail.getPos()));
                sailor.remove(0);
            }
        });
        Util.sortByX(sailsToClose.stream()).forEach(sail ->
        {
            if (sailor.stream().noneMatch(s -> s.getPos().equals(sail.getPos())))
            {
                movement.add(new SailorMovementObjective(sailor.get(0), sail.getPos()));
                sailor.remove(0);
            }
        });
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return movement.isEmpty() || movement.stream().allMatch(MovingObjective::isValidated);
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
        movement.forEach(obj -> actions.addAll(obj.resolve()));
        if (!isValidated())
        {
            return actions;
        }
        sailsToOpen.forEach(sail ->
        {
            sailors.stream().filter(s -> s.getPos().equals(sail.getPos())).findFirst().ifPresent(marin ->
            {
                actions.add(new LiftSailAction(marin));
                sail.setOpenned(true);
            });
        });
        sailsToClose.forEach(sail ->
        {
            sailors.stream().filter(s -> s.getPos().equals(sail.getPos())).findFirst().ifPresent(marin ->
            {
                actions.add(new LowerSailAction(marin));
                sail.setOpenned(false);
            });
        });
        return actions;
    }
}
