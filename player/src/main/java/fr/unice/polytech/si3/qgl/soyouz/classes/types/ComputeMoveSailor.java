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

    public <T extends OnboardEntity> ComputeMoveSailor(Marin sailor, Collection<T> entities)
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
    public Set<? extends OnboardEntity> getReachableEntities()
    {
        return Collections.unmodifiableSet(entities);
    }

    //TODO NOM TROMPEUR
    /**
     * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} reachable within
     * a round that exist in only one occurrence on the
     * {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
     */
    public Set<? extends OnboardEntity> getReachableSingleEntities()
    {
        return Util.filterType(entities.stream(), Gouvernail.class).collect(Collectors.toSet());
    }

    /**
     * @param ent to reach
     * @param <T> type of the ent
     * @return who many rounds it take to reach the entity
     */
    public <T extends OnboardEntity> int numberRoundsToReachEntity(T ent)
    {
        var number = this.extraRoundToReachEntity.entrySet().stream().
            filter(k -> k.getKey().equals(ent)).
            map(Map.Entry::getValue).
            findFirst();
        if (number.isPresent())
        {
            return number.get();
        }
        throw new IllegalArgumentException("Entity to reach not found");
    }

    //TODO NEVER USED
    public int numberRoundsToReachEntity(Pair<Integer, Integer> pos)
    {
        var ent = this.extraRoundToReachEntity.keySet().stream().
            filter(integer -> integer.getPosCoord().equals(pos)).
            findFirst();
        if (ent.isPresent())
        {
            return numberRoundsToReachEntity(ent.get());
        }
        throw new IllegalArgumentException("Entity to reach not found");
    }

    //TODO NEVER USED
    /**
     * @param number of rounds in which the entities can be reached
     * @return entities that can be reached in specified number of rounds
     */
    public Set<? extends OnboardEntity> entitiesReachableInExactXRounds(int number)
    {
        return this.extraRoundToReachEntity.entrySet().stream().
            filter(k -> k.getValue().equals(number)).
            map(Map.Entry::getKey).
            collect(Collectors.toSet());
    }

}
