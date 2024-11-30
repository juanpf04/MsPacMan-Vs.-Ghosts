package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman;

import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.GHOST;

public class MsPacManFuzzyMemory {

	private HashMap<String, Double> mem;

	private Map<GHOST, Double> confidence;

	public MsPacManFuzzyMemory() {
		this.mem = new HashMap<String, Double>();
		this.confidence = new HashMap<GHOST, Double>();
		for (GHOST g : GHOST.values())
			confidence.put(g, 100.0);
	}

	public void getInput(MsPacManInput input) {
		for (GHOST g : GHOST.values()) {
			double conf = this.confidence.get(g);
			if (input.isVisible(g))
				conf = 100;
			else
				conf = Double.max(0, conf - 5);

			this.confidence.put(g, conf);
			this.mem.put(g.name() + "confidence", conf);
		}
	}

	public HashMap<String, Double> getFuzzyValues() {
		return this.mem;
	}

}
