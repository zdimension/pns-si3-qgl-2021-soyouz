package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Vigie;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.OarConfiguration;

import java.util.ArrayList;
import java.util.List;

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
    private SailorMovementObjective transitionObjective;

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
        setupWatchObjective();
        setupRowerObjective();
        setupRudderObjective();
        setupSailObjective(rot);
    }

    /**
     * Generate the rowers objective in order to make them oar.
     */
    private void setupRowerObjective()
    {
        trace();
        RowersConfigHelper rowersConfigHelper = new RowersConfigHelper(rotation, distance,
            onBoardDataHelper.getMutableRowers().size(),
            onBoardDataHelper.getLeftImmutableRowers().size(),
            onBoardDataHelper.getRightImmutableRowers().size(),
            onBoardDataHelper.getShip().getNumberOar());
        OarConfiguration oarConfigWanted = rowersConfigHelper.findOptRowersConfiguration();
        rowersObjective = new RowersObjective(onBoardDataHelper.getShip(),
            onBoardDataHelper.getMutableRowers(), onBoardDataHelper.getLeftImmutableRowers(),
            onBoardDataHelper.getRightImmutableRowers(), oarConfigWanted.getSailorConfiguration());
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

    /**
     * Setup the watch objective.
     */
    private void setupWatchObjective()
    {
        trace();
        if (seaDataHelper.getShip().findFirstEntity(Vigie.class) == null)
        {
            return;
        }
        if (onBoardDataHelper.getWatchSailor() != null && onBoardDataHelper.getOldWatchPosition() == null)
        {
            watchObjective = new WatchObjective(onBoardDataHelper.getShip(),
                onBoardDataHelper.getWatchSailor());
        }
        else
        {
            WatchConfigHelper watchConfigHelper =
                new WatchConfigHelper(seaDataHelper.getShip().getPosition(),
                seaDataHelper.getLastWatchPos(), onBoardDataHelper.getOldWatchPosition());
            if (watchConfigHelper.findOptWatchConfiguration())
            {
                onBoardDataHelper.switchRowerToWatch();
                watchObjective = new WatchObjective(onBoardDataHelper.getShip(),
                    onBoardDataHelper.getWatchSailor());
                seaDataHelper.setLastWatchPos(seaDataHelper.getShip().getPosition());
            }
            else
            {
                transitionObjective = onBoardDataHelper.switchWatcherToOar();
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
        if (transitionObjective != null)
        {
            actions.addAll(transitionObjective.resolve());
        }
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