package fr.unice.polytech.si3.qgl.soyouz.tooling;

import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.Simulator;

import java.io.IOException;

public class Application
{

    public static void main(String[] args) throws IOException
    {
        new Simulator().setVisible(true);
    }
}
