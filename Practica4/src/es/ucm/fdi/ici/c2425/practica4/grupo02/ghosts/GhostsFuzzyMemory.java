package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts;

import java.util.HashMap;

import pacman.game.Constants.GHOST;

public class GhostsFuzzyMemory {
	HashMap<String,Double> mem;
	
	double[] confidence = {100,100,100,100};

	
	public GhostsFuzzyMemory() {
		mem = new HashMap<String,Double>();
	}
	
	public void getInput(GhostsInput input)
	{
		for(GHOST g: GHOST.values()) {
			double conf = confidence[g.ordinal()];
			if(input.isVisible(g))
				conf = 100;
			else
				conf = Double.max(0, conf-5);
			mem.put(g.name()+"confidence", conf);			
		}

	}
	
	public HashMap<String, Double> getFuzzyValues() {
		return mem;
	}
	
}
