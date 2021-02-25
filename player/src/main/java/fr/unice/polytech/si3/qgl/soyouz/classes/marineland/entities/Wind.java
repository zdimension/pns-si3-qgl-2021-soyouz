package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;


/**
 * The wind effect.
 */
public class Wind extends Entity{
    private double orientation;
    private double strength;

    public double windAdditionnalSpeed(int nbOfSails,int openedSails, Bateau boat){
        if (nbOfSails>0){
            return (openedSails/nbOfSails)*strength*Math.cos(orientation - boat.getPosition().getOrientation());
        }
        return 0;
    }
}
