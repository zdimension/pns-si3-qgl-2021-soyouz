package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

public class PosOnShip {

	private int x;
	private int y;

	public PosOnShip(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Pair<Integer,Integer> getPos(){
		return Pair.of(getX(), getY());
	}
}
