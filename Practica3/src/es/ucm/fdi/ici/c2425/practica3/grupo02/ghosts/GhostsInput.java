package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	private double minPacmanDistancePPill;

	public GhostsInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		int pacman = game.getPacmanCurrentNodeIndex();
		this.minPacmanDistancePPill = Double.MAX_VALUE;
		for (int ppill : game.getPowerPillIndices()) {
			double distance = game.getDistance(pacman, ppill, DM.PATH);
			this.minPacmanDistancePPill = Math.min(distance, this.minPacmanDistancePPill);
		}
	}

	public Collection<String> getFacts(GHOST ghost) {
		Vector<String> facts = new Vector<String>();
		facts.add(String.format("(GHOST (edible %s))", this.game.isGhostEdible(ghost)));
		facts.add(String.format("(MSPACMAN (mindistancePPill %d))", (int) this.minPacmanDistancePPill));
		return facts;
	}

	@Override
	public Collection<String> getFacts() {
		return null;
	}

}
