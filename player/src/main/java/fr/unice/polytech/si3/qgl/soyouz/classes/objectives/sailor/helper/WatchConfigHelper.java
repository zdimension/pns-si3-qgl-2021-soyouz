package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;

/**
 * Class to determine the if we should use a watch action this turn or not.
 */
public class WatchConfigHelper
{
    private final Position currentPosition;
    private final Position lastWatchPosition;
    private final PosOnShip lastWatcherPosition;

    /**
     * Constructor.
     *
     * @param currentPosition The actual ship position.
     * @param lastWatchPosition The position where the last watch action was thrown.
     */
    public WatchConfigHelper(Position currentPosition, Position lastWatchPosition, PosOnShip lastWatcherPosition)
    {
        this.currentPosition = currentPosition;
        this.lastWatchPosition = lastWatchPosition;
        this.lastWatcherPosition = lastWatcherPosition;
    }

    /**
     * Determine if we should perform a watch action or not.
     *
     * @return true if we should, false otherwise.
     */
    public boolean findOptWatchConfiguration()
    {
        return currentPosition.getLength(lastWatchPosition) >= 4000 && lastWatcherPosition == null;
    }
}
