package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;


/**
 * The wind effect.
 */
public class Wind implements Entity{
    private double orientation;
    private double strength;

    /**
     * Determine how much speed will the wind add to the boat.
     *
     * @param nbOfSails The number of sails.
     * @param openedSails The number of opened sails.
     * @param boat Out boat.
     * @return the speed added by the wind.
     */
    public double windAdditionnalSpeed(int nbOfSails,int openedSails, Bateau boat){
        if (nbOfSails>0) {
            return ((double)openedSails/nbOfSails)*strength*Math.cos(orientation - boat.getPosition().getOrientation());
        }
        return 0;
    }
}
