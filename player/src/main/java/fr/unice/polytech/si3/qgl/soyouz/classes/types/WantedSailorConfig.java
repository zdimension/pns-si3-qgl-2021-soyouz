package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class WantedSailorConfig
{
    private final Double rotation;
    private final HashSet<PosOnShip> absolutePosition;
    private Pair<Integer, Integer> rame;

    public WantedSailorConfig(Pair<Integer, Integer> rame)
    {
        this.rame = rame;
        this.rotation = (double) 0;
        absolutePosition = new HashSet<>();
    }

    public WantedSailorConfig(Pair<Integer, Integer> rame, Set<PosOnShip> absPos)
    {
        this.rame = rame;
        this.rotation = (double) 0;
        absolutePosition = new HashSet<>();
        absolutePosition.addAll(absPos);
    }

    public WantedSailorConfig(Pair<Integer, Integer> rame, Double rotation, Set<PosOnShip> absPos)
    {
        this.rame = rame;
        this.rotation = rotation;
        absolutePosition = new HashSet<>();
        absolutePosition.addAll(absPos);
    }

    public static WantedSailorConfig copy(WantedSailorConfig wanted)
    {
        return new WantedSailorConfig(wanted.getOarConfig(),
            wanted.getRotation(), wanted.absolutePosition);
    }

    public Pair<Integer, Integer> getOarConfig()
    {
        return rame;
    }

    //TODO NOM TROMPEUR ET OVERKILL ?
    public Set<PosOnShip> getAbsConfig()
    {
        return Collections.unmodifiableSet(absolutePosition);
    }

    public Double getRotation()
    {
        return rotation;
    }

    public boolean decrementOarUsage()
    {
        if (rame.first > 0 && rame.second > 0)
        {
            rame = Pair.of(rame.first - 1, rame.first - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WantedSailorConfig that = (WantedSailorConfig) o;
        return Objects.equals(rame, that.rame) && Objects.equals(rotation, that.rotation) && Objects.equals(absolutePosition, that.absolutePosition) ;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(rame, rotation, absolutePosition);
    }
}
