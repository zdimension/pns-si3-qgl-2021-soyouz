package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation.InitSailorPositionObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.unice.polytech.si3.qgl.soyouz.Cockpit.trace;

/**
 * Race type objective.
 */
public class RegattaObjective implements RootObjective
{
    private static final Logger logger = Logger.getLogger(RegattaObjective.class.getSimpleName());
    private final RegattaGoal goalData;
    private final InitSailorPositionObjective initialisationObjective;
    private int numCheckpoint = 0;
    private CheckpointObjective currentCheckpoint;
    private OnBoardDataHelper onBoardDataHelper;
    private SeaDataHelper seaDataHelper;


    /**
     * Constructor.
     *
     * @param goalData The data of the race.
     */
    public RegattaObjective(RegattaGoal goalData, InitGameParameters ip)
    {
        this.goalData = goalData;
        currentCheckpoint = null;
        onBoardDataHelper = null;
        seaDataHelper = null;
        initialisationObjective = new InitSailorPositionObjective(ip.getShip(),
            new ArrayList<>(Arrays.asList(ip.getSailors())));
    }

    public CheckpointObjective getCurrentCheckpoint()
    {
        return currentCheckpoint;
    }

    /**
     * Update the current checkpoint to reach.
     *
     * @param state of the game
     */
    private void update(GameState state)
    {
        trace();
        if (initialisationObjective.isValidated())
        {
            updateHelpers(state);
            updateCheckpoint(state);
        }
    }

    /**
     * Update all helpers.
     *
     * @param state The curent game state.
     */
    private void updateHelpers(GameState state)
    {
        trace();
        if (onBoardDataHelper == null)
        {
            onBoardDataHelper = new OnBoardDataHelper(state.getIp().getShip(),
                new ArrayList<>(Arrays.asList(state.getIp().getSailors())));
        }
        if (seaDataHelper == null)
        {
            seaDataHelper = new SeaDataHelper(state.getNp().getShip(), state.getNp().getWind());
        }
        else
        {
            seaDataHelper.update(state);
        }
    }

    /**
     * Update the current checkpoint.
     *
     * @param state The game state.
     */
    private void updateCheckpoint(GameState state)
    {
        trace();
        if (currentCheckpoint == null)
        {
            currentCheckpoint = new CheckpointObjective(goalData.getCheckpoints()[numCheckpoint],
                onBoardDataHelper, seaDataHelper);
        }
        if (currentCheckpoint.isValidated(state))
        {
            if (goalData.getCheckpoints().length - 1 > numCheckpoint)
            {
                logger.log(Level.INFO, "Checkpoint " + numCheckpoint + " reached");
                numCheckpoint++;
            }
            else
            {
                logger.log(Level.INFO, "Regatta ended");
                numCheckpoint = 0;
            }
            currentCheckpoint = new CheckpointObjective(goalData.getCheckpoints()[numCheckpoint],
                onBoardDataHelper, seaDataHelper);
        }
    }

    /**
     * Determine if the goal is reached.
     *
     * @param state of the game
     * @return true if this objective is validated
     */
    @Override
    public boolean isValidated(GameState state)
    {
        return numCheckpoint == ((RegattaGoal) state.getIp().getGoal()).getCheckpoints().length - 1;
    }

    /**
     * Defines actions to perform in order to reach the next checkpoint.
     *
     * @param state of the game
     * @return a list of all actions to send to JSON
     */
    @Override
    public List<GameAction> resolve(GameState state)
    {
        trace();
        update(state);

        if (initialisationObjective.isValidated())
        {
            return currentCheckpoint.resolve(state);
        }
        else
        {
            List<GameAction> ga = initialisationObjective.resolve();
            if (initialisationObjective.isValidated())
            {
                update(state);
                if (onBoardDataHelper.getMutableRowers().isEmpty())
                {
                    ga.addAll(currentCheckpoint.resolve(state));
                }
            }
            return ga;
        }
    }
}
