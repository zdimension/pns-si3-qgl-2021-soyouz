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
    final Collection<Marin> vacantSailors;
    final HashMap<OnboardEntity, Boolean> usedOnBoardEntity;

    /**
     * Constructor.
     *
     * @param entity All entities on board.
     * @param sailors All sailors on board.
     */
    public TempRoundChoice(Collection<OnboardEntity> entity, Collection<Marin> sailors){
        sailorsJob = new HashMap<>();
        usedOnBoardEntity = new HashMap<>();
        vacantSailors = sailors;
        for(var ent : entity){
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
    public Collection<Marin> getVacantSailors() {
        return Collections.unmodifiableCollection(vacantSailors);
    }

    public Collection<Marin> getBusySailors() { return Collections.unmodifiableCollection(sailorsJob.keySet());}

    public Set<GameAction> getAllActions() {
        return sailorsJob.values().stream().
                map(Pair::getSecond).
                flatMap(p -> Stream.of(p.getFirst(), p.getSecond())).
                filter(Objects::nonNull).
                collect(Collectors.toSet());
    }

    public Set<MoveAction> getAllMoves(){
        return getAllActions().stream().filter(a -> a instanceof MoveAction).map(a -> (MoveAction)a).collect(Collectors.toSet());
    }

    /**
     * Making a sailor perform an action and updating onBoardEntity usage.
     *
     * @param sailor to perform action.
     * @param pos new position of the sailor.
     * @param action for sailor to perform (anyAction, null) (moveAction, null) or (moveAction, AnyAction).
     * @throws IllegalArgumentException when illegal arguments.
     */
    public void hireSailor(Marin sailor, Pair<Integer,Integer> pos, Pair<GameAction, @Nullable GameAction> action) throws IllegalArgumentException{
        if(sailorsJob.containsKey(sailor))
            throw new IllegalArgumentException("Sailor not vacant");

        var optEntity = usedOnBoardEntity.keySet().stream().filter(e -> e.getPos() == pos).findAny();
        if(optEntity.isEmpty())
            throw new IllegalArgumentException("Entity no found");

        var entity = optEntity.get();
        if(usedOnBoardEntity.get(entity))
            throw new IllegalArgumentException("Entity already used");

        if(!vacantSailors.contains(sailor))
            throw new IllegalArgumentException("Sailor not found");


        if(!sailor.getPos().equals(pos)){
            var isActionMove = action != null && action.first instanceof MoveAction;

            if(!isActionMove){
                throw new IllegalArgumentException("Incoherent sailor position (position changed but no moveAction)");
            }

            var actionMove = (MoveAction)action.first;

            if(!actionMove.newPos().equals(pos)){
                throw new IllegalArgumentException("Incoherent sailor position (position not coherent with moveAction)");
            }
        }

        vacantSailors.remove(sailor);
        sailorsJob.put(sailor, Pair.of(new PosOnShip(pos.getFirst(), pos.getSecond()), action));
        usedOnBoardEntity.replace(entity, true);
    }

    /**
     * Making a sailor perform an action and updating onBoardEntity usage
     *
     * @param sailor to perform action
     * @param pos  new position of the sailor
     * @param act1 for sailor to perform
     * @param act2 for sailor to perform (only if the first action is MoveAction
     * @throws IllegalArgumentException when illegal arguments
     */
    public void hireSailor(Marin sailor, Pair<Integer,Integer> pos, GameAction act1, @Nullable GameAction act2) throws IllegalArgumentException{
        hireSailor(sailor, pos, Pair.of(act1, act2));
    }

    public void hireSailor(Marin sailor, PosOnShip pos, GameAction act1, @Nullable GameAction act2) throws IllegalArgumentException{
        hireSailor(sailor, pos.getPos(), Pair.of(act1, act2));
    }
}
