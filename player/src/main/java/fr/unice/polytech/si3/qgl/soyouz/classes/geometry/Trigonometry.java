package fr.unice.polytech.si3.qgl.soyouz.classes.geometry;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

public class Trigonometry {

    //TODO maybe bad concept
    static Pair<Double, Double> oarLinearSpeed(double orientation, int activeOarNb, int totalOarNb){
        return Pair.of(orientation, (double) (165 * activeOarNb / totalOarNb));
    }

    static Pair<Double, Double> linearSpeed(double orientation, int activeOarNb, int totalOarNb){
        return oarLinearSpeed(orientation, activeOarNb, totalOarNb);
    }

}