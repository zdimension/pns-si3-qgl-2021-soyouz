package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.helper;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnBoardDataHelper
{
    List<Marin> mutableRowers;
    List<Marin> immutableRowers;
    List<Marin> sailSailors;
    Marin rudderSailor;
    Bateau ship;

    //TODO VERIFIER QUE SAILOR EST BIEN VIDÉ AU FUR ET A MESURE DE L'INITIALISATION
    public OnBoardDataHelper(Bateau ship, List<Marin> sailors)
    {
        mutableRowers = new ArrayList<>();
        immutableRowers = new ArrayList<>();
        sailSailors = new ArrayList<>();
        rudderSailor = null;
        this.ship = ship;
        setupRudderSailor(ship, sailors);
        setupSailSailor(ship, sailors);
        setupImmutableRowers(ship, sailors);
        mutableRowers = sailors;
    }

    private void setupImmutableRowers(Bateau ship, List<Marin> sailors)
    {
        List<Marin> sailorOnOar = sailors.stream()
            .filter(sailor -> ship.hasAt(sailor.getX(), sailor.getY(), Rame.class))
            .collect(Collectors.toList());
        sailorOnOar.forEach(sailor -> {
            LineOnBoat line = new LineOnBoat(ship, sailor.getX());
            if (line.getOars().size() == 1)
                immutableRowers.add(sailor);
            if (line.getOars().size() == 2 &&
                sailorOnOar.stream().filter(s -> s.getX() == line.getX()).count() == 2)
            {
                immutableRowers.add(sailor);
            }
        });
        sailors.removeAll(immutableRowers);
    }

    private void setupRudderSailor(Bateau ship, List<Marin> sailors)
    {
        OnboardEntity rudder = ship.findFirstEntity(Gouvernail.class);
        rudderSailor = sailors.stream()
            .filter(sailor -> sailor.getPos().equals(rudder.getPosCoord()))
            .collect(Collectors.toList()).get(0);
        sailors.remove(rudderSailor);
    }

    private void setupSailSailor(Bateau ship, List<Marin> sailors)
    {
        List<OnboardEntity> sails = Arrays.stream(ship.getEntities()).filter(ent -> ent instanceof Voile).collect(Collectors.toList());
        sails.forEach(ent -> {
            sailSailors.add(sailors.stream()
                .filter(sailor -> sailor.getPos().equals(ent.getPosCoord()))
                .collect(Collectors.toList()).get(0));
        });
        sailors.removeAll(sailSailors);
    }

    public List<Marin> getMutableRowers()
    {
        return mutableRowers;
    }

    public List<Marin> getImmutableRowers()
    {
        return immutableRowers;
    }

    public List<Marin> getSailSailors()
    {
        return sailSailors;
    }

    public Marin getRudderSailor()
    {
        return rudderSailor;
    }

    public Bateau getShip()
    {
        return ship;
    }
}
