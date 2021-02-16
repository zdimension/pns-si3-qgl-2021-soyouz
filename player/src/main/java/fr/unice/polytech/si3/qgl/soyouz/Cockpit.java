package fr.unice.polytech.si3.qgl.soyouz;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.si3.qgl.regatta.cockpit.ICockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.GameAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.actions.MoveAction;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.GameState;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RegattaObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.RootObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
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
      e.printStackTrace();
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
      // TODO X.isUsed THROW NULLPOINTEREXCEPTION
      Arrays.stream(ip.getShip().getEntities())
          .filter(x -> x.isUsed())
          .forEach(
              x -> x.setUsed(false)); // At each start of round, the entities are no longer used
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
      /*return OBJECT_MAPPER.writeValueAsString(
      Arrays.stream(ip.getSailors())
          .filter(
              m -> ip.getShip().getEntityHere(m.getX(), m.getY()).orElse(null) instanceof Rame)
          .map(OarAction::new)
          .toArray(OarAction[]::new));*/

      // return OBJECT_MAPPER.writeValueAsString(objective.resolve(new GameState(ip,
      // np)).toArray());
    } catch (Exception e) {
      e.printStackTrace();
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
}
