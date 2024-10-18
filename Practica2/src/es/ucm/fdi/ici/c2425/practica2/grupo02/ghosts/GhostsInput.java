package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends Input {

	private boolean BLINKYedible;
	private boolean INKYedible;
	private boolean PINKYedible;
	private boolean SUEedible;
	private double minPacmanDistancePPill;
	private int closestPPillToPacman;
	private GhostsInfo info;

	public GhostsInput(Game game, GhostsInfo info) {
		super(game);
		this.info = info;
	}

	@Override
	public void parseInput() {
		this.BLINKYedible = game.isGhostEdible(GHOST.BLINKY);
		this.INKYedible = game.isGhostEdible(GHOST.INKY);
		this.PINKYedible = game.isGhostEdible(GHOST.PINKY);
		this.SUEedible = game.isGhostEdible(GHOST.SUE);

		int pacman = game.getPacmanCurrentNodeIndex();
		this.info.setPacmanNextJunction(getNextJunctionNode(pacman, game.getPacmanLastMoveMade()));

		this.minPacmanDistancePPill = Double.MAX_VALUE;

		for (int ppill : game.getPowerPillIndices()) {

			// find closest ppill to pacman and distance to it
			double distance = game.getDistance(pacman, ppill, DM.PATH);
			if (distance < this.minPacmanDistancePPill) {
				this.minPacmanDistancePPill = distance;
				this.closestPPillToPacman = ppill;
			}

			// ghost distances to pacman
			for (GHOST ghost : GHOST.values()) {
				double ghostDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghost),ppill,DM.PATH);
				if (this.info.getDistanceToNearestPPill(ghost) > ghostDistance) {
					this.info.setDistanceToNearestPPill(ghost, ghostDistance);
					this.info.setNearestPPill(ghost, ppill);
				}
			}
		}

		// ghost dtstances to pacman, nearest edible ghost to pacman
		info.setNearestEdibleGhostToPacman(-1);
		for (GHOST ghost : GHOST.values()) {
			int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
			double ghostDistance = game.getDistance(ghostIndex,pacman,DM.PATH);
			info.setDistanceGhostToPacman(ghost,ghostDistance);
			if (game.isGhostEdible(ghost)) {
				if (info.getNearestEdibleGhostToPacman() > ghostDistance) {
					info.setNearestEdibleGhostToPacman(ghostIndex);
				}
			}
		}

		// if there is an edible ghost, set node of not edible ghost closest to them
		int nearestEdibleGhost = info.getNearestEdibleGhostToPacman();
		int closestGhost = -1;
		if (nearestEdibleGhost != -1) {
			double ghostDistance = Integer.MAX_VALUE;

			for (GHOST ghost : GHOST.values()) {
				if (!game.isGhostEdible(ghost)) {
					int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
					double currDistance = game.getDistance(ghostIndex,nearestEdibleGhost,DM.PATH);

					if (currDistance < ghostDistance) {
						closestGhost = ghostIndex;
						ghostDistance = currDistance;
					}
				}
			}
		}
		this.info.setNearestGhost(closestGhost);


	}

	public boolean isBLINKYedible() {
		return BLINKYedible;
	}

	public boolean isINKYedible() {
		return INKYedible;
	}

	public boolean isPINKYedible() {
		return PINKYedible;
	}

	public boolean isSUEedible() {
		return SUEedible;
	}

	public double getMinPacmanDistancePPill() {
		return minPacmanDistancePPill;
	}

	public int getNumberOfActivePills() {
		return game.getNumberOfActivePills();
	}

	public boolean isGhostInLair(GHOST ghost) {
		return game.getGhostLairTime(ghost) > 0;
	}

	public int getNextJunctionNode(int node, MOVE move) {
		int nextNode = this.game.getNeighbour(node,move);
		int curr = node;
		MOVE currMove = move;

		while(!game.isJunction(nextNode)) {
			curr = nextNode;
			currMove = this.game.getPossibleMoves(curr,currMove)[0];
			nextNode = this.game.getNeighbour(curr,currMove);
		}

		return nextNode;
	}

}
