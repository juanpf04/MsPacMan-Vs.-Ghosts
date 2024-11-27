package es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman;

import java.util.HashMap;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import es.ucm.fdi.ici.fuzzy.FuzzyInput;

public class MsPacManInput extends FuzzyInput {

	private double[] distance;
	private double[] edible;
	private int[] edibleTime;
	private double[] lair;
	private int[] lairTimes;
	
	public MsPacManInput(Game game) {
		super(game);
	}
	
	@Override
	public void parseInput() {

		distance = new double[] {-1,-1,-1,-1};
		edible = new double[] {0,0,0,0};
		edibleTime = new int[] {-1,-1,-1,-1};

		lair = new double[] {0,0,0,0};
		lairTimes = new int[] {-1,-1,-1,-1};
		
		for(GHOST g: GHOST.values()) {
			int index = g.ordinal();
			int pos = game.getGhostCurrentNodeIndex(g);
			if(pos != -1) {
				distance[index] = game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);

				int tiempo = game.getGhostEdibleTime(g);
				int lairTime = game.getGhostLairTime(g);
				
				if(tiempo > 0){
					edible[index] = 1;
					edibleTime[index] = game.getGhostEdibleTime(g);
				}

				if(lairTime > 0){
					lair[index] = 1;
					lairTimes[index] = game.getGhostLairTime(g);
				}

			}
			else{
				distance[index] = -1;

				edible[index] = 0;
				edibleTime[index] = -1;

				lair[index] = 0;
				lairTimes[index] = -1;
			}
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
			double node = (double)game.getGhostCurrentNodeIndex(g);
			vars.put(g.name()+"distance",   distance[g.ordinal()]);
			vars.put(g.name()+"edible", edible[g.ordinal()]);
			vars.put(g.name()+"edibletime", edible[g.ordinal()]);

			vars.put(g.name()+"jail", lair[g.ordinal()]);
			vars.put(g.name()+"jailtime", (double) lairTimes[g.ordinal()]);
			
			vars.put(g.name()+"position", node != -1 ? node : -1);
		}
		return vars;
	}

}
