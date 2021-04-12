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
     * @param ship The ship.
     * @param nbSailOpenedOpt The number of sails to open.
     * @param sailors The sails sailors.
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
     * @param ship The ship.
     * @param nbSailOpenedOpt The number of sails that should be opened.
     */
    private void setupSails(Bateau ship, int nbSailOpenedOpt)
    {
        trace();
        List<Voile> sails = Util.filterType(Arrays.stream(ship.getEntities())
            .filter(ent -> ent instanceof Voile), Voile.class).collect(Collectors.toList());
        List<Voile> openedSails = sails.stream().filter(Voile::isOpenned).collect(Collectors.toList());
        List<Voile> closedSails = sails.stream().filter(sail -> !sail.isOpenned()).collect(Collectors.toList());
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
        List<Voile> sailsOpen = sailsToOpen.stream()
            .sorted(Comparator.comparing(OnboardEntity::getX)).collect(Collectors.toList());
        List<Voile> sailsClose = sailsToClose.stream()
            .sorted(Comparator.comparing(OnboardEntity::getX)).collect(Collectors.toList());
        List<Marin> sailor = sailors.stream()
            .sorted(Comparator.comparing(Marin::getX)).collect(Collectors.toList());
        sailsOpen.forEach(sail -> {
            if (sailor.stream().noneMatch(s -> s.getPos().equals(sail.getPosCoord())))
            {
                movement.add(new SailorMovementObjective(sailor.get(0), sail.getPos()));
                sailor.remove(0);
            }
        });
        sailsClose.forEach(sail -> {
            if (sailor.stream().noneMatch(s -> s.getPos().equals(sail.getPosCoord())))
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
            return actions;
        sailsToOpen.forEach(sail -> {
            //noinspection OptionalGetWithoutIsPresent
            Marin sailor = sailors.stream().filter(s -> s.getPos().equals(sail.getPosCoord())).findFirst().get();
            actions.add(new LiftSailAction(sailor));
            sail.setOpenned(true);
        });
        sailsToClose.forEach(sail -> {
            //noinspection OptionalGetWithoutIsPresent
            Marin sailor = sailors.stream().filter(s -> s.getPos().equals(sail.getPosCoord())).findFirst().get();
            actions.add(new LowerSailAction(sailor));
            sail.setOpenned(false);
        });
        return actions;
    }
}
