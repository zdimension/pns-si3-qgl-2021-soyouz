package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * A Helper that contains all data necessary, related to all onboars entities and sailors.
 */
public class OnBoardDataHelper
{
    private List<Marin> mutableRowers;
    private final List<Marin> immutableRowers;
    private final List<Marin> leftImmutableRowers;
    private final List<Marin> rightImmutableRowers;
    private final List<Marin> sailSailors;
    private Marin rudderSailor;
    private Marin watchSailor;
    private PosOnShip oldWatchPosition;
    private final Bateau ship;
    private Marin transitionSailor;

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
        leftImmutableRowers = new ArrayList<>();
        rightImmutableRowers = new ArrayList<>();
        sailSailors = new ArrayList<>();
        rudderSailor = null;
        watchSailor = null;
        oldWatchPosition = null;
        this.ship = ship;
        setupRudderSailor(sailors);
        setupWatchSailor(sailors);
        setupSailSailor(sailors);
        setupImmutableRowers(sailors);
        setupUselessSailors(sailors);
        mutableRowers = sailors;
    }

    /**
     * Switch a rower to the watch.
     */
    public void switchRowerToWatch()
    {
        Optional<Marin> rowerGoingToWatch = immutableRowers.stream()
            .filter(sailor -> sailor.isAbsPosReachable(ship.findFirstPosOfEntity(Vigie.class))).findFirst();
        if (rowerGoingToWatch.isPresent())
        {
            Marin sailor = rowerGoingToWatch.get();
            immutableRowers.remove(sailor);
            leftImmutableRowers.remove(sailor);
            rightImmutableRowers.remove(sailor);
            oldWatchPosition = sailor.getPosOnShip();
            watchSailor = sailor;
        }
        else
        {
            rowerGoingToWatch = mutableRowers.stream()
                .filter(sailor -> sailor.isAbsPosReachable(ship.findFirstPosOfEntity(Vigie.class))).findFirst();
            rowerGoingToWatch.ifPresent(sailor ->
            {
                mutableRowers.remove(sailor);
                oldWatchPosition = sailor.getPosOnShip();
                watchSailor = sailor;
            });
        }
    }

    /**
     * Switch back the watcher to the oars.
     *
     * @return a move action if necessary.
     */
    public SailorMovementObjective switchWatcherToOar()
    {
        if (watchSailor != null)
        {
            Marin sailorToMove = watchSailor;
            PosOnShip posToReachBack = oldWatchPosition;
            if (isImmutablePos(oldWatchPosition))
            {
                immutableRowers.add(watchSailor);
                if (oldWatchPosition.getY() == 0)
                    leftImmutableRowers.add(watchSailor);
                else
                    rightImmutableRowers.add(watchSailor);
            }
            else
                transitionSailor = watchSailor;
            watchSailor = null;
            oldWatchPosition = null;
            return new SailorMovementObjective(sailorToMove, posToReachBack);
        }
        else if (transitionSailor != null)
        {
            mutableRowers.add(transitionSailor);
            transitionSailor = null;
        }
        return null;
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

    /**
     * Determine which sailor is attached to the Watch, if there is one.
     *
     * @param sailors The remaining sailors.
     */
    private void setupWatchSailor(List<Marin> sailors)
    {
        trace();
        OnboardEntity rudder = ship.findFirstEntity(Vigie.class);
        if (rudder == null)
            return;
        Optional<Marin> potentialWatcher = sailors.stream()
            .filter(sailor -> sailor.getPos().equals(rudder.getPosCoord()))
            .findFirst();
        potentialWatcher.ifPresent(sailor ->
        {
            watchSailor = sailor;
            sailors.remove(watchSailor);
        });
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
        setupSideImmutableRowers();
    }

    /**
     * Setup immutable rowers on each side.
     */
    private void setupSideImmutableRowers()
    {
        leftImmutableRowers.addAll(immutableRowers.stream()
            .filter(sailor -> sailor.getY() == 0).collect(Collectors.toList()));
        rightImmutableRowers.addAll(immutableRowers.stream()
            .filter(sailor -> sailor.getY() == ship.getDeck().getWidth() - 1)
            .collect(Collectors.toList()));
    }

    /**
     * Determine if a pos in on a immutable row.
     *
     * @param pos The position.
     * @return True if it is, false otherwise.
     */
    private boolean isImmutablePos(PosOnShip pos) {
        trace();
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
     * @return the sailor on the watch.
     */
    public Marin getWatchSailor()
    {
        return watchSailor;
    }

    /**
     * Getter.
     *
     * @return the position where was the watch sailor before going to the watch.
     */
    public PosOnShip getOldWatchPosition()
    {
        return oldWatchPosition;
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
     * @return all right immutable rowers.
     */
    public List<Marin> getRightImmutableRowers()
    {
        return rightImmutableRowers;
    }

    /**
     * Getter.
     *
     * @return all left immutable rowers.
     */
    public List<Marin> getLeftImmutableRowers()
    {
        return leftImmutableRowers;
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
