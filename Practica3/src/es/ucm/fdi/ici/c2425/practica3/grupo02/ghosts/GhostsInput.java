package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	private int minPacmanDistancePPill;
	private int ppill;
	private int pacman;

	public GhostsInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		this.pacman = this.game.getPacmanCurrentNodeIndex();
		this.minPacmanDistancePPill = Integer.MAX_VALUE;
		for (int ppill : this.game.getActivePowerPillsIndices()) {
			int distance = this.game.getShortestPathDistance(this.pacman, ppill);
			if (distance < this.minPacmanDistancePPill) {
				this.minPacmanDistancePPill = distance;
				this.ppill = ppill;
			}
		}
	}

	public Collection<String> getFacts(GHOST ghost) {
		Vector<String> facts = new Vector<String>();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("(GHOST (name %s) ", ghost.name()))
				.append(String.format("(edible %s) ", this.game.isGhostEdible(ghost)))
				.append(String.format("(behindPacman %s) ", this.isGhostBehindMsPacMan(ghost)))
				.append(String.format("(distanceMSPACMANNearestPPill %d) ", this.game.getShortestPathDistance(this.game.getGhostCurrentNodeIndex(ghost), this.ppill)))
				.append(String.format("(distanceMSPACMAN %d) ", this.game.getShortestPathDistance(this.game.getGhostCurrentNodeIndex(ghost), this.pacman)))
				.append(String.format("(distanceToClosestEdibleGhost %d) ", 100)) // TODO
				.append(String.format("(distanceToClosestNotEdibleGhost %d) ", 150)) // TODO
				.append(String.format("(ghostDensity %d) ", 14)) // TODO
				.append(String.format("(pillCount %d))", 200)); // TODO

		facts.add(sb.toString());
		facts.add(String.format("(MSPACMAN (mindistancePPill %d))", (int) this.minPacmanDistancePPill));
		return facts;
	}

	private boolean isGhostBehindMsPacMan(GHOST ghost) { // TODO
		return false;
	}

	@Override
	public Collection<String> getFacts() {
		return null;
	}

}
