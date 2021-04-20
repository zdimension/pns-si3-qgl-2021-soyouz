package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;

interface SimulatorListener
{
    void npChanged(NextRoundParameters np);

    void fileLoaded(InitGameParameters ip, Cockpit cockpit);

    void turnEnd();

    void updateRequired();

    void gameFinished();
}
