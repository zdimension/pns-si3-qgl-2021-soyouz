package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LiftSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.LowerSailAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper.SeaDataHelper;

import java.util.ArrayList;
import java.util.List;

//TODO TO BE DONE
public class SailObjective implements OnBoardObjective
{
    private Bateau boat;
    private boolean shouldBeOpen;
    private double windSpeed;
    private Marin sailor;

    public SailObjective(Bateau boat, boolean shouldBeOpen, double windSpeed, Marin sailor)
    {
        this.boat = boat;
        this.shouldBeOpen = shouldBeOpen;
        this.windSpeed = windSpeed;
        this.sailor = sailor;
    }

    @Override
    public boolean isValidated()
    {
        return shouldBeOpen == ((Voile) boat.getEntityHere(sailor.getX(),sailor.getY()).get()).isOpenned();
    }

    @Override
    public List<GameAction> resolve()
    {
        List<GameAction> actions = new ArrayList<>();
        if(windSpeed > 0 && !isValidated())
            actions.add(new LowerSailAction(sailor));
        if(windSpeed < 0 && !isValidated())
            actions.add(new LiftSailAction(sailor));
        return actions;
    }
}
