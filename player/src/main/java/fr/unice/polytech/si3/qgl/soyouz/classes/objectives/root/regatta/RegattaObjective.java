package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.OnBoardDataHelper;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation.InitSailorPositionObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Race type objective.
 */
public class RegattaObjective extends RootObjective
{
    private static final Logger logger = Logger.getLogger(RegattaObjective.class.getSimpleName());
    private final RegattaGoal goalData;
    private int numCheckpoint = 0;
    private CheckpointObjective currentCheckpoint;
    private final InitSailorPositionObjective initialisationObjective;
    private OnBoardDataHelper onBoardDataHelper;


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
        initialisationObjective = new InitSailorPositionObjective(ip.getShip(), new ArrayList<>(Arrays.asList(ip.getSailors())));
    }

    /**
     * Update the current checkpoint to reach.
     *
     * @param state of the game
     */
    @Override
    public void update(GameState state)
    {
        if (initialisationObjective.isValidated())
        {
            if (onBoardDataHelper == null)
                onBoardDataHelper = new OnBoardDataHelper(state.getNp().getShip(), new ArrayList<>(Arrays.asList(state.getIp().getSailors())));
            if (currentCheckpoint == null)
                currentCheckpoint = new CheckpointObjective(goalData.getCheckpoints()[numCheckpoint], onBoardDataHelper);
            else if (currentCheckpoint != null && currentCheckpoint.isValidated(state))
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
                currentCheckpoint = new CheckpointObjective(goalData.getCheckpoints()[numCheckpoint], onBoardDataHelper);
            }
        }
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
        return initialisationObjective.isValidated() ? currentCheckpoint.resolve(state)
            : initialisationObjective.resolve();
    }
}
