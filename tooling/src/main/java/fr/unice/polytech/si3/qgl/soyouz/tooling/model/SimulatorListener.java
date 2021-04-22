package fr.unice.polytech.si3.qgl.soyouz.tooling.model;

public interface SimulatorListener
{
    void npChanged();

    void fileLoaded();

    void turnEnd();

    void updateRequired();

    void gameFinished();
}
