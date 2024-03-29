package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * A Helper that contains all data necessary, related to all onboars entities and sailors.
 */
public class OnBoardDataHelper
{
    private final List<Marin> immutableRowers;
    private final List<Marin> leftImmutableRowers;
    private final List<Marin> rightImmutableRowers;
    private final List<Marin> sailSailors;
    private final Bateau ship;
    private final List<Marin> sailors;
    private List<Marin> mutableRowers;
    private Marin rudderSailor;
    private Marin watchSailor;
    private PosOnShip oldWatchPosition;
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
        this.sailors = sailors;
        setupRudderSailor();
        setupWatchSailor();
        setupSailSailor();
        setupImmutableRowers();
        setupUselessSailors();
        mutableRowers = new ArrayList<>(sailors);
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
            oldWatchPosition = sailor.getPos();
            watchSailor = sailor;
        }
        else
        {
            rowerGoingToWatch = mutableRowers.stream()
                .filter(sailor -> sailor.isAbsPosReachable(ship.findFirstPosOfEntity(Vigie.class))).findFirst();
            rowerGoingToWatch.ifPresent(sailor ->
            {
                mutableRowers.remove(sailor);
                oldWatchPosition = sailor.getPos();
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
                {
                    leftImmutableRowers.add(watchSailor);
                }
                else
                {
                    rightImmutableRowers.add(watchSailor);
                }
            }
            else
            {
                transitionSailor = watchSailor;
            }
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
     */
    private void setupUselessSailors()
    {
        trace();
        sailors.removeIf(sailor -> new LineOnBoat(ship, sailor.getX()).getOars().isEmpty());
    }

    /**
     * Determine which sailor is attached to the Watch, if there is one.
     */
    private void setupWatchSailor()
    {
        trace();
        OnboardEntity rudder = ship.findFirstEntity(Vigie.class);
        if (rudder == null)
        {
            return;
        }
        Optional<Marin> potentialWatcher = findSailor(rudder.getPos());
        potentialWatcher.ifPresent(sailor ->
        {
            watchSailor = sailor;
            sailors.remove(watchSailor);
        });
    }

    private Optional<Marin> findSailor(PosOnShip pos)
    {
        return sailors.stream()
            .filter(sailor -> sailor.getPos().equals(pos))
            .findFirst();
    }

    /**
     * Determine which rowers won't be able to move, aka, two rowers on the same line or
     * alone on a single oar line.
     */
    private void setupImmutableRowers()
    {
        trace();
        List<Marin> sailorOnOar = sailors.stream()
            .filter(sailor -> ship.hasAt(sailor.getPos(), Rame.class))
            .collect(Collectors.toList());
        for (Marin sailor : sailorOnOar)
        {
            if (ship.hasAt(sailor.getPos(), Rame.class))
            {
                int lineSize = new LineOnBoat(ship, sailor.getX()).getOars().size();
                if (lineSize == 1 ||
                    (lineSize == 2 && sailorOnOar.stream().filter(s -> s.getX() == sailor.getX()).count() == 2))
                {
                    immutableRowers.add(sailor);
                    sailors.remove(sailor);
                }
            }
        }
        setupSideImmutableRowers();
    }

    /**
     * Setup immutable rowers on each side.
     */
    private void setupSideImmutableRowers()
    {
        immutableRowers.stream()
            .filter(sailor -> sailor.getY() == 0)
            .forEach(leftImmutableRowers::add);
        immutableRowers.stream()
            .filter(sailor -> sailor.getY() == ship.getDeck().getWidth() - 1)
            .forEach(rightImmutableRowers::add);
    }

    /**
     * Determine if a pos in on a immutable row.
     *
     * @param pos The position.
     * @return True if it is, false otherwise.
     */
    private boolean isImmutablePos(PosOnShip pos)
    {
        trace();
        int size = new LineOnBoat(ship, pos.getX()).getOars().size();
        return size == 1 ||
            size == 2 && immutableRowers.stream().anyMatch(s -> s.getX() == pos.getX());
    }

    /**
     * Determine which sailor is attached to the rudder.
     */
    private void setupRudderSailor()
    {
        trace();
        OnboardEntity rudder = ship.findFirstEntity(Gouvernail.class);
        if (rudder != null)
        {
            findSailor(rudder.getPos()).ifPresent(sailor ->
            {
                rudderSailor = sailor;
                sailors.remove(sailor);
            });
        }
    }

    /**
     * Determine which sailors are attached to sails.
     */
    private void setupSailSailor()
    {
        trace();
        ship.getSails().forEach(ent ->
            findSailor(ent.getPos()).ifPresent(sailSailors::add)
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
