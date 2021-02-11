package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import java.util.HashMap;

public class Trigonometry {

    // TODO POSSIBILITE DE PASSER LA PAIR EN CLE POUR POUVOIR AVOIR DIVERSES VITESSES POSSIBLES AVEC DES ANGLES SIMILAIRES
    static HashMap<Double, Pair<Integer, Integer>> turnPossibilities = new HashMap<>();

    //TODO maybe bad concept
    static Pair<Double, Double> oarLinearSpeed(double orientation, int activeOarNb, int totalOarNb){
        return Pair.of(orientation, (double) (165 * activeOarNb / totalOarNb));
    }

    static Double newOrientation(int activeLeftOar, int activeRightOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }

    static void setTurnPossibilities(boolean left, int nbSailor, int nbOarLeft, int nbOarRight) {
        for (int i = 0; i <= nbOarRight && i <= nbSailor; i++) {
            for (int j = 0; j <= nbOarLeft && j <= nbSailor; j++) {
                if (left) {
                    if (i > j && i + j <= nbSailor)
                        turnPossibilities.put(newOrientation(j, i, nbOarLeft + nbOarRight), Pair.of(j, i));
                } else {
                    if (i < j && i + j <= nbSailor)
                        turnPossibilities.put(newOrientation(j, i, nbOarLeft + nbOarRight), Pair.of(j, i));
                }
            }
        }
    }
}
    