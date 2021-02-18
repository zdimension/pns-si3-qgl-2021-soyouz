package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Gouvernail;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class to do some math/trigonometric stuff. Ewww.
 */
public class Trigonometry {

    // TODO POSSIBILITE DE PASSER LA PAIR EN CLE POUR POUVOIR AVOIR DIVERSES VITESSES POSSIBLES AVEC DES ANGLES SIMILAIRES
    static HashMap<Pair<Integer, Integer>, Double> leftTurnPossibilities = new HashMap<>();
    static HashMap<Pair<Integer, Integer>, Double> rightTurnPossibilities = new HashMap<>();

    /**
     * Determine the orientation and the linear speed of the boat in a specific configuration.
     *
     * @param activeOarNb The number of active oar.
     * @param totalOarNb The total number of oar.
     * @return a pair that contains all the information.
     */
    //TODO maybe bad concept
    static Double oarLinearSpeed(int activeOarNb, int totalOarNb){
        return (double) (165 * activeOarNb / totalOarNb);
    }

    /**
     * Determine the angle of rotation of a specific combination of oar.
     *
     * @param activeLeftOar The number of active oar on the left side of the boat.
     * @param activeRightOar The number of active oar on the right side of the boat.
     * @param totalOarNb The total number of oar.
     * @return the angle of the rotation.
     */
    static Double rotatingSpeed(int activeLeftOar, int activeRightOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }

    /**
     * Determine every angle of rotation possible according to the game parameters.
     * Then they are stocked into two maps, one for each side (right/left).
     *
     * @param nbSailor The number of sailor on the boat.
     * @param nbOarOnSide The number of oar on each side of the boat.
     */
    //TODO OAR ONLY, does not take in count the future RUDDER
    static void setTurnPossibilities(int nbSailor, int nbOarOnSide) {
        for (int i = 0; i <= nbOarOnSide && i <= nbSailor; i++) {
            for (int j = 0; j <= nbOarOnSide && j <= nbSailor; j++) {
                    if (i > j && i + j <= nbSailor)
                        leftTurnPossibilities.put(Pair.of(j, i), rotatingSpeed(j, i, nbOarOnSide * 2));
                    if (i < j && i + j <= nbSailor)
                        rightTurnPossibilities.put(Pair.of(j, i), rotatingSpeed(j, i, nbOarOnSide * 2));
            }
        }
    }

    public static Pair<Integer, Integer> findOptOarConfig(int nbSailor, int nbOarOnSide, Pair<Double, Double> opt) {
        setTurnPossibilities(nbSailor, nbOarOnSide);
        double maxSpeed = oarLinearSpeed(nbSailor, nbOarOnSide * 2);
        double minTurn = Math.abs(rotatingSpeed(1, 0, nbOarOnSide * 2));
        final boolean cond = opt.first > maxSpeed && Math.abs(opt.second) > minTurn;
        if (cond)
            return opt.second > 0 ? findOptConfigBasedOnVr(leftTurnPossibilities, opt)
                    : findOptConfigBasedOnVr(rightTurnPossibilities, opt);
        else
            return findOptConfigBasedOnVl(opt, nbOarOnSide * 2, nbSailor);
    }

    /*
    private static Pair<Integer, Integer> findOptOarConfig(HashMap<Pair<Integer, Integer>, Double> givenSide, Pair<Double, Double> opt, int nbTotalOar) {
        Pair<Integer, Integer> optimal = null;
        double diff = 0.0;
        for (Map.Entry<Pair<Integer, Integer>, Double> entry : givenSide.entrySet()) {
            double vl = oarLinearSpeed(entry.getKey().first + entry.getKey().second, nbTotalOar);
            double vr = entry.getValue();

            double difference = Math.abs((vl - opt.first) / opt.first * 100) + Math.abs((vr - opt.second) / opt.second * 100);


            System.out.println(entry.getKey());
            System.out.println(Pair.of(vl, vr));
            System.out.println("Vl % : " + Math.abs((vl - opt.first) / opt.first * 100));
            System.out.println("Vr %: " + Math.abs((vr - opt.second) / opt.second * 100));
            System.out.println(Math.abs((vl - opt.first) / opt.first * 100) + Math.abs((vr - opt.second) / opt.second * 100));
            System.out.println("\n");


            if (diff == 0 || difference < diff) {
                optimal = entry.getKey();
                diff = difference;
            }
        }
        return optimal;
    }
    */

    private static Pair<Integer, Integer> findOptConfigBasedOnVr(HashMap<Pair<Integer, Integer>, Double> givenSide, Pair<Double, Double> opt) {
        Pair<Integer, Integer> optimal = null;
        double diff = 0.0;
        for (Map.Entry<Pair<Integer, Integer>, Double> entry : givenSide.entrySet()) {
            double difference = Math.abs(opt.second - entry.getValue());

            /*
            System.out.println(entry.getValue());
            System.out.println(difference);
            System.out.println(entry.getKey());
            System.out.println("\n");
            */

            if (diff == 0 || (difference <= diff && (entry.getKey().first + entry.getKey().second) > (optimal.first + optimal.second))) {
                optimal = entry.getKey();
                diff = difference;
            }
        }
        //System.out.println(optimal);
        return optimal;
    }

    private static Pair<Integer, Integer> findOptConfigBasedOnVl(Pair<Double, Double> opt, int nbTotalOar, int nbSailor) {
        Pair<Integer, Integer> optimal = null;
        double diff = 0.0;

        for (int i = 2; i <= nbSailor; i+=2) {
            double difference = opt.first - oarLinearSpeed(i, nbTotalOar);

            /*
            System.out.println(oarLinearSpeed(i, nbTotalOar));
            System.out.println(difference);
            System.out.println(Pair.of(i / 2, i / 2));
            System.out.println("\n");
             */

            if (diff == 0 || difference <= diff && difference > 0) {
                optimal = Pair.of(i / 2, i / 2);
                diff = difference;
            }
        }
        //System.out.println(optimal);
        return optimal != null ? optimal : Pair.of(1, 1);
    }

    public static boolean rudderRotationIsInRange (Double neededRotation){
        return neededRotation > Gouvernail.ALLOWED_ROTATION.first && neededRotation < Gouvernail.ALLOWED_ROTATION.second;
    }

    public static Double optimizedRudderRotation (Double neededRotation) {
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

}
    