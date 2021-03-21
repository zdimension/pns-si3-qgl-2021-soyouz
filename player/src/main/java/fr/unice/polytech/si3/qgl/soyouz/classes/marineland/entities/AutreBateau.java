package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.ShapedEntity;

/**
 * Ship entity.
 */
public class AutreBateau extends ShapedEntity implements Entity
{
    private int life;

    /**
     * Getter.
     *
     * @return the health of the ship.
     */
    public int getLife()
    {
        return life;
    }
}
