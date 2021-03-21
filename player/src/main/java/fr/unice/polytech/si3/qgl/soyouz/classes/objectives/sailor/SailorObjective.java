package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.RowersConfigHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.RudderConfigHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.OarConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Top level objective for all sailors. Generate all sub objective to make the ship turn and move.
 */
public class SailorObjective implements OnBoardObjective
{
    private final OnBoardDataHelper dataHelper;
    private final double distance;
    private double rotation;
    private RudderObjective rudderObjective;
    //private SailObjective sailObjective;
    private RowersObjective rowersObjective;

    /**
     * Constructor.
     *
     * @param onBoardDataHelper The data helper.
     * @param distance The distance between the ship and the checkpoint.
     * @param rotation The angle between the ship and the checkpoint.
     */
    public SailorObjective(OnBoardDataHelper onBoardDataHelper, double distance, double rotation)
    {
        dataHelper = onBoardDataHelper;
        this.distance = distance;
        this.rotation = rotation;
        setupSubObjectives();
    }

    /**
     * Setup rower and rudder objectives.
     */
    private void setupSubObjectives()
    {
        setupRowerObjective();
        setupRudderObjective();
    }

    /**
     * Generate the rowers objective in order to make them oar.
     */
    private void setupRowerObjective()
    {
        int leftImmutable = (int) dataHelper.getImmutableRowers().stream()
            .filter(sailor -> sailor.getY() == 0).count();
        int rightImmutable = (int) dataHelper.getImmutableRowers().stream()
            .filter(sailor -> sailor.getY() == dataHelper.getShip().getDeck().getWidth() - 1)
            .count();
        RowersConfigHelper rowersConfigHelper = new RowersConfigHelper(rotation, distance,
            dataHelper.getMutableRowers().size(), leftImmutable, rightImmutable,
            dataHelper.getShip().getNumberOar());
        OarConfiguration oarConfigWanted = rowersConfigHelper.findOptRowersConfiguration();
        rowersObjective = new RowersObjective(dataHelper.getShip(), dataHelper.getMutableRowers(),
            dataHelper.getImmutableRowers(), oarConfigWanted.getSailorConfiguration());
        rotation -= oarConfigWanted.getAngleOfRotation();
    }

    /**
     * Setup the rudder objective to turn.
     */
    private void setupRudderObjective()
    {
        RudderConfigHelper rudderConfigHelper = new RudderConfigHelper(rotation);
        rudderObjective = new RudderObjective(dataHelper.getShip(),
            rudderConfigHelper.findOptRudderRotation(), dataHelper.getRudderSailor());
    }

    /**
     * Determine if the goal is reached.
     *
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated()
    {
        return rowersObjective.isValidated() && rudderObjective.isValidated();
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
        actions.addAll(rowersObjective.resolve());
        actions.addAll(rudderObjective.resolve());
        return actions;
    }
}
