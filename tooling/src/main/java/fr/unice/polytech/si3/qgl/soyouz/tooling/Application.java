package fr.unice.polytech.si3.qgl.soyouz.tooling;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
	
	public static void main(String [] args) throws IOException
	{
		Cockpit cockpit = new Cockpit();
		cockpit.initGame(Files.readString(Path.of("initGame.json")));
		cockpit.nextRound(Files.readString(Path.of("NextRound.json")));
		System.out.println("An instance of my team player: " + cockpit);
		System.out.println("When called, it returns some JSON: " + cockpit.nextRound(""));
	}
}
