package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WantedSailorConfig {
	private HashSet<Pair<Integer,Integer>> rame;
	private Double gouvernail;
	private HashMap<Class<? extends OnboardEntity>, Boolean> presentEntity;

	public WantedSailorConfig(HashSet<Pair<Integer, Integer>> rame, Double gouvernail) {
		this.rame = rame;
		this.gouvernail = gouvernail;
		presentEntity = new HashMap<>();
		presentEntity.put(Rame.class, rame != null);
		presentEntity.put(Gouvernail.class, gouvernail != null);
	}

	public boolean contains(Class<? extends OnboardEntity> ent){
		return presentEntity.get(ent);
	}

	public Set<Class<? extends OnboardEntity>> allEntities(){
		return presentEntity.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
	}
}
