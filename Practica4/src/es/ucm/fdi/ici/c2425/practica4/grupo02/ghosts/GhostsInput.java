package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts;

import java.util.HashMap;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import es.ucm.fdi.ici.fuzzy.FuzzyInput;

public class GhostsInput extends FuzzyInput {

	private double[] distance;
	
	
	public GhostsInput(Game game) {
		super(game);
	}
	
	@Override
	public void parseInput() {

		distance = new double[] {-1,-1,-1,-1};

		
		for(GHOST g: GHOST.values()) {
			int index = g.ordinal();
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) {
				distance[index] = game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			}
			else
				distance[index] = -1;
		}
	}
	
	public boolean isVisible(GHOST ghost)
	{
		return distance[ghost.ordinal()]!=-1;
	}
	
	

	@Override
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String,Double> vars = new HashMap<String,Double>();
		for(GHOST g: GHOST.values()) {
			vars.put(g.name()+"distance",   distance[g.ordinal()]);
		}
		return vars;
	}

}
