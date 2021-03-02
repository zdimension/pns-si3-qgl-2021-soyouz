package fr.unice.polytech.si3.qgl.soyouz.classes.objectives;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.*;
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
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Objective to be performed in a round
 */
public class RoundObjective implements Objective {

	private final WantedSailorConfig wanted;
	private static final Logger logger = Logger.getLogger(RoundObjective.class.getSimpleName());

	public RoundObjective(WantedSailorConfig wanted) {
		this.wanted = wanted;
	}

	@Override
	public List<GameAction> resolve(GameState state) {
		var acts = new ArrayList<GameAction>();
		var sailors = new ArrayList<>(Arrays.asList(state.getIp().getSailors()));
		var gameShip = state.getIp().getShip();
		var tempChoice = new TempRoundChoice(new ArrayList<>(Arrays.asList(gameShip.getEntities())), sailors);
		try {
			var reachableForSailors = new HashSet<ComputeMoveSailor>();
			var allOars = new HashSet<Rame>();
			var allAbsEnt = new HashSet<PosOnShip>();
			var sailorsNotMoving = new ArrayList<Marin>();
			for (Marin m : sailors) {

				sailorsNotMoving.add(m);
				reachableForSailors.add(new ComputeMoveSailor(m, new HashSet<>(Arrays.asList(state.getIp().getShip().getEntities()))));
			}

			for (OnboardEntity ent : gameShip.getEntities()) {
				if (ent instanceof Rame) {
					var r = (Rame) ent;
					allOars.add(r);
				} else {
					allAbsEnt.add(ent.getPos());
				}
			}

			var actsMoves = new ArrayList<MoveAction>();

			if (!isOarConfigReached(wanted.getOarConfig(), new ArrayList<>(), sailorsNotMoving, gameShip) || !isAbsConfigurationReached(wanted.getAbsConfig(), new ArrayList<>(), sailorsNotMoving, gameShip)) {
				System.out.println("calling");
				//actsMoves = findConifg(wanted, reachableForSailors, Set.of(gameShip.getEntities()), actsMoves, gameShip);
				actsMoves = findConfig2(wanted, reachableForSailors, Set.of(state.getIp().getShip().getEntities()), actsMoves, gameShip);
			}
			System.out.println("after calling");

			//when no moves are found, all sailors will row
			if (actsMoves == null) {
				//Cockpit.log("Sailor configuration cannot be respected");
				logger.log(Level.INFO, "Sailor move configuration cannot be respected");
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
					if (e.isPresent() && e.get() instanceof Rame)
						acts.add(new OarAction(m));
				}
				return acts;
				 */
			} else {
				//todo retirer les moves de moveAction avec 0,0 dedans
				for (MoveAction m : actsMoves) {
					try {
						if (m.getXDistance() == 0 && m.getYDistance() == 0)
							continue;
						tempChoice.moveSailor(m);
					} catch (Exception e) {
						//Cockpit.log("Error moving sailors : " + e.getMessage());
						logger.log(Level.SEVERE, "Error moving sailors : " + e.getMessage());
						throw e;
					}
				}
				System.out.println("wanted config");
				System.out.println(wanted.getOarConfig());
				System.out.println("Acts moves");
				System.out.println(actsMoves);
				System.out.println("unmoved");
				System.out.println(tempChoice.getUnmovedVacantSailors());
				System.out.println("On boat entities");
				System.out.println(Arrays.toString(gameShip.getEntities()));
				var oaring = whoShouldRow(wanted.getOarConfig(), actsMoves, new ArrayList<>(tempChoice.getUnmovedVacantSailors()), gameShip);
				if (oaring == null) {
					return new ArrayList<>();
				}

				for (var oarAct : oaring) {
					tempChoice.hireSailor(oarAct.getSailor(), oarAct);
				}

				var wantedConfiguration = wanted.getAbsConfig();

				for (var ent : wantedConfiguration) {
					if (ent instanceof Gouvernail) {
						//todo store it rather than get multiple times
						var pos = ((Gouvernail) ent).getPosCoord();
						var gouvSailor = tempChoice.findFirstVacantSailorHere(pos);
						if (gouvSailor == null) {
							//Cockpit.log("No sailor could move to Rudder");
							logger.log(Level.SEVERE, "No sailor could move to Rudder");
							continue;
						} else {
							var turn = new TurnAction(gouvSailor, wanted.getRotation());
							tempChoice.hireSailor(gouvSailor, turn);
						}
					}
				}

				var actions = new ArrayList<GameAction>(tempChoice.getAllActions());

				for (MoveAction m : tempChoice.getAllMoves()) {
					var sailor = state.getIp().getSailorById(m.getSailorId());
					sailor.ifPresent(marin -> marin.moveRelative(m.getXDistance(), m.getYDistance()));
				}

				var actTypes = List.of(
						MoveAction.class,
						OarAction.class,
						LiftSailAction.class,
						LowerSailAction.class,
						TurnAction.class
						//monter la garde
						//orienter canon
						//charger canon
						//tirer canon
				);

				actions.sort(Comparator.comparingInt(act -> actTypes.indexOf(act.getClass())));

				return actions;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			//Cockpit.log("Error resolving RoundObjective : " + e.getMessage());
			logger.log(Level.SEVERE, "Error resolving RoundObjective : " + e.getMessage());
			return new ArrayList<GameAction>();
		}
	}

	private ArrayList<MoveAction> firstSailorConfig(WantedSailorConfig wantedConfig, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<Rame> currentOars, Set<PosOnShip> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		var marins = possibleSailorConfig.stream().map(ComputeMoveSailor::getSailor).collect(Collectors.toSet());
		if (marins.isEmpty()) {
			return act;
		}
		//todo retirer les move action avec 0,0

		//System.out.println("before if");
		if (isOarConfigReached(wantedConfig.getOarConfig(), act, marins, gameShip)) {
			//if (isOarConfigurationReached(wantedConfig.getOarConfig(), act, gameShip)) {
			//System.out.println("after if");
			if (isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), act, gameShip)) {
				//System.out.println("after second if");
				return act;
			}
			var possibleSailorConfigAbs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getReachableSingleEntities)));
			var absMoves = firstSailorAbsConfig(wantedConfig.getAbsConfigPos(), possibleSailorConfigAbs, currentEntities, act, gameShip, true);
			if (absMoves != null) {
				if (isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), absMoves, gameShip)) {
					//System.out.println("All moves found in first loop");
					return absMoves;
				}
			}
		} else {
			var possibleSailorConfigOar = new HashMap<Marin, Set<? extends Rame>>();
			for (var p : possibleSailorConfig) {
				possibleSailorConfigOar.put(p.getSailor(), Util.filterType(p.getReachableEntities().stream(), Rame.class).collect(Collectors.toSet()));
			}

			for (Map.Entry<Marin, Set<? extends Rame>> pair : possibleSailorConfigOar.entrySet()) {
				var marin = pair.getKey();
				for (var rame : pair.getValue()) {
					if (!currentOars.contains(rame))
						continue;
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(marin)).collect(Collectors.toSet()));
					var oarsMinusThis = new HashSet<Rame>(currentOars);
					oarsMinusThis.remove(rame);
					var actPlusThis = new ArrayList<>(act);
					actPlusThis.add(new MoveAction(marin, rame.getX() - marin.getX(), rame.getY() - marin.getY()));
					var allMoves = firstSailorConfig(wantedConfig, sailorsMinusThis, oarsMinusThis, currentEntities, actPlusThis, gameShip);
					if (allMoves != null) {
						//todo remplacer par la nouvelle fonction
						if (isOarConfigReached(wantedConfig.getOarConfig(), allMoves, sailorsMinusThis.stream().map(ComputeMoveSailor::getSailor).collect(Collectors.toList()), gameShip)) {
							//if (isOarConfigurationReached(wantedConfig.getOarConfig(), allMoves, gameShip)) {
							if (!isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), allMoves, gameShip)) {
								if (!sailorsMinusThis.isEmpty()) {
									var possibleSailorConfigAbs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().collect(Collectors.toMap(ComputeMoveSailor::getSailor, ComputeMoveSailor::getReachableSingleEntities)));
									var absMoves = firstSailorAbsConfig(wantedConfig.getAbsConfigPos(), possibleSailorConfigAbs, currentEntities, actPlusThis, gameShip, true);
									if (absMoves != null) {
										var moves = new ArrayList<MoveAction>();
										moves.addAll(allMoves);
										moves.addAll(absMoves);
										System.out.println("All moves found in second loop");
										return moves;
									}
								}
							} else {
								//System.out.println("All moves found");
								return allMoves;
							}

						}
					}


				}
			}
		}
		//System.out.println("return null");
		return null;

	}

	private ArrayList<MoveAction> firstSailorAbsConfig(Set<PosOnShip> wantedAbsConfig, HashMap<Marin, Set<? extends OnboardEntity>> possibleSailorAbsConfig, Set<PosOnShip> currentEntities, ArrayList<MoveAction> act, Bateau gameShip, boolean placeholder) {
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
		return new ArrayList<>();
	}

	private ArrayList<MoveAction> findConifg(WantedSailorConfig wantedConfig, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<? extends OnboardEntity> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedConfig == null ||
				(wantedConfig.getAbsConfigPos() == null && wantedConfig.getOarConfig() == null) ||
				(wantedConfig.getAbsConfigPos() != null && wantedConfig.getAbsConfigPos().isEmpty() && wantedConfig.getOarConfig() == null) ||
				(wantedConfig.getOarConfig() != null && wantedConfig.getOarConfig().equals(Pair.of(0, 0)) && wantedConfig.getAbsConfigPos() == null) ||
				(wantedConfig.getOarConfig() != null && wantedConfig.getOarConfig().equals(Pair.of(0, 0)) && (wantedConfig.getAbsConfigPos() != null && wantedConfig.getAbsConfigPos().isEmpty())))
			return new ArrayList<>();

		if (wantedConfig.getAbsConfigPos() != null && wantedConfig.getAbsConfigPos().isEmpty()) {
			if (isOarConfigurationReached(wantedConfig.getOarConfig(), act, gameShip)) {
				return act;
			}
			var oars = new HashMap<Marin, Set<Rame>>(possibleSailorConfig.stream().map(p -> Map.entry(p.getSailor(), p.getOars())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
			for (Map.Entry<Marin, Set<Rame>> pair : oars.entrySet()) {
				if (pair.getValue() == null || pair.getValue().isEmpty())
					continue;
				var sailor = pair.getKey();
				var isOar = gameShip.getEntityHere(sailor.getPos());
				if (isOar.isPresent()) {
					var oar = (Rame) isOar.get();
					if (((oar.isLeft() && wantedConfig.getOarConfig().first > 0) || (!oar.isLeft() && wantedConfig.getOarConfig().second > 0)) && currentEntities.contains(oar)) {
						var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
						WantedSailorConfig wantedMinusThis = null;
						if (oar.isLeft()) {
							wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfig.getOarConfig().first - 1, wantedConfig.getOarConfig().second), wantedConfig.getGouvernail(), wantedConfig.getRotation());
						} else {
							wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfig.getOarConfig().first, wantedConfig.getOarConfig().second - 1), wantedConfig.getGouvernail(), wantedConfig.getRotation());
						}
						var entitiesMinusThis = new HashSet<>(currentEntities);
						entitiesMinusThis.remove(oar);
						if (isOarConfigurationReached(wantedMinusThis.getOarConfig(), act, gameShip)) {
							return act;
						} else {
							var allMoves = findConifg(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, act, gameShip);
							if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), allMoves, gameShip) && isOarConfigurationReached(wantedMinusThis.getOarConfig(), allMoves, gameShip)) {
								return allMoves;
							}
						}
					}
				}
				for (var rame : pair.getValue()) {
					if ((rame.isLeft() && wantedConfig.getOarConfig().first == 0) || (!rame.isLeft() && wantedConfig.getOarConfig().second == 0) || !currentEntities.contains(rame))
						continue;
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
					WantedSailorConfig wantedMinusThis = null;
					if (rame.isLeft()) {
						wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfig.getOarConfig().first - 1, wantedConfig.getOarConfig().second), wantedConfig.getGouvernail(), wantedConfig.getRotation());
					} else {
						wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfig.getOarConfig().first, wantedConfig.getOarConfig().second - 1), wantedConfig.getGouvernail(), wantedConfig.getRotation());
					}
					var entitiesMinusThis = new HashSet<>(currentEntities);
					entitiesMinusThis.remove(rame);
					var actsPlusThis = new ArrayList<>(act);
					var pos = rame.getPos().minus(sailor.getPos());
					actsPlusThis.add(new MoveAction(sailor, pos.getX(), pos.getY()));
					if (isOarConfigurationReached(wantedMinusThis.getOarConfig(), actsPlusThis, gameShip)) {
						return actsPlusThis;
					} else {
						var allMoves = findConifg(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, actsPlusThis, gameShip);
						if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), allMoves, gameShip) && isOarConfigurationReached(wantedMinusThis.getOarConfig(), allMoves, gameShip)) {
							return allMoves;
						}
						continue;
					}
				}
			}
		} else {
			if (isAbsConfigurationReached(wantedConfig.getAbsConfigPos(), act, gameShip)) {
				return act;
			}
			var abs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().map(p -> Map.entry(p.getSailor(), p.getReachableSingleEntities())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
			for (Map.Entry<Marin, Set<? extends OnboardEntity>> pair : abs.entrySet()) {
				if (pair.getValue() == null || pair.getValue().isEmpty())
					continue;
				var sailor = pair.getKey();
				var isEnt = gameShip.getEntityHere(sailor.getPos());
				if (isEnt.isPresent()) {
					var ent = isEnt.get();
					if (wantedConfig.getAbsConfig().contains(ent) && currentEntities.contains(ent)) {
						var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
						//var sailorsMinusThis = new HashSet<>(possibleSailorConfig);
						//sailorsMinusThis.remove(sailor);
						WantedSailorConfig wantedMinusThis = null;
						if (ent instanceof Gouvernail) {
							wantedMinusThis = new WantedSailorConfig(wantedConfig.getOarConfig(), wantedConfig.getGouvernail(), (double) 0);
						}
						//todo add here when new entity added
						var entitiesMinusThis = new HashSet<>(currentEntities);
						entitiesMinusThis.remove(ent);
						if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), act, gameShip)) {
							return act;
						} else {
							var allMoves = findConifg(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, act, gameShip);
							if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), allMoves, gameShip) && isOarConfigurationReached(wantedMinusThis.getOarConfig(), allMoves, gameShip)) {
								return allMoves;
							}
						}
					}

				}

				for (var ent : pair.getValue()) {
					if (!wantedConfig.getAbsConfig().contains(ent) || !currentEntities.contains(ent))
						continue;
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
					WantedSailorConfig wantedMinusThis = null;
					if (ent instanceof Gouvernail) {
						wantedMinusThis = new WantedSailorConfig(wantedConfig.getOarConfig(), wantedConfig.getGouvernail(), (double) 0);
					}
					//todo add here when new entity added
					var entitiesMinusThis = new HashSet<>(currentEntities);
					entitiesMinusThis.remove(ent);
					var actsPlusThis = new ArrayList<>(act);
					var pos = ent.getPos().minus(sailor.getPos());
					actsPlusThis.add(new MoveAction(sailor, pos.getX(), pos.getY()));
					if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), actsPlusThis, gameShip)) {
						return actsPlusThis;
					} else {
						var allMoves = findConifg(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, actsPlusThis, gameShip);
						if (isAbsConfigurationReached(wantedMinusThis.getAbsConfigPos(), allMoves, gameShip) && isOarConfigurationReached(wantedMinusThis.getOarConfig(), allMoves, gameShip)) {
							return allMoves;
						}
						continue;
					}
				}
			}
		}

		return null;
	}

	private ArrayList<MoveAction> findConfig2(WantedSailorConfig wantedConfig, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<? extends OnboardEntity> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		//var absConf = findConfigAbs(wantedConfig.getAbsConfig(), possibleSailorConfig, currentEntities.stream().filter(e -> !(e instanceof Rame)).collect(Collectors.toSet()), act, gameShip);
		//if (absConf == null)
		//	return null;
		var permutations = allSubLists(new ArrayList<ComputeMoveSailor>(possibleSailorConfig));
		for (var pair : permutations) {
			var absConf = findConfigAbs(wantedConfig.getAbsConfig(), new HashSet<ComputeMoveSailor>(pair.getFirst()), currentEntities.stream().filter(e -> !(e instanceof Rame)).collect(Collectors.toSet()), act, gameShip);
			var oarConf = findConfigOar(wantedConfig.getOarConfig(), new HashSet<ComputeMoveSailor>(pair.getSecond()), currentEntities.stream().filter(e -> (e instanceof Rame)).map(e -> (Rame) e).collect(Collectors.toSet()), act, gameShip);
			var unmoved = new ArrayList<Marin>();
			if (absConf != null)
				unmoved.addAll(pair.getFirst().stream().filter(s -> {
					var temp = absConf.stream().map(GameAction::getSailor).collect(Collectors.toSet());
					return !temp.contains(s.getSailor());
				}).map(ComputeMoveSailor::getSailor).collect(Collectors.toSet()));

			if (oarConf != null)
				unmoved.addAll(pair.getSecond().stream().filter(s -> {
					var temp = oarConf.stream().map(GameAction::getSailor).collect(Collectors.toSet());
					return !temp.contains(s.getSailor());
				}).map(ComputeMoveSailor::getSailor).collect(Collectors.toSet()));

			if (absConf != null && oarConf != null) {
				//System.out.println(wantedConfig.getAbsConfigPos());
				//System.out.println(absConf);
				//System.out.println(wantedConfig.getOarConfig());
				//System.out.println(oarConf);
				//System.out.println(unmoved);
				if (isOarConfigReached(wantedConfig.getOarConfig(), absConf, unmoved, gameShip) && isAbsConfigurationReached(wantedConfig.getAbsConfig(), oarConf, unmoved, gameShip)) {

					var allMoves = new ArrayList<MoveAction>();
					allMoves.addAll(absConf);
					allMoves.addAll(oarConf);
					return allMoves;
				}
				//throw new IllegalStateException("This line is not supposed to be reachable");
			}
		}
		return null;
	}

	private ArrayList<MoveAction> findConfigAbs(Set<? extends OnboardEntity> wantedConfigAbs, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<? extends OnboardEntity> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedConfigAbs == null || wantedConfigAbs.isEmpty())
			return new ArrayList<>();


		if (isAbsConfigurationReached(wantedConfigAbs.stream().map(OnboardEntity::getPos).collect(Collectors.toSet()), act, gameShip)) {
			return act;
		}
		var abs = new HashMap<Marin, Set<? extends OnboardEntity>>(possibleSailorConfig.stream().map(p -> Map.entry(p.getSailor(), p.getReachableSingleEntities())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		for (Map.Entry<Marin, Set<? extends OnboardEntity>> pair : abs.entrySet()) {
			if (pair.getValue() == null || pair.getValue().isEmpty())
				continue;
			var sailor = pair.getKey();
			var isEnt = gameShip.getEntityHere(sailor.getPos());
			try {

				if (isEnt.isPresent()) {
					var ent = isEnt.get();
					if (wantedConfigAbs.contains(ent) && currentEntities.contains(ent)) {
						var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
						var wantedMinusThis = new HashSet<>(wantedConfigAbs);
						wantedConfigAbs.remove(ent);
						//todo add here when new entity added
						var entitiesMinusThis = new HashSet<>(currentEntities);
						entitiesMinusThis.remove(ent);
						if (isAbsConfigurationReached(wantedMinusThis.stream().map(OnboardEntity::getPos).collect(Collectors.toSet()), act, gameShip)) {
							return act;
						} else {
							var allMoves = findConfigAbs(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, act, gameShip);
							if (allMoves != null) {
								if (isAbsConfigurationReached(wantedMinusThis.stream().map(OnboardEntity::getPos).collect(Collectors.toSet()), allMoves, gameShip)) {
									return allMoves;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in finding configuration of absolute entity (without moving) : " + e.getMessage());
				throw e;
			}
			try {
				for (var ent : pair.getValue()) {
					if (!wantedConfigAbs.contains(ent) || !currentEntities.contains(ent))
						continue;
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
					var wantedMinusThis = new HashSet<>(wantedConfigAbs);
					wantedMinusThis.remove(ent);
					var entitiesMinusThis = new HashSet<>(currentEntities);
					entitiesMinusThis.remove(ent);
					var actsPlusThis = new ArrayList<>(act);
					var pos = ent.getPos().minus(sailor.getPos());
					actsPlusThis.add(new MoveAction(sailor, pos.getX(), pos.getY()));
					if (isAbsConfigurationReached(wantedMinusThis.stream().map(OnboardEntity::getPos).collect(Collectors.toSet()), actsPlusThis, gameShip)) {
						return actsPlusThis;
					} else {
						var allMoves = findConfigAbs(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, actsPlusThis, gameShip);
						if (allMoves != null) {
							if (isAbsConfigurationReached(wantedMinusThis.stream().map(OnboardEntity::getPos).collect(Collectors.toSet()), allMoves, gameShip)) {
								return allMoves;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in finding configuration of absolute entity (moving) : " + e.getMessage());
				throw e;
			}
		}
		return null;
	}

	private ArrayList<MoveAction> findConfigOar(Pair<Integer, Integer> wantedConfigOar, HashSet<ComputeMoveSailor> possibleSailorConfig, Set<Rame> currentEntities, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedConfigOar == null || wantedConfigOar.equals(Pair.of(0, 0)))
			return new ArrayList<>();


		if (isOarConfigurationReached(wantedConfigOar, act, gameShip)) {
			return act;
		}
		var oars = new HashMap<Marin, Set<Rame>>(possibleSailorConfig.stream().map(p -> Map.entry(p.getSailor(), p.getOars())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		for (Map.Entry<Marin, Set<Rame>> pair : oars.entrySet()) {
			if (pair.getValue() == null || pair.getValue().isEmpty())
				continue;
			var sailor = pair.getKey();
			var isOar = gameShip.getEntityHere(sailor.getPos());
			try {
				if (isOar.isPresent()) {
					var oar = (Rame) isOar.get();
					if (((oar.isLeft() && wantedConfigOar.first > 0) || (!oar.isLeft() && wantedConfigOar.second > 0)) && currentEntities.contains(oar)) {
						var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
						Pair<Integer, Integer> wantedMinusThis;
						if (oar.isLeft()) {
							//wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfigOar.first - 1, wantedConfig.getOarConfig().second), wantedConfig.getGouvernail(), wantedConfig.getRotation());
							wantedMinusThis = Pair.of(wantedConfigOar.getFirst() - 1, wantedConfigOar.getSecond());
						} else {
							wantedMinusThis = Pair.of(wantedConfigOar.getFirst(), wantedConfigOar.getSecond() - 1);
							//wantedMinusThis = new WantedSailorConfig(Pair.of(wantedConfig.getOarConfig().first, wantedConfig.getOarConfig().second - 1), wantedConfig.getGouvernail(), wantedConfig.getRotation());
						}
						var entitiesMinusThis = new HashSet<>(currentEntities);
						entitiesMinusThis.remove(oar);
						if (isOarConfigurationReached(wantedMinusThis, act, gameShip)) {
							return act;
						} else {
							var allMoves = findConfigOar(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, act, gameShip);
							if (isOarConfigurationReached(wantedMinusThis, allMoves, gameShip)) {
								return allMoves;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in finding configuration of oar (without moving) : " + e.getMessage());
				throw e;
			}
			try {

				for (var rame : pair.getValue()) {
					if ((rame.isLeft() && wantedConfigOar.first == 0) || (!rame.isLeft() && wantedConfigOar.second == 0) || !currentEntities.contains(rame))
						continue;
					var sailorsMinusThis = new HashSet<>(possibleSailorConfig.stream().filter(s -> !s.getSailor().equals(sailor)).collect(Collectors.toSet()));
					Pair<Integer, Integer> wantedMinusThis;
					if (rame.isLeft()) {
						wantedMinusThis = Pair.of(wantedConfigOar.getFirst() - 1, wantedConfigOar.getSecond());
					} else {
						wantedMinusThis = Pair.of(wantedConfigOar.getFirst(), wantedConfigOar.getSecond() - 1);
					}
					var entitiesMinusThis = new HashSet<>(currentEntities);
					entitiesMinusThis.remove(rame);
					var actsPlusThis = new ArrayList<>(act);
					var pos = rame.getPos().minus(sailor.getPos());
					actsPlusThis.add(new MoveAction(sailor, pos.getX(), pos.getY()));
					if (isOarConfigReached(wantedMinusThis, actsPlusThis, sailorsMinusThis.stream().map(ComputeMoveSailor::getSailor).collect(Collectors.toSet()), gameShip)) {
						return actsPlusThis;
					} else {
						var allMoves = findConfigOar(wantedMinusThis, sailorsMinusThis, entitiesMinusThis, actsPlusThis, gameShip);
						//todo les gens sauf ceux dans allMoves
						if (isOarConfigReached(wantedMinusThis, allMoves, sailorsMinusThis.stream().filter(s -> !allMoves.stream().map(m -> m.getSailor()).collect(Collectors.toList()).contains(s.getSailor())).map(ComputeMoveSailor::getSailor).collect(Collectors.toSet()), gameShip)) {
							return allMoves;
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error in finding configuration of oar (moving) : " + e.getMessage());
				throw e;
			}
		}
		return null;
	}

	//https://stackoverflow.com/questions/29656649/split-a-list-into-two-sublists-in-all-possible-ways
	private ArrayList<Pair<ArrayList<ComputeMoveSailor>, ArrayList<ComputeMoveSailor>>> allSubLists(ArrayList<ComputeMoveSailor> list) {
		try {

			var ret = new ArrayList<Pair<ArrayList<ComputeMoveSailor>, ArrayList<ComputeMoveSailor>>>();
			int size = list.size();
			boolean[] flags = new boolean[size];
			for (int i = 0; i != size; ) {
				ArrayList<ComputeMoveSailor> a = new ArrayList<>(), b = new ArrayList<>();
				for (int j = 0; j < size; j++)
					if (flags[j])
						a.add(list.get(j));
					else
						b.add(list.get(j));
				//System.out.println("" + a + ", " + b);
				for (i = 0; i < size && !(flags[i] = !flags[i]); i++) ;
				ret.add(Pair.of(a, b));
			}
			return ret;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error making sublists" + e.getMessage());
			throw e;
		}
	}

	private boolean isAbsConfigurationReached(Set<PosOnShip> wantedAbsConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedAbsConfig == null || wantedAbsConfig.isEmpty())
			return true;

		var obj = new HashSet<PosOnShip>();
		for (MoveAction g : act) {
			var pos = new PosOnShip(g.newPosCoord());

			try {
				obj.add(pos);
			} catch (Exception e) {
				//Cockpit.log("Error checking if configuration (not oars) reached : " + e.getMessage());
				logger.log(Level.SEVERE, "Error checking if configuration (not oars) reached : " + e.getMessage());
				return false;
			}
			if (obj.containsAll(wantedAbsConfig))
				return true;
		}
		return false;
	}

	private boolean isAbsConfigurationReached(Set<? extends OnboardEntity> wantedAbsConfig, ArrayList<MoveAction> act, ArrayList<Marin> unmoved, Bateau gameShip) {
		if (wantedAbsConfig == null || wantedAbsConfig.isEmpty())
			return true;

		var wanted = wantedAbsConfig.stream().map(OnboardEntity::getPos).collect(Collectors.toSet());
		var obj = new HashSet<PosOnShip>();
		if (act != null)
			for (MoveAction g : act) {
				var pos = g.newPos();
				obj.add(pos);
				if (obj.containsAll(wanted))
					return true;
			}
		if (unmoved != null)
			for (var m : unmoved) {
				var pos = m.getPos();
				obj.add(pos);
				if (obj.containsAll(wanted))
					return true;
			}

		return false;

	}

	private boolean isOarConfigurationReached(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, Bateau gameShip) {
		if (wantedOarConfig == null) {
			return true;
		}
		var obj = Pair.of(0, 0);
		if (act != null)
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
						if (oar.isLeft()) {
							obj = Pair.of(obj.first + 1, obj.second);
						} else {
							obj = Pair.of(obj.first, obj.second + 1);
						}
					}
				} catch (Exception e) {
					//Cockpit.log("Error checking if oar configuration reached : " + e.getMessage());
					logger.log(Level.SEVERE, "Error checking if oar configuration reached : " + e.getMessage());
					return false;
				}
				if (obj.first >= wantedOarConfig.first && obj.second >= wantedOarConfig.second) {
					return true;
				}
			}


		return false;
	}

	private boolean isOarConfigReached(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, Collection<Marin> unmovedSailors, Bateau gameShip) {
		if (wantedOarConfig == null)
			return true;
		try {
			HashSet<PosOnShip> poss = new HashSet<>();
			if (act != null && !act.isEmpty())
				poss.addAll(act.stream().map(MoveAction::newPos).collect(Collectors.toSet()));
			if (unmovedSailors != null && !unmovedSailors.isEmpty())
				poss.addAll(unmovedSailors.stream().map(Marin::getPos).collect(Collectors.toSet()));
			HashSet<Rame> oars = new HashSet<Rame>(poss.stream().filter(p -> {
				var ent = gameShip.getEntityHere(p);
				if (ent.isEmpty())
					return false;
				var entity = ent.get();
				return entity instanceof Rame;
			}).map(e -> (Rame) (gameShip.getEntityHere(e).get())).collect(Collectors.toSet()));
			var left = oars.stream().filter(r -> r.isLeft()).collect(Collectors.toSet()).size();
			var right = oars.stream().filter(r -> !r.isLeft()).collect(Collectors.toSet()).size();
			return wantedOarConfig.first <= left && wantedOarConfig.second <= right;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error checking if oar configuration reached" + e.getMessage());
		}
		return false;
	}

	private ArrayList<OarAction> whoShouldRow(Pair<Integer, Integer> wantedOarConfig, ArrayList<MoveAction> act, ArrayList<Marin> unmovedSailors, Bateau gameShip) {
		var oaring = new ArrayList<OarAction>();
		var obj = Pair.of(0, 0);
		HashMap<Marin, PosOnShip> sailorAndDistance = new HashMap<>();
		if (act != null && !act.isEmpty()) {
			sailorAndDistance.putAll(act.stream().map(a -> Map.entry(a.getSailor(), a.newPos())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		}
		if (unmovedSailors != null && !unmovedSailors.isEmpty()) {
			sailorAndDistance.putAll(unmovedSailors.stream().map(s -> Map.entry(s, s.getPos())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		}
		for (Map.Entry<Marin, PosOnShip> pair : sailorAndDistance.entrySet()) {
			try {
				var isEntity = gameShip.getEntityHere(pair.getValue());
				if (isEntity.isEmpty())
					continue;
				var ent = isEntity.get();
				if (!(ent instanceof Rame))
					continue;
				Rame oar = (Rame) ent;
				if (oar.isLeft()) {
					if (obj.first.equals(wantedOarConfig.first))
						continue;
					obj = Pair.of(obj.first + 1, obj.second);
					oaring.add(new OarAction(pair.getKey()));
				} else {
					if (obj.second.equals(wantedOarConfig.second))
						continue;
					obj = Pair.of(obj.first, obj.second + 1);
					oaring.add(new OarAction(pair.getKey()));
				}
				if (obj.equals(wantedOarConfig)) {
					return oaring;
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error determining who should row : " + e.getMessage());
				return null;
			}
		}
		logger.log(Level.SEVERE, "Could not establish who should row");
		return new ArrayList<>();
	}

	//TODO PAS BEAU CA
	@Override
	public boolean isValidated(GameState state) {
		return false;
	}

}
