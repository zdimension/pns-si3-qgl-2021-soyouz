package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to do some math/trigonometric stuff. Ewww.
 */
public class Trigonometry {

    /**
     * Determine the orientation and the linear speed of the boat in a specific configuration.
     *
     * @param activeOarNb The number of active oar.
     * @param totalOarNb The total number of oar.
     * @return a pair that contains all the information.
     */
    static Double oarLinearSpeed(int activeOarNb, int totalOarNb){
        return (165.0 * activeOarNb / totalOarNb);
    }

    /**
     * Determine the angle of rotation of a specific combination of oar.
     *
     * @param activeLeftOar The number of active oar on the left side of the boat.
     * @param activeRightOar The number of active oar on the right side of the boat.
     * @param totalOarNb The total number of oar.
     * @return the angle of the rotation.
     */
    static Double rotatingAngle(int activeLeftOar, int activeRightOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }

    //TODO A REFACTO DANS UNE CLASSE
    /**
     * Determine every angle of rotation possible according to the game parameters.
     * Then they are stocked into two maps, one for each side (right/left).
     *
     * @param nbSailor The number of sailor on the boat.
     * @param nbOarOnSide The number of oar on each side of the boat.
     */
    static private void setTurnPossibilities(int nbSailor, int nbOarOnSide, Pair<HashMap<Pair<Integer, Integer>, Double>, HashMap<Pair<Integer, Integer>, Double>> turnPossibilities) {
        for (int i = 0; i <= nbOarOnSide && i < nbSailor; i++) {
            for (int j = 0; j <= nbOarOnSide && j < nbSailor; j++) {
                    if (i > j && i + j < nbSailor)
                        turnPossibilities.first.put(Pair.of(j, i), rotatingAngle(j, i, nbOarOnSide * 2));
                    if (i < j && i + j < nbSailor)
                        turnPossibilities.second.put(Pair.of(j, i), rotatingAngle(j, i, nbOarOnSide * 2));
            }
        }
    }

    public static Pair<Pair<Integer, Integer>, Double> findOptTurnConfig(int nbSailor, int nbOarOnSide, Pair<Double, Double> opt, Pair<HashMap<Pair<Integer, Integer>, Double>, HashMap<Pair<Integer, Integer>, Double>> turnPossibilities) {
        Pair<Integer, Integer> neededOarRotation;
        double neededRudderRotation;
        if (rudderRotationIsInRange(opt.second)) {
            neededOarRotation = findOptConfigBasedOnVl(opt, nbOarOnSide * 2, nbSailor);
        } else {
            neededOarRotation = findOptConfigBasedOnVr(nbSailor, nbOarOnSide, opt, turnPossibilities);
        }
        neededRudderRotation = findOptRudderRotation(opt.second - rotatingAngle(neededOarRotation.first, neededOarRotation.second, nbOarOnSide * 2));
        return Pair.of(neededOarRotation, neededRudderRotation);
    }

    private static Pair<Integer, Integer> findOptConfigBasedOnVr(int nbSailor, int nbOarOnSide, Pair<Double, Double> opt, Pair<HashMap<Pair<Integer, Integer>, Double>, HashMap<Pair<Integer, Integer>, Double>> turnPossibilities) {
        setTurnPossibilities(nbSailor, nbOarOnSide, turnPossibilities);
        var givenSide = opt.second > 0 ? turnPossibilities.first : turnPossibilities.second;
        Pair<Integer, Integer> optimal = null;
        double diff = 0.0;
        for (Map.Entry<Pair<Integer, Integer>, Double> entry : givenSide.entrySet()) {
            double difference = Math.abs(opt.second - entry.getValue());
            if (diff == 0 || (difference <= diff && (entry.getKey().first + entry.getKey().second) > (optimal.first + optimal.second))) {
                optimal = entry.getKey();
                diff = difference;
            }
        }
        return optimal;
    }

    private static Pair<Integer, Integer> findOptConfigBasedOnVl(Pair<Double, Double> opt, int nbTotalOar, int nbSailor) {
        Pair<Integer, Integer> optimal = Pair.of(1, 1);
        double diff = 0.0;
        for (int i = 2; i <= nbSailor; i+=2) {
            double difference = opt.first - oarLinearSpeed(i, nbTotalOar);
             if (diff == 0 || difference <= diff && difference > 0) {
                optimal = Pair.of(i / 2, i / 2);
                diff = difference;
            }
        }
        if (diff < 0) {
            if (opt.second < 0)
                optimal = Pair.of(1, 0);
            else
                optimal = Pair.of(0,1);
        }
        return optimal;
    }

    public static boolean rudderRotationIsInRange (Double neededRotation){
        return neededRotation > Gouvernail.ALLOWED_ROTATION.first && neededRotation < Gouvernail.ALLOWED_ROTATION.second;
    }

    public static Double findOptRudderRotation (Double neededRotation) {
        if (rudderRotationIsInRange(neededRotation)){
            return neededRotation;
        }else{
            if (neededRotation<0){
                return Gouvernail.ALLOWED_ROTATION.first;
            }else{
                return Gouvernail.ALLOWED_ROTATION.second;
            }
        }
    }

    public static double calculateAngle(Bateau boat, Checkpoint checkpoint) {
        double boatOrientation = boat.getPosition().getOrientation();
        var boatVect = Pair.of(Math.cos(boatOrientation), Math.sin(boatOrientation));
        var cpVect = Pair.of(checkpoint.getPosition().getX() - boat.getPosition().getX(), checkpoint.getPosition().getY() - boat.getPosition().getY());
        var normeDirection = Math.sqrt(Math.pow(cpVect.first, 2) + Math.pow(cpVect.second, 2));
        var scalaire = boatVect.first * cpVect.first + boatVect.second * cpVect.second;

        var angle = Math.acos(scalaire / normeDirection);
        if (cpVect.second - boatVect.second < 0)
            angle = -angle;
        return angle;
    }
}