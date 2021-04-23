package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.ShapedEntity;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;

import java.util.Collection;
import java.util.LinkedList;

public interface SimulatorView
{
    static void updateHistory(SimulatorModel model, LinkedList<Position>[] shipHistory)
    {
        var ships = model.getShips();
        for (int i = 0; i < ships.length; i++)
        {
            var history = shipHistory[i];
            var shipPos = ships[i].getPosition();
            if (history.isEmpty() || !shipPos.equals(history.getLast()))
            {
                history.add(shipPos);
            }
        }
    }

    void clearHistory();

    void setDrawPath(boolean selected);

    void setDrawNodes(boolean selected);

    void setDebugCollisions(boolean selected);

    void centerView(boolean b);

    void reset();

    void update();

    SimulatorModel getModel();

    default Collection<ShapedEntity> getVisibleShapes()
    {
        return getModel().cockpits[0].entityMemory.values();
    }
}
