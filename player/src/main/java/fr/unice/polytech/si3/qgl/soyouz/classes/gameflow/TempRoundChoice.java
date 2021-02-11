package fr.unice.polytech.si3.qgl.soyouz.classes.gameflow;

import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TempRoundChoice {
    final HashMap<Marin, Pair<Pair<Integer,Integer>, Pair<GameAction, @Nullable GameAction>>> sailorsJob;
    final HashMap<OnboardEntity, Boolean> usedOnBoardEntity;
    final Collection<Marin> vacantSailors;

    public TempRoundChoice(Collection<OnboardEntity> entity, Collection<Marin> sailors){
        sailorsJob = new HashMap<>();
        usedOnBoardEntity = new HashMap<>();
        vacantSailors = sailors;
        for(var ent : entity){
            usedOnBoardEntity.put(ent, false);
        }
    }

    public Map<Marin, Pair<Pair<Integer, Integer>, Pair<GameAction, @Nullable GameAction>>> getSailorsJob() {
        return Collections.unmodifiableMap(sailorsJob);
    }

    public Map<OnboardEntity, Boolean> getUsedOnBoardEntity() {
        return Collections.unmodifiableMap(usedOnBoardEntity);
    }

    public Collection<Marin> getVacantSailors() {
        return Collections.unmodifiableCollection(vacantSailors);
    }

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
            var isActionMove = MoveAction.class.isInstance(action.first);

            if(!isActionMove){
                throw new IllegalArgumentException("Incoherent sailor position (position changed but no moveAction)");
            }

            var actionMove = (MoveAction)action.first;

            if(!actionMove.newPos(sailor.getPos()).equals(pos)){
                throw new IllegalArgumentException("Incoherent sailor position (position not coherent with moveAction)");
            }
        }

        vacantSailors.remove(sailor);
        sailorsJob.put(sailor, Pair.of( pos, action));
        usedOnBoardEntity.replace(entity, true);
    }
    public void hireSailor(Marin sailor, Pair<Integer,Integer> pos, GameAction act1, @Nullable GameAction act2) throws IllegalArgumentException{
        hireSailor(sailor, pos, Pair.of(act1, act2));
    }


}