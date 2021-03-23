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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Sail objective.
 */
public class SailObjective implements OnBoardObjective
{
    //TODO REFAIRE GESTION (SI VOILE FERMEE OU OUVERTE ALORS NE PAS OUVRIR FERMER
    private final List<Marin> sailors;
    private final List<MovingObjective> movement;
    private int nbSailToOpen;
    private int nbSailToClose;

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
        nbSailToOpen = 0;
        nbSailToClose = 0;
        movement = new ArrayList<>();
        setupNbSails(ship, nbSailOpenedOpt);
        setMovement(ship);
    }


    /**
     * Determine how many sails will be opened/closed.
     *
     * @param ship The ship.
     * @param nbSailOpenedOpt The number of sails that should be opened.
     */
    private void setupNbSails(Bateau ship, int nbSailOpenedOpt)
    {
        List<Voile> sails = Util.filterType(Arrays.stream(ship.getEntities())
            .filter(ent -> ent instanceof Voile), Voile.class).collect(Collectors.toList());
        int nbOpened = (int) sails.stream().filter(Voile::isOpenned).count();
        while (nbOpened > nbSailOpenedOpt)
        {
            nbSailToClose++;
            nbOpened--;
        }
        while (nbOpened < nbSailOpenedOpt)
        {
            nbSailToOpen++;
            nbOpened++;
        }
    }

    /**
     * Move the Sail sailors to the sails if needed.
     *
     * @param ship The ship.
     */
    private void setMovement(Bateau ship)
    {
        List<OnboardEntity> sails = Arrays.stream(ship.getEntities())
            .filter(ent -> ent instanceof Voile)
            .sorted(Comparator.comparing(OnboardEntity::getX)).collect(Collectors.toList());
        sailors.forEach(sailor -> {
            if (!ship.hasAt(sailor.getX(), sailor.getY(), Voile.class) && !sails.isEmpty())
            {
                movement.add(new SailorMovementObjective(sailor, sails.get(0).getPos()));
                sails.remove(0);
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
        List<GameAction> actions = new ArrayList<>();
        movement.forEach(obj -> actions.addAll(obj.resolve()));
        if (!isValidated())
            return actions;
        sailors.forEach(sailor -> {
            if (nbSailToOpen > 0)
            {
                actions.add(new LowerSailAction(sailor));
                nbSailToOpen--;
            }
            else if (nbSailToClose > 0)
            {
                actions.add(new LiftSailAction(sailor));
                nbSailToClose--;
            }
        });
        return actions;
    }
}
