package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * A Helper that contains all data necessary, related to all onboars entities and sailors.
 */
public class OnBoardDataHelper
{
    private List<Marin> mutableRowers;
    private final List<Marin> immutableRowers;
    private final List<Marin> sailSailors;
    private Marin rudderSailor;
    private Marin watchSailor;
    private PosOnShip oldWatchPosition;
    private final Bateau ship;
    //nouvelle entite vigiesailor
    //methode pour attribuer un marin à la vigie qui est aux rames puis l'y remettre
    //stocker son état pré-mouvement
    //setup mutable rower()

    /**
     * Constructor.
     *
     * @param ship    The ship.
     * @param sailors All sailors on the ship.
     */
    public OnBoardDataHelper(Bateau ship, List<Marin> sailors)
    {
        mutableRowers = new ArrayList<>();
        immutableRowers = new ArrayList<>();
        sailSailors = new ArrayList<>();
        rudderSailor = null;
        this.ship = ship;
        setupRudderSailor(sailors);
        setupSailSailor(sailors);
        setupImmutableRowers(sailors);
        setupCrownestSailor(sailors);
        setupUselessSailors(sailors);
        mutableRowers = sailors;
    }


    /**
     * Determine which sailors are in exceed on an empty line.
     *
     * @param sailors The list of remaining sailors.
     */
    private void setupUselessSailors(List<Marin> sailors)
    {
        trace();
        List<Marin> uselessSailors = new ArrayList<>();
        sailors.forEach(sailor ->
        {
            LineOnBoat line = new LineOnBoat(ship, sailor.getX());
            if (line.getOars().isEmpty())
            {
                uselessSailors.add(sailor);
            }
        });
        sailors.removeAll(uselessSailors);
    }

    private void setupCrownestSailor(List<Marin> sailors)
    {
        trace();
        OnboardEntity crownest = ship.findFirstEntity(Vigie.class);
        if (crownest != null)
        {
            var isThereWatcher = sailors.stream()
                .filter(sailor -> sailor.getPos().equals(crownest.getPosCoord()))
                .findFirst();
            if (isThereWatcher.isPresent())
            {
                watchSailor = isThereWatcher.get();
                oldWatchPosition = null;
                sailors.remove(watchSailor);
            }
            else
            {
                watchSailor = null;
                oldWatchPosition = null;
            }
        }
        //watchSailor = null;
        //oldWatchPosition = null;
    }

    public boolean isWatcherThereForever()
    {
        return watchSailor != null && oldWatchPosition == null;
    }

    /**
     * Determine which rowers won't be able to move, aka, two rowers on the same line or
     * alone on a single oar line.
     *
     * @param sailors The remaining sailors.
     */
    private void setupImmutableRowers(List<Marin> sailors)
    {
        trace();
        List<Marin> sailorOnOar = sailors.stream()
            .filter(sailor -> ship.hasAt(sailor.getX(), sailor.getY(), Rame.class))
            .collect(Collectors.toList());
        sailorOnOar.forEach(sailor ->
        {
            LineOnBoat line = new LineOnBoat(ship, sailor.getX());
            if (line.getOars().size() == 1)
            {
                immutableRowers.add(sailor);
            }
            if (line.getOars().size() == 2 &&
                sailorOnOar.stream().filter(s -> s.getX() == line.getX()).count() == 2)
            {
                immutableRowers.add(sailor);
            }
        });
        sailors.removeAll(immutableRowers);
    }

    private boolean isImmutablePos(PosOnShip pos)
    {
        LineOnBoat line = new LineOnBoat(ship, pos.getX());
        return line.getOars().size() == 1 || line.getOars().size() == 2 &&
            immutableRowers.stream().anyMatch(s -> s.getX() == line.getX());
    }

    /**
     * Determine which sailor is attached to the rudder.
     *
     * @param sailors The remaining sailors.
     */
    private void setupRudderSailor(List<Marin> sailors)
    {
        trace();
        OnboardEntity rudder = ship.findFirstEntity(Gouvernail.class);
        rudderSailor = sailors.stream()
            .filter(sailor -> sailor.getPos().equals(rudder.getPosCoord()))
            .findFirst().get();
        sailors.remove(rudderSailor);
    }

    /**
     * Determine which sailors are attached to sails.
     *
     * @param sailors The remaining sailors.
     */
    private void setupSailSailor(List<Marin> sailors)
    {
        trace();
        Util.filterType(Arrays.stream(ship.getEntities()), Voile.class).forEach(ent ->
            sailors.stream()
                .filter(sailor -> sailor.getPos().equals(ent.getPosCoord()))
                .findFirst().ifPresent(sailSailors::add)
        );
        sailors.removeAll(sailSailors);
    }

    /**
     * Getter.
     *
     * @return all mutable rowers.
     */
    public List<Marin> getMutableRowers()
    {
        return mutableRowers;
    }

    /**
     * Getter.
     *
     * @return all immutable rowers.
     */
    public List<Marin> getImmutableRowers()
    {
        return immutableRowers;
    }

    /**
     * Getter.
     *
     * @return all sailors on sail.
     */
    public List<Marin> getSailSailors()
    {
        return sailSailors;
    }

    /**
     * Getter.
     *
     * @return the sailor on the rudder.
     */
    public Marin getRudderSailor()
    {
        return rudderSailor;
    }

    public Marin getCrownestSailor()
    {
        return watchSailor;
    }

    public PosOnShip getCrownestOldPos()
    {
        return oldWatchPosition;
    }

    public boolean removeSailorFromCrownest()
    {
        if (watchSailor != null && oldWatchPosition != null)
        {

            var temp = watchSailor;
            watchSailor = null;
            oldWatchPosition = null;
            if (isImmutablePos(temp.getPosOnShip()))
            {
                immutableRowers.add(temp);
            }
            else
            {
                mutableRowers.add(temp);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addSailorToCrownest(Marin sailor)
    {
        if (watchSailor == null && oldWatchPosition == null)
        {
            if (!mutableRowers.contains(sailor) && !immutableRowers.contains(sailor))
            {
                return false;
            }
            if (isImmutablePos(sailor.getPosOnShip()))
            {
                immutableRowers.remove(sailor);
            }
            else
            {
                mutableRowers.remove(sailor);
            }
            watchSailor = sailor;
            oldWatchPosition = sailor.getPosOnShip();
            return true;
        }
        return false;
    }

    /**
     * Getter.
     *
     * @return the ship.
     */
    public Bateau getShip()
    {
        return ship;
    }
}
