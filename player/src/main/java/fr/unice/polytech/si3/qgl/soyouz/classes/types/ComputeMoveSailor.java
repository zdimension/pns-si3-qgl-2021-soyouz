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
 * ComputeMoveSailor is a class allowing to get all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity}
 * based entities a {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin} can reach and how many rounds it
 * would take.
 */
public class ComputeMoveSailor {
	private final Marin sailor;
	private Set<? extends OnboardEntity> entities;
	private HashMap<? extends  OnboardEntity, Integer> extraRoundToReachEntity;
	private Pair<Integer, Integer> move;

	public <T extends OnboardEntity> ComputeMoveSailor(Marin sailor, Collection<T> entities) {
		this.sailor = sailor;
		this.entities = entities.stream().filter(ent -> sailor.isAbsPosReachable(ent.getPosCoord())).collect(Collectors.toSet());
		this.extraRoundToReachEntity = new HashMap<T, Integer>(entities.stream().map(e ->
				 Map.entry(e, sailor.numberExtraRoundsToReachEntity(e.getPosCoord()))
		).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		move = Pair.of(0,0);
	}

	public Marin getSailor() {
		return sailor;
	}

	/**
	 *
	 * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame} reachable within
	 * a round
	 */
	public Set<Rame> getOars(){
		return Util.filterType(entities.stream(), Rame.class).collect(Collectors.toSet());
	}

	/**
	 *
	 * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} reachable within
	 * a round
	 */
	public Set<? extends OnboardEntity> getReachableEntities() {
		return Collections.unmodifiableSet(entities);
	}

	/**
	 *
	 * @return all {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity} reachable within
	 * a round that exist in only one occurrence on the {@link fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Deck}.
	 */
	public Set<? extends OnboardEntity> getReachableSingleEntities(){
		return Util.filterType(entities.stream(), Gouvernail.class).collect(Collectors.toSet());
	}

	/**
	 *
	 * @param ent to reach
	 * @param <T>
	 * @return who many rounds it take to reach the entity
	 */
	public <T extends OnboardEntity> int numberRoundsToReachEntity(T ent){
		var number =  this.extraRoundToReachEntity.entrySet().stream().
				filter(k -> k.getKey().equals(ent)).
				map(Map.Entry::getValue).
				findFirst();
		if(number.isPresent())
			return number.get();
		throw new IllegalArgumentException("Entity to reach not found");
	}

	public int numberRoundsToReachEntity(Pair<Integer,Integer> pos){
		var ent = this.extraRoundToReachEntity.keySet().stream().
				filter(integer -> integer.getPosCoord().equals(pos)).
				findFirst();
		if(ent.isPresent())
			return numberRoundsToReachEntity(ent.get());
		throw new IllegalArgumentException("Entity to reach not found");
	}

	/**
	 *
	 * @param number of rounds in which the entities can be reached
	 * @return entities that can be reached in specified number of rounds
	 */
	public Set<? extends OnboardEntity> entitiesReachableInExactXRounds(int number){
		return this.extraRoundToReachEntity.entrySet().stream().
				filter(k -> k.getValue().equals(number)).
				map(Map.Entry::getKey).
				collect(Collectors.toSet());
	}

	public void Move(Pair<Integer, Integer> move){
		if(move.equals(Pair.of(0,0))){
			throw new IllegalArgumentException("Cannot move sailor by 0, 0");
		}
		if(!this.move.equals(Pair.of(0,0))){
			throw new IllegalArgumentException("Cannot move sailor again");
		}
		this.move = move;
		this.entities = entities.stream().filter(ent -> sailor.isAbsPosReachable(ent.getPos().minus(new PosOnShip(move)))).collect(Collectors.toSet());
		this.extraRoundToReachEntity = new HashMap<>(entities.stream().map(e ->
				Map.entry(e, sailor.numberExtraRoundsToReachEntity(e.getPos().minus(new PosOnShip(move))))
		).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}



}
