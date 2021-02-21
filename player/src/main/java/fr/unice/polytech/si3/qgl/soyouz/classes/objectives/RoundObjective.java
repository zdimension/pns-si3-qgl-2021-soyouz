package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;

/**
 * Objective to be performed in a round
 */
public class RoundObjective extends Objective {

	private final Map<Class<? extends OnboardEntity>, Class<?>> configurationShape = Map.of(
			Rame.class, Pair.class  //rajouter que c'est Pair<Integer, Integer>
	);

	private final HashMap<Class<? extends OnboardEntity>, Object> wantedConfiguration;


	public RoundObjective(HashMap<Class<? extends OnboardEntity>, Object> wantedConfiguration) {
		this.wantedConfiguration = wantedConfiguration;
	}

	@Override
	public List<GameAction> resolve(GameState state) {
		var acts = new ArrayList<GameAction>();
		var sailors = new ArrayList<>(Arrays.asList(state.getIp().getSailors()));
		var gameShip = state.getIp().getShip();
		try {
			var oarReachableForSailors = new HashMap<Marin, Set<Rame>>();
			var absReachableForSailors = new HashMap<Marin, Set<Pair<Integer, Integer>>>();
			var allOars = new HashSet<Rame>();
			var allAbsEnt = new HashSet<Pair<Integer, Integer>>();
			var sailorsNotMoving = new ArrayList<MoveAction>();
			for (Marin m : sailors) {
				oarReachableForSailors.put(m, new HashSet<>());
				absReachableForSailors.put(m, new HashSet<>());
				sailorsNotMoving.add(new MoveAction(m, 0, 0));
			}
			//how many sailors on the left and the right
			Pair<Integer, Integer> wantedOarConfig;
			//coordinates of entities having only one instance on the ship
			HashSet<Pair<Integer, Integer>> wantedAbsConfig = new HashSet<>();
			try {
				if (wantedConfiguration.containsKey(Rame.class)) ;
				wantedOarConfig = Pair.of((Integer) ((Pair) wantedConfiguration.get(Rame.class)).first, (Integer) ((Pair) wantedConfiguration.get(Rame.class)).second);
				if (wantedConfiguration.containsKey(Gouvernail.class))
					wantedAbsConfig.add(Pair.of((Integer) ((Pair) wantedConfiguration.get(Gouvernail.class)).first, (Integer) ((Pair) wantedConfiguration.get(Gouvernail.class)).second));

			} catch (Exception e) {
				Cockpit.log("Here are Java's limits");
				throw new RuntimeException("Java is a fucking piece of shit");
			}

			//compute all reachable oars
			for (OnboardEntity ent : gameShip.getEntities()) {
				if (ent instanceof Rame) {
					var r = (Rame) ent;
					allOars.add(r);
					for (Marin m : sailors) {
						if (m.isAbsPosReachable(r.getPos()))
							oarReachableForSailors.get(m).add(r);
					}
				} else {
					allAbsEnt.add(ent.getPos());
					for (Marin m : sailors) {
						if (m.isAbsPosReachable(ent.getPos()))
							absReachableForSailors.get(m).add(ent.getPos());
					}
				}
			}

			var actsMoves = new ArrayList<MoveAction>();

			if (!isOarConfigurationReached(wantedOarConfig, sailorsNotMoving, gameShip) || !isAbsConfigurationReached(wantedAbsConfig, sailorsNotMoving, gameShip)) {
				actsMoves = firstSailorConfig(wantedOarConfig, wantedAbsConfig, oarReachableForSailors, absReachableForSailors, allOars, allAbsEnt, actsMoves, gameShip);
			}

			//when no moves are found, random
			if (actsMoves == null) {
				var unmovedSailors = new ArrayList<Marin>(sailors);
				Cockpit.log("No sailor MoveAction matches the wanted sailor configuration");
				Cockpit.log("wanted oar config" + wantedOarConfig);
				Cockpit.log("wanted absolute configuration" + wantedAbsConfig.toString());
				for (Marin m : sailors) {
					if (!gameShip.hasAt(m.getX(), m.getY(), Rame.class)) {
						var rame =
								Arrays.stream(gameShip.getEntities())
										.filter(
												e ->
														e instanceof Rame
																&& !(sailors.stream()
																.anyMatch(n -> n.getX() == e.getX() && n.getY() == e.getY())))
										.findFirst()
										.get();
						acts.add(new MoveAction(m, rame.getX() - m.getX(), rame.getY() - m.getY()));
						acts.add(new OarAction(m));
						unmovedSailors.remove(m);
						m.setX(rame.getX());
						m.setY(rame.getY());
					}
				}
				for (Marin m : unmovedSailors) {
					var e = gameShip.getEntityHere(m.getPos());
					if(e.isPresent()){
						if(e.get() instanceof Rame)
							acts.add(new OarAction(m));
					}
				}
				return acts;
			} else {

				var unmovedSailors = new ArrayList<Marin>(sailors);
				for (MoveAction m : actsMoves) {
					unmovedSailors.remove(m);
				}
				var oaring = whoShouldOar(wantedOarConfig, actsMoves, unmovedSailors, gameShip);
				if (oaring == null) {
					return new ArrayList<GameAction>();
				}

				//TODO faire executer l'action qui n'est pas OAR

				var actions = new ArrayList<GameAction>();
				actions.addAll(actsMoves);
				actions.addAll(oaring);
				//update sailors
				for (MoveAction m : actsMoves) {
					state.getIp().getSailorById(m.getSailorId()).get().moveRelative(m.getXDistance(), m.getYDistance());
				}

				return actions;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Cockpit.log(e.getMessage());
			return new ArrayList<GameAction>();
		}
	}

	private ArrayList<MoveAction> firstSailorConfig(Pair<Integer, Integer> wantedOarConfig, Set<Pair<Integer, Integer>> wantedAbsConfig, HashMap<Marin, Set<Rame>> possibleSailorOarConfig, HashMap<Marin, Set<Pair<Integer, Integer>>> possibleSailorAbsConfig, Set<Rame> currentOars, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorOarConfig.keySet();
		if (marins.isEmpty())
			return act;

		for (Map.Entry<Marin, Set<Rame>> pair : possibleSailorOarConfig.entrySet()) {
			var marin = pair.getKey();
			for (var rame : pair.getValue()) {
				if (!currentOars.contains(rame))
					continue;
				var oarSailorsMinusThis = new HashMap<>(possibleSailorOarConfig);
				oarSailorsMinusThis.remove(marin);
				var absSailorsMinusThis = new HashMap<>(possibleSailorAbsConfig);
				absSailorsMinusThis.remove(marin);
				var oarsMinusThis = new HashSet<Rame>(currentOars);
				oarsMinusThis.remove(rame);
				var actPlusThis = new ArrayList<>(act);
				actPlusThis.add(new MoveAction(marin, rame.getX() - marin.getX(), rame.getY() - marin.getY()));
				var allMoves = firstSailorConfig(wantedOarConfig, wantedAbsConfig, oarSailorsMinusThis, absSailorsMinusThis, oarsMinusThis, currentEntities, actPlusThis, gameShip);
				if (allMoves != null) {
					if (isOarConfigurationReached(wantedOarConfig, allMoves, gameShip)) {
						var absMoves = firstSailorAbsConfig(wantedAbsConfig, absSailorsMinusThis, currentEntities, new ArrayList<MoveAction>(), gameShip);
						if (absMoves != null) {
							var moves = new ArrayList<MoveAction>();
							moves.addAll(allMoves);
							moves.addAll(absMoves);
							return moves;
						}
					}
				}
			}
		}
		return null;
	}

	private ArrayList<MoveAction> firstSailorAbsConfig(Set<Pair<Integer, Integer>> wantedAbsConfig, HashMap<Marin, Set<Pair<Integer, Integer>>> possibleSailorAbsConfig, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorAbsConfig.keySet();
		if (marins.isEmpty())
			return act;
		if(wantedAbsConfig.isEmpty())
			return act;

		for (Map.Entry<Marin, Set<Pair<Integer, Integer>>> pair : possibleSailorAbsConfig.entrySet()) {
			var marin = pair.getKey();
			for (var ent : pair.getValue()) {
				if (!currentEntities.contains(ent))
					continue;
				var sailorsMinusThis = new HashMap<>(possibleSailorAbsConfig);
				sailorsMinusThis.remove(marin);
				var entsMinusThis = new HashSet<Pair<Integer, Integer>>(currentEntities);
				entsMinusThis.remove(ent);
				var actPlusThis = new ArrayList<>(act);
				actPlusThis.add(new MoveAction(marin, ent.first - marin.getX(), ent.second - marin.getY()));
				var allMoves = firstSailorAbsConfig(wantedAbsConfig, sailorsMinusThis, entsMinusThis, actPlusThis, gameShip);
				if (allMoves != null) {
					if (isAbsConfigurationReached(wantedAbsConfig, allMoves, gameShip)) {
						return allMoves;
					}
				}
			}
		}
		return null;
	}

	private boolean isAbsConfigurationReached(Set<Pair<Integer, Integer>> wantedAbsConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		if(wantedAbsConfig.isEmpty())
			return true;

		var obj = new HashSet<Pair<Integer, Integer>>();
		for (MoveAction g : act) {
			var entity = Pair.of(g.getSailor().getX() + g.getXDistance(), g.getSailor().getY() + g.getYDistance());

			try {
				obj.add(entity);
			} catch (Exception e) {
				Cockpit.log(e.getMessage());
				return false;
			}
			if (obj.containsAll(wantedAbsConfig))
				return true;
		}
		return false;
	}

	private boolean isOarConfigurationReached(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		var obj = Pair.of(0, 0);
		for (MoveAction g : act) {
			var entity = Pair.of(g.getSailor().getX() + g.getXDistance(), g.getSailor().getY() + g.getYDistance());
			Rame oar;
			try {
				var entHere = gameShip.getEntityHere(entity);
				if (entHere.isEmpty()) {
					//no entity here
					continue;
				}
				if (entHere.get() instanceof Rame) {
					oar = (Rame) entHere.get();
					if (gameShip.isOarLeft(oar)) {
						obj = Pair.of(obj.first + 1, obj.second);
					} else {
						obj = Pair.of(obj.first, obj.second + 1);
					}
				}
			} catch (Exception e) {
				Cockpit.log(e.getMessage());
				return false;
			}
			if (obj.first >= wantedOarConfig.first && obj.second >= wantedOarConfig.second) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<OarAction> whoShouldOar(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, ArrayList<Marin> unmovedSailors, Bateau gameShip) {
		var oaring = new ArrayList<OarAction>();
		var obj = Pair.of(0, 0);
		ArrayList<Marin> sailorAndDistance = new ArrayList<>();
		for (var move : act) {
			sailorAndDistance.add(new Marin(move.getSailorId(), move.getSailor().getX() + move.getXDistance(), move.getSailor().getY() + move.getYDistance(), move.getSailor().getName()));
		}
		sailorAndDistance.addAll(unmovedSailors);
		for (var s : sailorAndDistance) {
			var m = s;
			var pos = s.getPos();
			Rame oar;
			try {
				var entHere = gameShip.getEntityHere(pos);
				if (entHere.isEmpty()) {
					//no entity here
					continue;
				}
				if (entHere.get() instanceof Rame) {
					oar = (Rame) entHere.get();
					if (gameShip.isOarLeft(oar)) {
						if (obj.first.equals(wantedOarConfig.first)) {
							continue;
						} else {
							obj = Pair.of(obj.first + 1, obj.second);
							oaring.add(new OarAction(m));
						}
					} else {
						if (obj.second.equals(wantedOarConfig.second)) {
							continue;
						} else {
							obj = Pair.of(obj.first, obj.second + 1);
							oaring.add(new OarAction(m));
						}
					}
				}
			} catch (Exception e) {
				Cockpit.log(e.getMessage());
				return null;
			}
			if (obj.equals(wantedOarConfig)) {
				return oaring;
			}
		}
		Cockpit.log("Could not establish who should row");
		return null;
	}

	@Override
	public boolean isValidated(GameState state) {
		return false;
	}

}
