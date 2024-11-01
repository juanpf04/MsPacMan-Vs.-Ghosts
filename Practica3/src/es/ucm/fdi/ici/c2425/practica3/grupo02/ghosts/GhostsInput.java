package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts;

import java.util.Collection;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	private boolean BLINKYedible;
	private boolean INKYedible;
	private boolean PINKYedible;
	private boolean SUEedible;
	private double minPacmanDistancePPill;

	public GhostsInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		this.BLINKYedible = game.isGhostEdible(GHOST.BLINKY);
		this.INKYedible = game.isGhostEdible(GHOST.INKY);
		this.PINKYedible = game.isGhostEdible(GHOST.PINKY);
		this.SUEedible = game.isGhostEdible(GHOST.SUE);

		int pacman = game.getPacmanCurrentNodeIndex();
		this.minPacmanDistancePPill = Double.MAX_VALUE;
		for (int ppill : game.getPowerPillIndices()) {
			double distance = game.getDistance(pacman, ppill, DM.PATH);
			this.minPacmanDistancePPill = Math.min(distance, this.minPacmanDistancePPill);
		}
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();

		for (GHOST ghost : GHOST.values()) {
			facts.add(String.format("(%s (edible %s))", ghost, this.game.isGhostEdible(ghost)));
		}

		facts.add(String.format("(MSPACMAN (mindistancePPill %d))", (int) this.minPacmanDistancePPill));
		return facts;
	}

}
