package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class WantedSailorConfig
{
    private final Gouvernail gouvernail;
    private final Double rotation;
    private final HashMap<Class<? extends OnboardEntity>, Boolean> presentEntity;
    private Pair<Integer, Integer> rame;

    public WantedSailorConfig(Pair<Integer, Integer> rame, Gouvernail gouvernail, Double rotation)
    {
        this.rame = rame;
        this.gouvernail = gouvernail;
        this.rotation = rotation;
        presentEntity = new HashMap<>();
        presentEntity.put(Rame.class, rame != null);
        presentEntity.put(Gouvernail.class, !rotation.equals((double) 0));
    }

    public static WantedSailorConfig copy(WantedSailorConfig wanted)
    {
        return new WantedSailorConfig(wanted.getOarConfig(), wanted.getGouvernail(),
            wanted.getRotation());
    }

    public boolean contains(Class<? extends OnboardEntity> ent)
    {
        return presentEntity.get(ent);
    }

    public Set<Class<? extends OnboardEntity>> allEntities()
    {
        return presentEntity.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public Pair<Integer, Integer> getOarConfig()
    {
        return rame;
    }

    public Set<? extends OnboardEntity> getAbsConfig()
    {
        return Set.of(gouvernail);
    }

    public Set<PosOnShip> getAbsConfigPos()
    {
        var pos = new HashSet<PosOnShip>();
        if (gouvernail != null)
        {
            pos.add(new PosOnShip(gouvernail.getPosCoord()));
        }
        return pos;
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

    public Gouvernail getGouvernail()
    {
        return gouvernail;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WantedSailorConfig that = (WantedSailorConfig) o;
        return Objects.equals(rame, that.rame) && Objects.equals(gouvernail, that.gouvernail) && Objects.equals(rotation, that.rotation) && Objects.equals(presentEntity, that.presentEntity);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(rame, gouvernail, rotation, presentEntity);
    }
}
