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

  static int i = 0;

  private InitGameParameters ip;
  private NextRoundParameters np;
  private RootObjective objective;
  private int numCheckpoint = 0;

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
    i++;
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

      var xb = np.getShip().getPosition().getX();
      var yb = np.getShip().getPosition().getY();
      //TODO : c'est moche, mais sa regarde si le centre du bateau est dans le checkpoint, bon c'est pas garanti que ça marche pour la week 4...
      if (np.getShip().getPosition().getLength(((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint].getPosition())
              < (((Circle) ((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint].getShape())).getRadius()) {
        if (numCheckpoint < ((RegattaGoal) ip.getGoal()).getCheckpoints().length - 1) {
          numCheckpoint++;
        }
      }

      var nextCp = ((RegattaGoal) ip.getGoal()).getCheckpoints()[numCheckpoint];
      var xo = nextCp.getPosition().getX();
      var yo = nextCp.getPosition().getY();
      var da = Math.atan2(yo - yb, xo - xb);
      var vl = da == 0 ? xo - xb : da * (Math.pow(xo - xb, 2) + Math.pow(yo - yb, 2)) / (yo - yb);
      var vr = 2 * da;
      //TODO Ignoble d'appeller 2 fois la meme instance du bateau
      var vrr = Trigonometry.neededRotation(np.getShip(), ip.getShip(), nextCp.getPosition());
      Pair<Double, Double> opt = Pair.of(vl, -vrr);
      var acts = new ArrayList<GameAction>();
      //-----------------------------------
      //computing how to move sailors
      var sailors = ip.getSailors();
      var oarReachableForSailors = new HashMap<Marin, Set<Rame>>();
      var allOars = new HashSet<Rame>();
      var sailorsNotMoving = new ArrayList<MoveAction>();
      for (Marin m : ip.getSailors()) {
        oarReachableForSailors.put(m, new HashSet<>());
        sailorsNotMoving.add(new MoveAction(m, 0, 0));
      }
      var wantedOarConfig = Trigonometry.findOptOarConfig(sailors.length, ip.getShip().getNumberOar() / 2, opt);

      //compute all reachable oars
      for (OnboardEntity ent : ip.getShip().getEntities()) {
        if (!(ent instanceof Rame))
          continue;
        var r = (Rame) ent;
        allOars.add(r);
        for (Marin m : ip.getSailors()) {
          if (m.isAbsPosReachable(r.getPos()))
            oarReachableForSailors.get(m).add(r);
        }
      }

      var actsMoves = new ArrayList<MoveAction>();

      if (!isOarConfigurationReached(wantedOarConfig, sailorsNotMoving)) {
        actsMoves = firstSailorConfig(wantedOarConfig, oarReachableForSailors, allOars, actsMoves);
      }
      var unmovedSailors = new ArrayList<Marin>(Arrays.asList(ip.getSailors()));
      for (MoveAction m : actsMoves) {
        unmovedSailors.remove(m);
      }

      //when no moves are found, random
      if (actsMoves == null) {
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
      } else {
        var oaring = whoShouldOar(wantedOarConfig, actsMoves, unmovedSailors);
        if (oaring == null) {
          return "[]";
        }
        var actions = new ArrayList<GameAction>();
        actions.addAll(actsMoves);
        actions.addAll(oaring);
        //update sailors
        for (MoveAction m : actsMoves) {
          m.getSailor().moveRelative(m.getXDistance(), m.getYDistance());
        }
        return OBJECT_MAPPER.writeValueAsString(actions.toArray(GameAction[]::new));
      }
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

  private ArrayList<MoveAction> firstSailorConfig(Pair<Integer, Integer> wantedConfig, HashMap<Marin, Set<Rame>> possibleSailorConfig, Set<Rame> currentOars, ArrayList<MoveAction> act) {
    var marins = possibleSailorConfig.keySet();
    if (marins.isEmpty())
      return act;

    for (Map.Entry<Marin, Set<Rame>> pair : possibleSailorConfig.entrySet()) {
      var marin = pair.getKey();
      for(var rame : pair.getValue()){
        if(!currentOars.contains(rame))
          continue;
        var sailorsMinusThis = new HashMap<>(possibleSailorConfig);
        sailorsMinusThis.remove(marin);
        var oarsMinusThis = new HashSet<Rame>(currentOars);
        oarsMinusThis.remove(rame);
        var actPlusThis = new ArrayList<>(act);
        actPlusThis.add(new MoveAction(marin, rame.getX() - marin.getX(), rame.getY() - marin.getY()));
        var allMoves = firstSailorConfig(wantedConfig, sailorsMinusThis, oarsMinusThis, actPlusThis);
        if (allMoves != null) {
          if (isOarConfigurationReached(wantedConfig, allMoves)) {
            return allMoves;
          }
        }
      }
    }


    return null;
  }

  private boolean isOarConfigurationReached(Pair<Integer, Integer> wantedConfig, ArrayList<MoveAction> act) {
    var obj = Pair.of(0, 0);
    for (MoveAction g : act) {
      var entity = Pair.of(g.getSailor().getX() + g.getXDistance(), g.getSailor().getY() + g.getYDistance());
      Rame oar;
      try {
        var entHere = getIp().getShip().getEntityHere(entity);
        if (entHere.isEmpty()) {
          //no entity here
          continue;
        }
        if (entHere.get() instanceof Rame) {
          oar = (Rame) entHere.get();
          if (getIp().getShip().isOarLeft(oar)) {
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

  private ArrayList<OarAction> whoShouldOar(Pair<Integer, Integer> wantedConfig, ArrayList<MoveAction> act, ArrayList<Marin> unmovedSailors) {
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
        var entHere = getIp().getShip().getEntityHere(pos);
        if (entHere.isEmpty()) {
          //no entity here
          continue;
        }
        if (entHere.get() instanceof Rame) {
          oar = (Rame) entHere.get();
          if (getIp().getShip().isOarLeft(oar)) {
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
}