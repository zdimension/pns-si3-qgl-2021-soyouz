package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities;

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
