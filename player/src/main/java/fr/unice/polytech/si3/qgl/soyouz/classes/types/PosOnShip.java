package fr.unice.polytech.si3.qgl.soyouz.classes.types;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

public class PosOnShip {

	private int x;
	private int y;

	public PosOnShip(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public PosOnShip(Pair<Integer, Integer> pos){
		this.x = pos.first;
		this.y = pos.second;
	}

	public PosOnShip(OnboardEntity ent){
		this.x = ent.getX();
		this.y = ent.getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Pair<Integer,Integer> getPosCoord(){
		return Pair.of(getX(), getY());
	}

}
