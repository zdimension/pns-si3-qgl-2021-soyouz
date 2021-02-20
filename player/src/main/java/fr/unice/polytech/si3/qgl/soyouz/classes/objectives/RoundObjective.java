package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Trigonometry;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;

public class RoundObjective extends Objective {

	private final Map<Class<? extends OnboardEntity>, Class<?>> configurationShape = Map.of(
			Rame.class, Pair.class  //rajouter que c'est Pair<Integer, Integer>
	);

	private final HashMap<Class<? extends OnboardEntity>, Object> wantedConfiguration;



	public RoundObjective(HashMap<Class<? extends OnboardEntity>, Object> wantedConfiguration) {
		this.wantedConfiguration = wantedConfiguration;
	}


	private ArrayList<MoveAction> firstSailorConfig(Pair<Integer, Integer> wantedConfig, HashMap<Marin, Set<Rame>> possibleSailorConfig, Set<Rame> currentOars, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorConfig.keySet();
		if (marins.isEmpty())
			return act;

		for (Map.Entry<Marin, Set<Rame>> pair : possibleSailorConfig.entrySet()) {
			var marin = pair.getKey();
			for (var rame : pair.getValue()) {
				if (!currentOars.contains(rame))
					continue;
				var sailorsMinusThis = new HashMap<>(possibleSailorConfig);
				sailorsMinusThis.remove(marin);
				var oarsMinusThis = new HashSet<Rame>(currentOars);
				oarsMinusThis.remove(rame);
				var actPlusThis = new ArrayList<>(act);
				actPlusThis.add(new MoveAction(marin, rame.getX() - marin.getX(), rame.getY() - marin.getY()));
				var allMoves = firstSailorConfig(wantedConfig, sailorsMinusThis, oarsMinusThis, actPlusThis, gameShip);
				if (allMoves != null) {
					if (isOarConfigurationReached(wantedConfig, allMoves, gameShip)) {
						return allMoves;
					}
				}
			}
		}


		return null;
	}

	private boolean isOarConfigurationReached(Pair<Integer, Integer> wantedConfig, ArrayList<MoveAction> act, Bateau gameShip) {
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
				return false;
			}
			if (obj.first >= wantedConfig.first && obj.second >= wantedConfig.second) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<OarAction> whoShouldOar(Pair<Integer, Integer> wantedConfig, ArrayList<MoveAction> act, ArrayList<Marin> unmovedSailors, Bateau gameShip) {
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
						if (obj.first.equals(wantedConfig.first)) {
							continue;
						} else {
							obj = Pair.of(obj.first + 1, obj.second);
							oaring.add(new OarAction(m));
						}
					} else {
						if (obj.second.equals(wantedConfig.second)) {
							continue;
						} else {
							obj = Pair.of(obj.first, obj.second + 1);
							oaring.add(new OarAction(m));
						}
					}
				}
			} catch (Exception e) {
				return null;
			}
			if (obj.equals(wantedConfig)) {
				return oaring;
			}
		}
		return null;
	}

	@Override
	public boolean isValidated(GameState state) {
		return false;
	}

	@Override
	public List<GameAction> resolve(GameState state) {
		var acts = new ArrayList<GameAction>();
		var sailors = new ArrayList<>(Arrays.asList(state.getIp().getSailors()));
		var gameShip = state.getIp().getShip();
		try {


			var oarReachableForSailors = new HashMap<Marin, Set<Rame>>();
			var allOars = new HashSet<Rame>();
			var sailorsNotMoving = new ArrayList<MoveAction>();
			for (Marin m : sailors) {
				oarReachableForSailors.put(m, new HashSet<>());
				sailorsNotMoving.add(new MoveAction(m, 0, 0));
			}
			Pair<Integer,Integer> wantedOarConfig;
			try{
				wantedOarConfig = Pair.of((Integer)((Pair) wantedConfiguration.get(Rame.class)).first, (Integer) ((Pair) wantedConfiguration.get(Rame.class)).second);
			}
			catch(Exception e){
				throw new RuntimeException("Java is a fucking trash");
			}

			//compute all reachable oars
			for (OnboardEntity ent : gameShip.getEntities()) {
				if (!(ent instanceof Rame))
					continue;
				var r = (Rame) ent;
				allOars.add(r);
				for (Marin m : sailors) {
					if (m.isAbsPosReachable(r.getPos()))
						oarReachableForSailors.get(m).add(r);
				}
			}

			var actsMoves = new ArrayList<MoveAction>();

			if (!isOarConfigurationReached(wantedOarConfig, sailorsNotMoving, gameShip)) {
				actsMoves = firstSailorConfig(wantedOarConfig, oarReachableForSailors, allOars, actsMoves, gameShip);
			}
			var unmovedSailors = new ArrayList<Marin>(sailors);
			for (MoveAction m : actsMoves) {
				unmovedSailors.remove(m);
			}

			//when no moves are found, random
			if (actsMoves == null) {
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
						m.setX(rame.getX());
						m.setY(rame.getY());
					}
				}
				return acts;
			} else {
				var oaring = whoShouldOar(wantedOarConfig, actsMoves, unmovedSailors, gameShip);
				if (oaring == null) {
					return new ArrayList<GameAction>();
				}
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
			e.printStackTrace();
			return new ArrayList<GameAction>();
		}
	}
}
