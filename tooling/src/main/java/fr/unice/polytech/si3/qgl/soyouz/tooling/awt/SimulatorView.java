package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

public interface SimulatorView
{
    void clearHistory();

    void setDrawPath(boolean selected);

    void setDrawNodes(boolean selected);

    void setDebugCollisions(boolean selected);

    void centerView(boolean b);

    void reset();

    void repaint();
}
