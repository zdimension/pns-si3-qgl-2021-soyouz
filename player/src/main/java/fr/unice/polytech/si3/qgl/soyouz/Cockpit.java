package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.OarAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Trigonometry;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/** Control panel of the whole game. Here happens all the magic. */
public class Cockpit implements ICockpit {
  private static final Queue<String> logList = new ConcurrentLinkedQueue<>();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private InitGameParameters ip;
  private NextRoundParameters np;
  private RootObjective objective;
  private int numCheckpoint =0;

  /**
   * Print the logs on the console and put them to the log file.
   *
   * @param message The logs
   */
  public static void log(String message) {
    System.out.println(message);
    logList.add(message);
  }

  /**
   * Parse all the initial Game Parameters into a InitGameParameters object.
   *
   * @param game The Json to init the game.
   */
  @Override
  public void initGame(String game) {
    try {
      ip = OBJECT_MAPPER.readValue(game, InitGameParameters.class);
      objective = ip.getGoal().getObjective();
      log("Init game input: " + ip);
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }

  /**
   * Parse all the current Game Parameters into a NextRoundParameters object. Determine which
   * actions to do in order to win and create a matching Json.
   *
   * @param round The Json of the current state of the Game.
   * @return the corresponding Json.
   */
  @Override
  public String nextRound(String round) {
    try {
      np = OBJECT_MAPPER.readValue(round, NextRoundParameters.class);
      log("Next round input: " + np);
      objective.update(new GameState(ip, np));

      // TODO Check if it work
      List<Marin> sailorWithoutOar =
          Arrays.stream(ip.getSailors())
              .filter(m -> ip.getShip().getEntityHere(m.getX(), m.getY()).equals(null))
              .collect(Collectors.toList());
      // TODO Stream to put every sailor on an Oar

      /*return OBJECT_MAPPER.writeValueAsString(Arrays.stream(ip.getSailors()).filter(
          m -> ip.getShip().getEntityHere(m.getX(), m.getY()).orElse(null) instanceof Rame
      ).map(OarAction::new).toArray(OarAction[]::new));*/

      var xb = ip.getShip().getPosition().getX();
      var yb = ip.getShip().getPosition().getY();
      //TODO : c'est moche, mais sa regarde si le centre du bateau est dans le checkpoint, bon c'est pas garanti que ça marche pour la week 4...
      if (ip.getShip().getPosition().getLength(((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint].getPosition())
              < (((Circle)((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint].getShape())).getRadius()){
        if(numCheckpoint<((RegattaGoal) ip.getGoal()).getCheckpoints().length-1){
          numCheckpoint++;
        }
      }

      var nextCp = ((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint];
      var xo = nextCp.getPosition().getX();
      var yo = nextCp.getPosition().getY();
      var da = Math.atan2(yo - yb, xo - xb);
      var vl = da == 0 ? xo - xb : da * (Math.pow(xo - xb, 2) + Math.pow(yo - yb, 2)) / (yo - yb);
      var vr = 2 * da;
      Pair<Double, Double> opt = Pair.of(vl, vr);
      var acts = new ArrayList<GameAction>();
      //-----------------------------------
      //computing how to move sailors
      var sailors = ip.getSailors();
      var oarReachableForSailors = new HashMap<Marin, Set<Rame>>();
      var allOars = new HashSet<Rame>();
      for(Marin m : ip.getSailors()){
        oarReachableForSailors.put(m, new HashSet<>());
      }
      var wantedOarConfig = Trigonometry.findOptOarConfig(sailors.length, ip.getShip().getNumberOar(),opt);

      //calculer toutes les rames atteignables par tous les marins
      for(OnboardEntity ent : ip.getShip().getEntities()){
        if(!(ent instanceof Rame))
          continue;
        var r = (Rame) ent;
        allOars.add(r);
        for(Marin m : ip.getSailors()){
          if(m.isAbsPosReachable(r.getPos()))
            oarReachableForSailors.get(m).add(r);
        }
      }
      //essayer d'atteindre la configuration de maniere itérative
      var actsMoves = new ArrayList<MoveAction>();
      if(!isConfigurationReached(wantedOarConfig, actsMoves)){
        actsMoves = firstSailorConfig(wantedOarConfig, oarReachableForSailors, allOars, actsMoves);
      }
      //when no moves are found, random
      if(actsMoves == null){
        //rien de possible en un tour seulement
        for (Marin m : ip.getSailors()) {
          if (!ip.getShip().hasAt(m.getX(), m.getY(), Rame.class)) {
            var rame =
                Arrays.stream(ip.getShip().getEntities())
                    .filter(
                        e ->
                            e instanceof Rame
                                && !(Arrays.stream(ip.getSailors())
                                    .anyMatch(n -> n.getX() == e.getX() && n.getY() == e.getY())))
                    .findFirst()
                    .get();
            acts.add(new MoveAction(m, rame.getX() - m.getX(), rame.getY() - m.getY()));
            m.setX(rame.getX());
            m.setY(rame.getY());
          }
        }
        return OBJECT_MAPPER.writeValueAsString(acts.toArray(GameAction[]::new));
      }
      else{
        var oaring = whoShouldOar(wantedOarConfig, actsMoves);
        if(oaring == null){
          return "";
        }
        var actions = new ArrayList<GameAction>();
        actions.addAll(actsMoves);
        actions.addAll(oaring);
        //update sailors
        for(MoveAction m : actsMoves){
          m.getSailor().moveRelative(m.getXDistance(), m.getYDistance());
        }
        return OBJECT_MAPPER.writeValueAsString(actions.toArray(GameAction[]::new));
      }

      //-----------------------------------
      //for (Marin m : ip.getSailors()) {
      //  if (!ip.getShip().hasAt(m.getX(), m.getY(), Rame.class)) {
      //    var rame =
      //        Arrays.stream(ip.getShip().getEntities())
      //            .filter(
      //                e ->
      //                    e instanceof Rame
      //                        && !(Arrays.stream(ip.getSailors())
      //                            .anyMatch(n -> n.getX() == e.getX() && n.getY() == e.getY())))
      //            .findFirst()
      //            .get();
      //    acts.add(new MoveAction(m, rame.getX() - m.getX(), rame.getY() - m.getY()));
      //    m.setX(rame.getX());
      //    m.setY(rame.getY());
      //  }
      //}


      /*return OBJECT_MAPPER.writeValueAsString(
      Arrays.stream(ip.getSailors())
          .filter(
              m -> ip.getShip().getEntityHere(m.getX(), m.getY()).orElse(null) instanceof Rame)
          .map(OarAction::new)
          .toArray(OarAction[]::new));*/

      // return OBJECT_MAPPER.writeValueAsString(objective.resolve(new GameState(ip,
      // np)).toArray());
    } catch (Exception e) {
      //e.printStackTrace();
      return "[]";
    }
  }
  /**
   * Getters.
   *
   * @return a list of log.
   */
  @Override
  public List<String> getLogs() {
    return new ArrayList<>(logList);
  }
  /**
   * Getters.
   *
   * @return the Init Game Parameters.
   */
  public InitGameParameters getIp() {
    return ip;
  }

  /**
   * Getters.
   *
   * @return the Next Round Parameters.
   */
  public NextRoundParameters getNp() {
    return np;
  }

  private ArrayList<MoveAction> firstSailorConfig(Pair<Integer,Integer> wantedConfig, HashMap<Marin, Set<Rame>> possibleSailorConfig, Set<Rame> currentOars, ArrayList<MoveAction> act){
    var marins = possibleSailorConfig.keySet();
    if(marins.isEmpty())
      return act;
    for(Rame r : currentOars){
      for(Marin m : marins){
        var sailorsMinusThis = new HashMap<>(possibleSailorConfig);
        sailorsMinusThis.remove(m);
        var oarsMinusThis = new HashSet<Rame>(currentOars);
        oarsMinusThis.remove(r);
        var actPlusThis = new ArrayList<>(act);
        actPlusThis.add(new MoveAction(m, r.getX() - m.getX(), r.getY() - m.getY()));
        var allMoves = firstSailorConfig(wantedConfig,sailorsMinusThis,oarsMinusThis,actPlusThis);
        if(allMoves != null){
          if(isConfigurationReached(wantedConfig, allMoves)){
            return allMoves;
          }
        }
      }
    }
    return null;
  }

  private boolean isConfigurationReached(Pair<Integer,Integer> wantedConfig, ArrayList<MoveAction> act){
    var obj = Pair.of(0,0);
    for(MoveAction g : act){
      var entity = Pair.of(g.getSailor().getX() + g.getXDistance(), g.getSailor().getY()+g.getYDistance());
      Rame oar;
      try{
        if(getIp().getShip().getEntityHere(entity).get() instanceof Rame){
          oar = (Rame) getIp().getShip().getEntityHere(entity).get();
          if(getIp().getShip().isOarLeft(oar)){
            obj = Pair.of(obj.first+1, obj.second);
          }
          else{
            obj = Pair.of(obj.first, obj.second+1);
          }
        }
      }
      catch(Exception e){
        return false;
      }
      if(obj.first>=wantedConfig.first && obj.second >= wantedConfig.second){
        return true;
      }
    }
    return false;
  }

  private ArrayList<OarAction> whoShouldOar(Pair<Integer,Integer> wantedConfig, ArrayList<MoveAction> act){
    var oaring = new ArrayList<OarAction>();
    var obj = Pair.of(0,0);
    for(MoveAction g : act){
      var entity = Pair.of(g.getSailor().getX() + g.getXDistance(), g.getSailor().getY()+g.getYDistance());
      Rame oar;
      try{
        if(getIp().getShip().getEntityHere(entity).get() instanceof Rame){
          oar = (Rame) getIp().getShip().getEntityHere(entity).get();
          if(getIp().getShip().isOarLeft(oar)){
            if(obj.first.equals(wantedConfig.first)){
              continue;
            }
            else{
              obj = Pair.of(obj.first+1, obj.second);
              oaring.add(new OarAction(g.getSailor()));
            }
          }
          else{
            if(obj.second.equals(wantedConfig.second)){
              continue;
            }
            else{
              obj = Pair.of(obj.first, obj.second+1);
              oaring.add(new OarAction(g.getSailor()));
            }
          }
        }
      }
      catch(Exception e){
        return null;
      }
      if(obj.equals(wantedConfig)){
        return oaring;
      }
    }
    return null;

  }
}


