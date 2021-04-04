package fr.unice.polytech.si3.qgl.soyouz.tooling;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application
{

    public static void main(String[] args) throws IOException
    {
        new Simulator().setVisible(true);
    }
}
