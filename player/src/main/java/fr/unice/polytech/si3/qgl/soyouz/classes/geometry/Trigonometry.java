package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

public class Trigonometry {

    //TODO maybe bad concept
    static Pair<Double, Double> oarLinearSpeed(double orientation, int activeOarNb, int totalOarNb){
        return Pair.of(orientation, (double) (165 * activeOarNb / totalOarNb));
    }

    static Double newOrientation(int activeRightOar, int activeLeftOar, int totalOarNb) {
        return Math.PI*(activeRightOar - activeLeftOar)/totalOarNb;
    }
}
    