package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.HashMap;

/**
 * A class to do some math/trigonometric stuff. Ewww.
 */
public class Trigonometry {

    // TODO POSSIBILITE DE PASSER LA PAIR EN CLE POUR POUVOIR AVOIR DIVERSES VITESSES POSSIBLES AVEC DES ANGLES SIMILAIRES
    static HashMap<Double, Pair<Integer, Integer>> leftTurnPossibilities = new HashMap<>();
    static HashMap<Double, Pair<Integer, Integer>> rightTurnPossibilities = new HashMap<>();

    /**
     * Determine the orientation and the linear speed of the boat in a specific configuration.
     *
     * @param orientation The actual orientation of the boat.
     * @param activeOarNb The number of active oar.
     * @param totalOarNb The total number of oar.
     * @return a pair that contains all the information.
     */
    //TODO maybe bad concept
    static Pair<Double, Double> oarLinearSpeed(double orientation, int activeOarNb, int totalOarNb){
        return Pair.of(orientation, (double) (165 * activeOarNb / totalOarNb));
    }

    /**
     * Determine the angle of rotation of a specific combination of oar.
     *
     * @param activeLeftOar The number of active oar on the left side of the boat.
     * @param activeRightOar The number of active oar on the right side of the boat.
     * @param totalOarNb The total number of oar.
     * @return the angle of the rotation.
     */
    static Double newOrientation(int activeLeftOar, int activeRightOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }

    /**
     * Method to determine every angle of rotation possible according to the game parameters.
     * Then they are stocked into two maps, one for each side (right/left).
     *
     * @param nbSailor The number of sailor on the boat.
     * @param nbOarLeft The number of oar on the left side of the boat.
     * @param nbOarRight The number of oar on the rignt side of the boat.
     */
    //TODO OAR ONLY, does not take in count the future RUDDER
    static void setTurnPossibilities(int nbSailor, int nbOarLeft, int nbOarRight) {
        for (int i = 0; i <= nbOarRight && i <= nbSailor; i++) {
            for (int j = 0; j <= nbOarLeft && j <= nbSailor; j++) {
                    if (i > j && i + j <= nbSailor)
                        leftTurnPossibilities.put(newOrientation(j, i, nbOarLeft + nbOarRight), Pair.of(j, i));
                    if (i < j && i + j <= nbSailor)
                        rightTurnPossibilities.put(newOrientation(j, i, nbOarLeft + nbOarRight), Pair.of(j, i));
            }
        }
    }
}
    