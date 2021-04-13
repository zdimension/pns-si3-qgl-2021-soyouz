package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.OarConfiguration;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Top level objective for all sailors. Generate all sub objective to make the ship turn and move.
 */
public class SailorObjective implements OnBoardObjective
{
    private final OnBoardDataHelper onBoardDataHelper;
    private final SeaDataHelper seaDataHelper;
    private double distance;
    private double rotation;
    private RudderObjective rudderObjective;
    private WatchObjective watchObjective;
    private SailObjective sailObjective;
    private RowersObjective rowersObjective;

    /**
     * Constructor.
     *
     * @param onBoardDataHelper The data helper.
     * @param distance          The distance between the ship and the checkpoint.
     * @param rotation          The angle between the ship and the checkpoint.
     */
    public SailorObjective(OnBoardDataHelper onBoardDataHelper, SeaDataHelper seaDataHelper,
                           double distance, double rotation)
    {
        this.onBoardDataHelper = onBoardDataHelper;
        this.seaDataHelper = seaDataHelper;
        this.distance = distance;
        this.rotation = rotation;
        setupSubObjectives();
    }

    /**
     * Setup rower and rudder objectives.
     */
    private void setupSubObjectives()
    {
        trace();
        double rot = rotation;
        if (onBoardDataHelper.getShip().findFirstEntity(Vigie.class) == null)
        {
            setupRowerObjective();
            setupRudderObjective();
            watchObjective = null;
        }
        else
        {
            //in case the watcher sailor has just been brought back to its row, it cannot move again
            if (seaDataHelper.getTurnsBeforeWatch() == 0)
            {
                setupWatchObjective();
                setupRowerObjective();
                setupRudderObjective();
            }
            else
            {
                setupRowerObjective();
                setupRudderObjective();
                setupWatchObjective();
            }
        }
        setupSailObjective(rot);
    }

    /**
     * Generate the rowers objective in order to make them oar.
     */
    private void setupRowerObjective()
    {
        trace();
        int leftImmutable = (int) onBoardDataHelper.getImmutableRowers().stream()
            .filter(sailor -> sailor.getY() == 0).count();
        int rightImmutable = (int) onBoardDataHelper.getImmutableRowers().stream()
            .filter(sailor -> sailor.getY() == onBoardDataHelper.getShip().getDeck().getWidth() - 1)
            .count();
        RowersConfigHelper rowersConfigHelper = new RowersConfigHelper(rotation, distance,
            onBoardDataHelper.getMutableRowers().size(), leftImmutable, rightImmutable,
            onBoardDataHelper.getShip().getNumberOar());
        OarConfiguration oarConfigWanted = rowersConfigHelper.findOptRowersConfiguration();
        rowersObjective = new RowersObjective(onBoardDataHelper.getShip(),
            onBoardDataHelper.getMutableRowers(),
            onBoardDataHelper.getImmutableRowers(), oarConfigWanted.getSailorConfiguration());
        distance -= oarConfigWanted.getLinearSpeed();
        rotation -= oarConfigWanted.getAngleOfRotation();
    }

    /**
     * Setup the rudder objective to turn.
     */
    private void setupRudderObjective()
    {
        trace();
        RudderConfigHelper rudderConfigHelper = new RudderConfigHelper(rotation);
        rudderObjective = new RudderObjective(onBoardDataHelper.getShip(),
            rudderConfigHelper.findOptRudderRotation(), onBoardDataHelper.getRudderSailor());
        rotation -= rudderConfigHelper.findOptRudderRotation();
    }

    /**
     * Setup the sail objective.
     */
    private void setupSailObjective(double rot)
    {
        trace();
        SailConfigHelper sailConfigHelper = new SailConfigHelper(distance, rot,
            seaDataHelper.getShip().getNumberSail(), seaDataHelper.getShip(),
            seaDataHelper.getWind());
        sailObjective = new SailObjective(onBoardDataHelper.getShip(),
            sailConfigHelper.findOptSailConfiguration(), onBoardDataHelper.getSailSailors());
    }

    private void setupWatchObjective()
    {
        if (onBoardDataHelper.isWatcherThereForever())
        {
            watchObjective = new WatchObjective(onBoardDataHelper.getShip(),
                onBoardDataHelper.getCrownestSailor());
        }
        else
        {
            switch (seaDataHelper.getTurnsBeforeWatch())
            {
                case 0:
                    var sailortemp =
                        getClosestRower(onBoardDataHelper.getShip().findFirstEntity(Vigie.class).getPos());
                    if (sailortemp.isPresent())
                    {
                        var sailor = sailortemp.get();
                        if (onBoardDataHelper.addSailorToCrownest(sailor))
                        {
                            watchObjective = new WatchObjective(onBoardDataHelper.getShip(), sailor,
                                null);
                        }
                    }
                    break;
                case SeaDataHelper.TURNS_BEFORE_WATCH:
                    if (onBoardDataHelper.getCrownestSailor() != null)
                    {
                        var tempSailor = onBoardDataHelper.getCrownestSailor();
                        var tempPos = onBoardDataHelper.getCrownestOldPos();
                        if (onBoardDataHelper.removeSailorFromCrownest())
                        {
                            watchObjective = new WatchObjective(onBoardDataHelper.getShip(),
                                tempSailor, tempPos);
                        }
                    }
                    break;
                default:
                    watchObjective = new WatchObjective(null, null);
            }
        }
    }

    private Optional<Marin> getClosestRower(PosOnShip pos)
    {
        return Stream.concat(onBoardDataHelper.getImmutableRowers().stream(),
            onBoardDataHelper.getMutableRowers().stream())
            .map(marin -> Pair.of(marin, marin.getPosOnShip().dist(pos)))
            .filter(pair -> pair.second <= Marin.MAX_MOVE)
            .min(Comparator.comparingInt(Pair::getSecond))
            .map(pair -> pair.first);
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return rowersObjective.isValidated() && rudderObjective.isValidated() && sailObjective.isValidated() && (watchObjective == null || watchObjective.isValidated());
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
        actions.addAll(rowersObjective.resolve());
        actions.addAll(rudderObjective.resolve());
        actions.addAll(sailObjective.resolve());
        if (watchObjective != null)
        {
            actions.addAll(watchObjective.resolve());
        }
        return actions;
    }
}