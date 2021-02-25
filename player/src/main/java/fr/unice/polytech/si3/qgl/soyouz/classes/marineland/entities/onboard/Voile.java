package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Voile extends OnboardEntity {

    private boolean openned;

    /**
     * Constructor.
     *
     * @param x Abscissa of the entity.
     * @param y Ordinate of the entity.
     */
    public Voile(@JsonProperty("x") int x,@JsonProperty("y") int y,@JsonProperty("openned") Boolean openned) {
        super(x, y);
        this.openned = openned;
    }

    public boolean isOpenned() {
        return openned;
    }

    public void setOpenned(boolean openned) {
        this.openned = openned;
    }
}
