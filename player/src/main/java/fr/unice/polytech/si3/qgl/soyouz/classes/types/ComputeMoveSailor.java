package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ComputeMoveSailor is a class allowing to get all
 * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity}
 * based entities a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} can reach
 * and how many rounds it
 * would take.
 */
public class ComputeMoveSailor
{
    private final Marin sailor;
    private final Set<? extends OnboardEntity> entities;
    private final HashMap<? extends OnboardEntity, Integer> extraRoundToReachEntity;

    public ComputeMoveSailor(Marin sailor, Collection<? extends OnboardEntity> entities)
    {
        this.sailor = sailor;
        this.entities =
            entities.stream().filter(ent -> sailor.isAbsPosReachable(ent.getPosCoord())).collect(Collectors.toSet());
        this.extraRoundToReachEntity = new HashMap<>(entities.stream().map(e ->
            Map.entry(e, sailor.numberExtraRoundsToReachEntity(e.getPosCoord()))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Marin getSailor()
    {
        return sailor;
    }

    //TODO NEVER USED
    /**
     * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame} reachable within
     * a round
     */
    public Set<Rame> getOars()
    {
        return Util.filterType(entities.stream(), Rame.class).collect(Collectors.toSet());
    }

    /**
     * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} reachable within
     * a round
     */
    public Set<OnboardEntity> getReachableEntities()
    {
        return Collections.unmodifiableSet(entities);
    }

    //TODO NOM TROMPEUR
    /**
     * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} reachable within
     * a round that exist in only one occurrence on the
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
     */
    public Set<OnboardEntity> getReachableSingleEntities()
    {
        return Util.filterType(entities.stream(), Gouvernail.class).collect(Collectors.toSet());
    }

    /**
     * @param ent to reach
     * @return who many rounds it take to reach the entity
     */
    public int numberRoundsToReachEntity(OnboardEntity ent)
    {
        return numberRoundsToReachEntity(ent.getPosCoord());
    }

    public int numberRoundsToReachEntity(Pair<Integer, Integer> pos)
    {
        return (Math.abs(pos.first - sailor.getX()) + Math.abs(pos.second - sailor.getY()) - 1) / 5;
    }

    //TODO NEVER USED
    /**
     * @param number of rounds in which the entities can be reached
     * @return entities that can be reached in specified number of rounds
     */
    public Set<OnboardEntity> entitiesReachableInExactXRounds(int number)
    {
        return this.extraRoundToReachEntity.entrySet().stream().
            filter(k -> k.getValue().equals(number)).
            map(Map.Entry::getKey).
            collect(Collectors.toSet());
    }

}
