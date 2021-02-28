package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.TurnAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.TempRoundChoice;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.ComputeMoveSailor;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.WantedSailorConfig;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Objective to be performed in a round
 */
public class RoundObjective extends Objective {

	private final Map<Class<? extends OnboardEntity>, Class<?>> configurationShape = Map.of(
			Rame.class, Pair.class,  //rajouter que c'est Pair<Integer, Integer>
			Gouvernail.class, Double.class
	);

	//private final HashMap<Class<? extends OnboardEntity>, Object> wantedConfiguration;
	private final WantedSailorConfig wanted;


	public RoundObjective(WantedSailorConfig wanted) {
		//this.wantedConfiguration = wantedConfiguration;
		this.wanted = wanted;
	}

	@Override
	public List<GameAction> resolve(GameState state) {
		var acts = new ArrayList<GameAction>();
		var sailors = new ArrayList<>(Arrays.asList(state.getIp().getSailors()));
		var gameShip = state.getIp().getShip();
		var tempChoice = new TempRoundChoice(new ArrayList<>(Arrays.asList(state.getIp().getShip().getEntities())), sailors);
		try {
			var reachableForSailors = new HashSet<ComputeMoveSailor>();
			/*
			var oarReachableForSailors = new HashMap<Marin, Set<Rame>>();
			var absReachableForSailors = new HashMap<Marin, Set<Pair<Integer, Integer>>>();
			 */
			var allOars = new HashSet<Rame>();
			var allAbsEnt = new HashSet<Pair<Integer, Integer>>();
			var sailorsNotMoving = new ArrayList<MoveAction>();
			for (Marin m : sailors) {
				/*
				oarReachableForSailors.put(m, new HashSet<>());
				absReachableForSailors.put(m, new HashSet<>());
				 */
				sailorsNotMoving.add(new MoveAction(m, 0, 0));
				reachableForSailors.add(new ComputeMoveSailor(m, new HashSet<>(Arrays.asList(state.getIp().getShip().getEntities()))));
			}
			/*
			//how many sailors on the left and the right
			Pair<Integer, Integer> wantedOarConfig = null;
			//coordinates of entities having only one instance on the ship
			HashSet<Pair<Integer, Integer>> wantedAbsConfig = new HashSet<>();
			try {
				if (wantedConfiguration.containsKey(Rame.class))
					wantedOarConfig = Pair.of((Integer) ((Pair) wantedConfiguration.get(Rame.class)).first, (Integer) ((Pair) wantedConfiguration.get(Rame.class)).second);
				if (wantedConfiguration.containsKey(Gouvernail.class)) {
					wantedAbsConfig.add(state.getNp().getShip().findFirstPosOfEntity(Gouvernail.class));
				}

			} catch (Exception e) {
				Cockpit.log("Here are Java's limits");
				throw new RuntimeException("Java is a fucking piece of shit");
			}*/

			//compute all reachable oars

			for (OnboardEntity ent : gameShip.getEntities()) {
				if (ent instanceof Rame) {
					var r = (Rame) ent;
					allOars.add(r);
					/*
					for (Marin m : sailors) {
						if (m.isAbsPosReachable(r.getPos()))
							oarReachableForSailors.get(m).add(r);
					}

					 */
				} else {
					allAbsEnt.add(ent.getPos());
					/*
					for (Marin m : sailors) {
						if (m.isAbsPosReachable(ent.getPos()))
							absReachableForSailors.get(m).add(ent.getPos());
					}

					 */
				}
			}

			var actsMoves = new ArrayList<MoveAction>();

			/*
			if (!isOarConfigurationReached(wantedOarConfig, sailorsNotMoving, gameShip) || !isAbsConfigurationReached(wantedAbsConfig, sailorsNotMoving, gameShip)) {
				actsMoves = firstSailorConfig(wantedOarConfig, wantedAbsConfig, oarReachableForSailors, absReachableForSailors, allOars, allAbsEnt, actsMoves, gameShip);
			}
			 */
			if (!isOarConfigurationReached(wanted.getOarConfig(), sailorsNotMoving, gameShip) || !isAbsConfigurationReached(wanted.getAbsConfigPos(), sailorsNotMoving, gameShip)) {
				actsMoves = firstSailorConfig(wanted, reachableForSailors, allOars, allAbsEnt, actsMoves, gameShip);
			}

			//when no moves are found, all sailors will row
			if (actsMoves == null) {
				Cockpit.log("Sailor configuration cannot be respected");
				return acts;
				/*
				var unmovedSailors = new ArrayList<Marin>(sailors);
				Cockpit.log("No sailor MoveAction matches the wanted sailor configuration");
				Cockpit.log("wanted oar config" + wantedOarConfig);
				Cockpit.log("wanted absolute configuration" + wantedAbsConfig.toString());
				for (Marin m : sailors) {
					if (!gameShip.hasAt(m.getX(), m.getY(), Rame.class)) {
						//TODO A REFACTO CA OPTIONAL CAN BE NULLABLE
						var rame =
								Arrays.stream(gameShip.getEntities())
										.filter(e ->
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
					if (e.isPresent()) {
						if (e.get() instanceof Rame)
							acts.add(new OarAction(m));
					}
				}
				return acts;
				 */
			} else {
				for (MoveAction m : actsMoves) {
					try {
						tempChoice.moveSailor(m);
					} catch (Exception e) {
						Cockpit.log("Error moving sailors : " + e.getMessage());
						throw e;
					}
				}

				/*
				var unmovedSailors = new ArrayList<Marin>(sailors);
				for (MoveAction m : actsMoves) {
					unmovedSailors.remove(m.getSailor());
				}
				 */
				var oaring = whoShouldOar(wanted.getOarConfig(), actsMoves, new ArrayList<>(tempChoice.getUnmovedVacantSailors()), gameShip);
				if (oaring == null) {
					return new ArrayList<GameAction>();
				}

				/*
				//all sailors, removing busy sailors
				Pair<ArrayList<Marin>, ArrayList<MoveAction>> vacantSailors = Pair.of(new ArrayList<>(tempChoice.getUnmovedVacantSailors()), new ArrayList<>(actsMoves));
				for (var oar : oaring) {
					if (vacantSailors.first.contains(oar.getSailor())) {
						vacantSailors.first.remove(oar.getSailor());
					} else {
						for (var m : actsMoves) {
							if (m.getSailor().equals(oar.getSailor())) {
								vacantSailors.second.remove(m);
								break;
							}
						}
					}
				}

				 */

				//todo hiresailors dans tempChoice pour ceux qui rament
				for(var oarAct : oaring){
					tempChoice.hireSailor(oarAct.getSailor(), oarAct);
				}

				var wantedConfiguration = wanted.getAbsConfig();

				for (var ent : wantedConfiguration) {
					if (ent instanceof Gouvernail) {
						//todo store it rather than get multiple times
						var pos = ((Gouvernail) ent).getPos();
						var gouvSailor = tempChoice.findFirstVacantSailorHere(pos);
						if (gouvSailor == null) {
							Cockpit.log("No sailor could move to Rudder");
							continue;
						} else {
							var turn = new TurnAction(gouvSailor, wanted.getRotation());
							tempChoice.hireSailor(gouvSailor, turn);
							//actions.add(turn);
						}
						/*
						var unmoved = vacantSailors.first.stream().filter(m -> m.getPos().equals(pos)).findFirst();
						if (unmoved.isPresent()) {
							actions.add(new TurnAction(unmoved.get(), (Double) pair.getValue()));
							continue;
						}
						var moved = vacantSailors.second.stream().filter(m -> m.newPos().equals(pos)).findFirst();
						moved.ifPresent(moveAction -> actions.add(new TurnAction(moveAction.getSailor(), (Double) pair.getValue())));

						 */
					}

				}

				var actions = new ArrayList<GameAction>(tempChoice.getAllActions());
				//actions.addAll(actsMoves);
				//actions.addAll(oaring);

				//update sailors
				for (MoveAction m : tempChoice.getAllMoves()) {
					var sailor = state.getIp().getSailorById(m.getSailorId());
					sailor.ifPresent(marin -> marin.moveRelative(m.getXDistance(), m.getYDistance()));
				}
				//Deplacer marin
				//ramer
				//hisser la voile
				//affaler la voile
				//gouvernail
				//monter la garde
				//orienter canon
				//charger canon
				//tirer canon

				var actTypes = List.of(
						MoveAction.class,
						OarAction.class,
						TurnAction.class
				);

				actions.sort(Comparator.comparingInt(act -> actTypes.indexOf(act.getClass())));

				return actions;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Cockpit.log("Error resolving RoundObjective : " + e.getMessage());
			return new ArrayList<GameAction>();
		}
	}

	/*
	private ArrayList<MoveAction> firstSailorConfig(Pair<Integer, Integer> wantedOarConfig, Set<Pair<Integer, Integer>> wantedAbsConfig, HashMap<Marin, Set<Rame>> possibleSailorOarConfig, HashMap<Marin, Set<Pair<Integer, Integer>>> possibleSailorAbsConfig, Set<Rame> currentOars, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorOarConfig.keySet();
		if (marins.isEmpty())
			return act;

		if (isOarConfigurationReached(wantedOarConfig, act, gameShip)) {
			if (isAbsConfigurationReached(wantedAbsConfig, act, gameShip)) {
				return act;
			}
			var absMoves = firstSailorAbsConfig(wantedAbsConfig, possibleSailorAbsConfig, currentEntities, new ArrayList<MoveAction>(), gameShip);
			if (absMoves != null) {
				var moves = new ArrayList<MoveAction>();
				moves.addAll(act);
				moves.addAll(absMoves);
				return moves;
			}
			return null;

		} else {


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
							//au pire, appel recusrif avec wanted oar config vide
							if (!isAbsConfigurationReached(wantedAbsConfig, allMoves, gameShip)) {
								if (!oarSailorsMinusThis.isEmpty()) {
									var absMoves = firstSailorAbsConfig(wantedAbsConfig, absSailorsMinusThis, currentEntities, new ArrayList<MoveAction>(), gameShip);
									if (absMoves != null) {
										var moves = new ArrayList<MoveAction>();
										moves.addAll(allMoves);
										moves.addAll(absMoves);
										return moves;
									}
								}
							} else {
								return allMoves;
							}
						}
					}
				}
			}
			return null;
		}
	}
	 */

	private ArrayList<MoveAction> firstSailorConfig(WantedSailorConfig wantedConfig, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<Rame> currentOars, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorConfig.stream().map(ComputeMoveSailor::getSailor).collect(Collectors.toSet());
		if (marins.isEmpty()) {
			return act;
		}

		if (isOarConfigurationReached(wantedConfig.getOarConfig(), act, gameShip)) {
			if (isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), act, gameShip)) {
				return act;
			}
			var possibleSailorConfigAbs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getLonelyEntities)));
			var absMoves = firstSailorAbsConfig(wantedConfig.getAbsConfigPos(), possibleSailorConfigAbs, currentEntities, act, gameShip, true);
			if (absMoves != null) {
				var moves = new ArrayList<MoveAction>();
				//moves.addAll(act);
				moves.addAll(absMoves);
				return moves;
			}
		} else {
			var possibleSailorConfigOar = new HashMap<Marin, Set<? extends Rame>>();
			for (var p : possibleSailorConfig) {
				var temp = p.getEntities().stream().filter(e -> e instanceof Rame).collect(Collectors.toSet());
				possibleSailorConfigOar.put(p.getSailor(), (Set<Rame>) temp);
			}

			for (Map.Entry<Marin, Set<? extends Rame>> pair : possibleSailorConfigOar.entrySet()) {
				var marin = pair.getKey();
				for (var rame : pair.getValue()) {
					if (!currentOars.contains(rame))
						continue;
					//var sailorsMinusThis = new HashSet<>(possibleSailorConfig);
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(marin)).collect(Collectors.toSet()));
					//sailorsMinusThis.remove(marin);
					//var absSailorsMinusThis = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getLonelyEntity)));
					//absSailorsMinusThis.remove(marin);
					var oarsMinusThis = new HashSet<Rame>(currentOars);
					oarsMinusThis.remove(rame);
					var actPlusThis = new ArrayList<>(act);
					actPlusThis.add(new MoveAction(marin, rame.getX() - marin.getX(), rame.getY() - marin.getY()));
					//var possibleSailorConfigAbs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getLonelyEntity)));
					var allMoves = firstSailorConfig(wantedConfig, sailorsMinusThis, oarsMinusThis, currentEntities, actPlusThis, gameShip);
					if (allMoves != null) {
						if (isOarConfigurationReached(wantedConfig.getOarConfig(), allMoves, gameShip)) {
							if (!isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), allMoves, gameShip)) {
								if (!sailorsMinusThis.isEmpty()) {
									var possibleSailorConfigAbs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getLonelyEntities)));
									var absMoves = firstSailorAbsConfig(wantedConfig.getAbsConfigPos(), possibleSailorConfigAbs, currentEntities, actPlusThis, gameShip, true);
									if (absMoves != null) {
										var moves = new ArrayList<MoveAction>();
										moves.addAll(allMoves);
										moves.addAll(absMoves);
										return moves;
									}
								}
							} else {
								return allMoves;
							}

						}
					}


				}
			}
		}
		return null;

	}


	/*
		private ArrayList<MoveAction> firstSailorAbsConfig(Set<Pair<Integer, Integer>> wantedAbsConfig, HashMap<Marin, Set<Pair<Integer, Integer>>> possibleSailorAbsConfig, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorAbsConfig.keySet();
		if (marins.isEmpty())
			return act;
		if (wantedAbsConfig.isEmpty())
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
	 */

	private ArrayList<MoveAction> firstSailorAbsConfig(Set<PosOnShip> wantedAbsConfig, HashMap<Marin, Set<? extends OnboardEntity>> possibleSailorAbsConfig, Set<Pair<Integer, Integer>> currentEntities, ArrayList<MoveAction> act, Bateau gameShip, boolean placeholder) {
		var marins = possibleSailorAbsConfig.keySet();
		if (marins.isEmpty())
			return act;
		if (wantedAbsConfig.isEmpty())
			return act;

		for (Map.Entry<Marin, Set<? extends OnboardEntity>> pair : possibleSailorAbsConfig.entrySet()) {
			var marin = pair.getKey();
			for (var ent : pair.getValue()) {
				//todo if contains of type
				if (!currentEntities.contains(ent.getPos()))
					continue;
				var sailorsMinusThis = new HashMap<>(possibleSailorAbsConfig);
				sailorsMinusThis.remove(marin);
				var entsMinusThis = new HashSet<>(currentEntities);
				entsMinusThis.remove(ent.getPos());
				var actPlusThis = new ArrayList<>(act);
				actPlusThis.add(new MoveAction(marin, ent.getX() - marin.getX(), ent.getY() - marin.getY()));
				var allMoves = firstSailorAbsConfig(wantedAbsConfig, sailorsMinusThis, entsMinusThis, actPlusThis, gameShip, true);
				if (allMoves != null) {
					if (isAbsConfigurationReached(wantedAbsConfig, allMoves, gameShip)) {
						return allMoves;
					}
				}
			}
		}
		return null;
	}


	private boolean isAbsConfigurationReached(Set<PosOnShip> wantedAbsConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedAbsConfig == null || wantedAbsConfig.isEmpty())
			return true;

		var obj = new HashSet<PosOnShip>();
		for (MoveAction g : act) {
			var pos = new PosOnShip(g.newPos());

			try {
				obj.add(pos);
			} catch (Exception e) {
				Cockpit.log("Error checking if configuration (not oars) reached : " + e.getMessage());
				return false;
			}
			if (obj.containsAll(wantedAbsConfig))
				return true;
		}
		return false;
	}


	private boolean isOarConfigurationReached(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedOarConfig == null) {
			return true;
		}
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
				Cockpit.log("Error checking if oar configuration reached : " + e.getMessage());
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
				Cockpit.log("Error determining who should row : " + e.getMessage());
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
