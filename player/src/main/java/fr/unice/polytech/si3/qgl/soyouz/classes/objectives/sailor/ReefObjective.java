package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Entity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Reef;

import java.util.List;
/** This objective will have an higher priority above all objectives.
 */
public class ReefObjective implements OnBoardObjective
{
    Reef reefToEscape;
    SailorObjective[] escapingPath;
    
    public ReefObjective(Reef reefToEscape, Bateau boat)
    {
        this.reefToEscape = reefToEscape;
        this.escapingPath = determineEscapePath(boat);
    }

    /**
     * Method that will determine in advance the movements to turn around the reefs
     * @param boat the boat
     * @return a list of sailor objectives for next turns
     */
    private SailorObjective[] determineEscapePath(Bateau boat){
        return null;
    }

    @Override
    public boolean isValidated()
    {
        return false;
    }

    @Override
    public List<GameAction> resolve()
    {
        return null;
    }
}
