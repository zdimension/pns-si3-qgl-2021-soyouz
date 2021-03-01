package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.*;
import java.util.stream.Collectors;

public class ComputeMoveSailor {
	private final Marin sailor;
	private final Set<? extends OnboardEntity> entities;
	private final HashMap<? extends  OnboardEntity, Integer> extraRoundToReachEntity;

	public <T extends OnboardEntity> ComputeMoveSailor(Marin sailor, Collection<T> entities) {
		this.sailor = sailor;
		this.entities = entities.stream().filter(ent -> sailor.isAbsPosReachable(ent.getPos())).collect(Collectors.toSet());
		this.extraRoundToReachEntity = new HashMap<T, Integer>(entities.stream().map(e ->
				 Map.entry(e, sailor.numberExtraRoundsToReachEntity(e.getPos()))
		).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public Marin getSailor() {
		return sailor;
	}

	public Set<Rame> getOars(){
		return Util.filterType(entities.stream(), Rame.class).collect(Collectors.toSet());
	}

	public Set<? extends OnboardEntity> getReachableEntities() {
		return Collections.unmodifiableSet(entities);
	}

	public Set<? extends OnboardEntity> getReachableSingleEntities(){
		return Util.filterType(entities.stream(), Gouvernail.class).collect(Collectors.toSet());
	}

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
				filter(integer -> integer.getPos().equals(pos)).
				findFirst();
		if(ent.isPresent())
			return numberRoundsToReachEntity(ent.get());
		throw new IllegalArgumentException("Entity to reach not found");
	}

	public Set<? extends OnboardEntity> entitiesReachableInExactXRounds(int number){
		return this.extraRoundToReachEntity.entrySet().stream().
				filter(k -> k.getValue().equals(number)).
				map(Map.Entry::getKey).
				collect(Collectors.toSet());
	}

}
