package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts;

import java.util.HashMap;

import pacman.game.Constants.GHOST;

public class GhostsFuzzyMemory {

	private HashMap<String, Double> mem;

	private double confidence;

	public GhostsFuzzyMemory() {
		this.mem = new HashMap<String, Double>();
		this.confidence = 100;
	}

	public void getInput(GhostsInput input) {
		double conf = this.confidence;
		if (input.isVisible())
			conf = 100;
		else
			conf = Double.max(0, conf - 5);
		this.mem.put("MSPACMANconfidence", conf);
	}

	public HashMap<String, Double> getFuzzyValues(GHOST ghost) {
		return this.mem;
	}

}
