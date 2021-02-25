package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.PosOnShip;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class storing intended action for the next round.
 */
public class TempRoundChoice {
	final HashMap<Marin, Pair<PosOnShip, Pair<GameAction, @Nullable GameAction>>> sailorsJob;
	final HashMap<Marin, Pair<PosOnShip, MoveAction>> movedSailors;
	final Collection<Marin> vacantSailors;
	final HashMap<OnboardEntity, Boolean> usedOnBoardEntity;

	/**
	 * Constructor.
	 *
	 * @param entity  All entities on board.
	 * @param sailors All sailors on board.
	 */
	public TempRoundChoice(Collection<OnboardEntity> entity, Collection<Marin> sailors) {
		sailorsJob = new HashMap<>();
		usedOnBoardEntity = new HashMap<>();
		movedSailors = new HashMap<>();
		vacantSailors = sailors;
		for (var ent : entity) {
			usedOnBoardEntity.put(ent, false);
		}
	}

	/**
	 * Getters;
	 *
	 * @return all sailors jobs.
	 */
	public Map<Marin, Pair<PosOnShip, Pair<GameAction, @Nullable GameAction>>> getSailorsJob() {
		return Collections.unmodifiableMap(sailorsJob);
	}

	/**
	 * Getters.
	 *
	 * @return all on board entities that are used.
	 */
	public Map<OnboardEntity, Boolean> getUsedOnBoardEntity() {
		return Collections.unmodifiableMap(usedOnBoardEntity);
	}

	/**
	 * Getters.
	 *
	 * @return all vacant sailors.
	 */
	public Collection<Marin> getUnmovedVacantSailors() {
		return Collections.unmodifiableCollection(vacantSailors);
	}

	public Collection<Marin> getMovedVacantSailors() {
		return Collections.unmodifiableCollection(movedSailors.keySet());
	}

	public Collection<Marin> getBusySailors() {
		return Collections.unmodifiableCollection(sailorsJob.keySet());
	}

	public Set<GameAction> getAllActions() {
		return new HashSet<>() {{
			addAll(sailorsJob.values().stream().
					map(Pair::getSecond).
					flatMap(p -> Stream.of(p.getFirst(), p.getSecond())).
					filter(Objects::nonNull).
					collect(Collectors.toSet()));
			addAll(movedSailors.values().stream().
					map(Pair::getSecond).
					collect(Collectors.toSet())
			);
		}};
	}

	public Set<MoveAction> getAllMoves() {
		return getAllActions().stream().filter(a -> a instanceof MoveAction).map(a -> (MoveAction) a).collect(Collectors.toSet());
	}

	//todo check if sailor can perform action HERE

	/**
	 * Making a sailor perform an action and updating onBoardEntity usage.
	 *
	 * @param sailor to perform action.
	 * @param pos    new position of the sailor.
	 * @param action for sailor to perform (anyAction, null) (moveAction, null) or (moveAction, AnyAction).
	 * @throws IllegalArgumentException when illegal arguments.
	 */
	public void hireSailor(Marin sailor, Pair<Integer, Integer> pos, Pair<GameAction, @Nullable GameAction> action) {
		if (sailorsJob.containsKey(sailor))
			throw new IllegalArgumentException("Sailor not vacant");

		OnboardEntity entity = null;
		if (action.getSecond() != null) {
			var optEntity = usedOnBoardEntity.keySet().stream().filter(e -> e.getPos() == pos).findAny();
			if (optEntity.isEmpty())
				throw new IllegalArgumentException("Entity no found");

			entity = optEntity.get();
			if (usedOnBoardEntity.get(entity))
				throw new IllegalArgumentException("Entity already used");

			if (action.getSecond().getEntityNeeded().isInstance(entity))
				throw new IllegalArgumentException("Entity not compatible with action");
		}

		if (sailorsJob.containsKey(sailor))
			throw new IllegalArgumentException("Sailor not found");

		if (vacantSailors.contains(sailor)) {

			if (!sailor.getPos().equals(pos)) {
				var isActionMove = action != null && action.first instanceof MoveAction;

				if (!isActionMove)
					throw new IllegalArgumentException("Incoherent sailor position (position changed but no moveAction)");

				var actionMove = (MoveAction) action.first;

				if (!actionMove.newPos().equals(pos))
					throw new IllegalArgumentException("Incoherent sailor position (position not coherent with moveAction)");
			}
			vacantSailors.remove(sailor);
			if (entity != null) {
				usedOnBoardEntity.replace(entity, true);
				sailorsJob.put(sailor, Pair.of(new PosOnShip(pos.getFirst(), pos.getSecond()), action));
			} else {
				movedSailors.put(sailor, Pair.of(new PosOnShip(pos.getFirst(), pos.getSecond()), (MoveAction) action.getFirst()));
			}
		} else if (movedSailors.containsKey(sailor)) {
			if (action.getSecond() != null)
				throw new IllegalArgumentException("Sailor cannot perform 2 extra actions");
			if (action.getFirst() instanceof MoveAction)
				throw new IllegalArgumentException("Sailor already moved");

			var oldSailor = movedSailors.remove(sailor);
			sailorsJob.put(sailor, Pair.of(new PosOnShip(pos.getFirst(), pos.getSecond()), Pair.of(oldSailor.getSecond(), action.getFirst())));
			usedOnBoardEntity.replace(entity, true);
		} else {
			throw new IllegalArgumentException("Sailor not found");
		}
	}

	/**
	 * Making a sailor perform an action and updating onBoardEntity usage
	 *
	 * @param sailor to perform action
	 * @param pos    new position of the sailor
	 * @param act1   for sailor to perform
	 * @param act2   for sailor to perform (only if the first action is MoveAction
	 * @throws IllegalArgumentException when illegal arguments
	 */
	public void hireSailor(Marin sailor, Pair<Integer, Integer> pos, GameAction act1, @Nullable GameAction act2) {
		hireSailor(sailor, pos, Pair.of(act1, act2));
	}

	public void hireSailor(Marin sailor, PosOnShip pos, GameAction act1, @Nullable GameAction act2) {
		hireSailor(sailor, pos.getPosCoord(), Pair.of(act1, act2));
	}

	public void hireSailor(Marin sailor, GameAction act1) {
		if (act1 instanceof MoveAction) {
			hireSailor(sailor, (MoveAction) act1);
			return;
		}
		if (vacantSailors.contains(sailor)) {
			hireSailor(sailor, sailor.getPos(), Pair.of(act1, null));
		} else if (movedSailors.containsKey(sailor)) {
			var mov = movedSailors.get(sailor).first;
			hireSailor(sailor, mov.getPosCoord(), Pair.of(act1, null));
		} else
			throw new IllegalArgumentException("Cannot hire sailor");
	}

	public void moveSailor(MoveAction move) {
		hireSailor(move.getSailor(), Pair.of(move.newPos().first, move.newPos().getSecond()), Pair.of(move, null));
	}

	public Marin findFirstVacantSailorHere(Pair<Integer, Integer> pos) {
		var unmoved = vacantSailors.stream().filter(s -> s.getPos().equals(pos)).findFirst();
		if (unmoved.isPresent()) {
			return unmoved.get();
		}
		var moved = movedSailors.entrySet().stream().filter(s -> s.getValue().first.getPosCoord().equals(pos)).findFirst();
		return moved.map(Map.Entry::getKey).orElse(null);
	}
}
