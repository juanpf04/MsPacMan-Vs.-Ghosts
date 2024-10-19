package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

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
		this.parseInput();
	}

	@Override
	public void parseInput() {

		if (this.info == null)
			return;

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
				double ghostDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghost), ppill, DM.PATH);
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
			double ghostDistance = game.getDistance(ghostIndex, pacman, DM.PATH);
			info.setDistanceGhostToPacman(ghost, ghostDistance);
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
					double currDistance = game.getDistance(ghostIndex, nearestEdibleGhost, DM.PATH);

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

	public double getMsPacManMinDistancePPill() {
		return minPacmanDistancePPill;
	}

	public int getNumberOfActivePills() {
		return game.getNumberOfActivePills();
	}

	public boolean isGhostInLair(GHOST ghost) {
		return game.getGhostLairTime(ghost) > 0;
	}

	public int getNextJunctionNode(int node, MOVE move) {
		int nextNode = this.game.getNeighbour(node, move);
		int curr = node;
		MOVE currMove = move;

		while (!game.isJunction(nextNode)) {
			curr = nextNode;
			currMove = this.game.getPossibleMoves(curr, currMove)[0];
			nextNode = this.game.getNeighbour(curr, currMove);
		}

		return nextNode;
	}

	public boolean isGhostBehindPacman(GHOST ghost) {
		int pacman = game.getPacmanCurrentNodeIndex();
		int pacmanNextJunction = getNextJunctionNode(pacman, game.getPacmanLastMoveMade());
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);

		return game.getDistance(ghostIndex, pacmanNextJunction, DM.PATH) < game.getDistance(ghostIndex, pacman,
				DM.PATH);
	}

	public GHOST closestGhostToIndex(int index) {
		double minDistance = Double.MAX_VALUE;
		GHOST theChosenOne = GHOST.BLINKY;

		for (GHOST ghost : GHOST.values()) {
			double currDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghost), index, DM.PATH);
			if (minDistance > currDistance) {
				minDistance = currDistance;
				theChosenOne = ghost;
			}
		}

		return theChosenOne;
	}

	public int getGeometricCenterOfActivePills() {
		int[] pills = game.getActivePillsIndices();
		return geometricCenterOfIndexlist(pills);
	}

	private int geometricCenterOfIndexlist(int[] list) {
		int bestNode = -1;
		double minDistanceSum = Double.MAX_VALUE;

		for (int i : list) {
			double currentDistanceSum = 0;
			for (int j : list)
				if (i != j)
					currentDistanceSum += game.getDistance(i, j, DM.PATH);

			// Check if this node has the smallest total distance
			if (currentDistanceSum < minDistanceSum) {
				minDistanceSum = currentDistanceSum;
				bestNode = i;
			}
		}

		return bestNode;
	}

	public double getDistanceToMsPacManNearestPPill(GHOST ghost) {
		return game.getDistance(game.getGhostCurrentNodeIndex(ghost), this.closestPPillToPacman, DM.PATH);
	}

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function. Range is between 0 (very dispersed) and 1.2+ (very dense)
	 * 
	 * @param ghost index will be point of density evaluation
	 * @return numerical value for ghost density
	 */
	public double getGhostDensity(GHOST g) {
		double density = 0.0;
		int position = game.getGhostCurrentNodeIndex(g);

		for (GHOST ghost : GHOST.values()) {
			double distance = game.getEuclideanDistance(game.getGhostCurrentNodeIndex(ghost), position);

			if (distance < 20)
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution

		}

		return density;
	}

	public boolean isGhostEdible(GHOST ghost) {
		return game.isGhostEdible(ghost);
	}

	public double getDistanceMsPacMan(GHOST ghost) {
		return game.getDistance(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), DM.PATH);
	}

	public boolean doesGhostRequiresAction(GHOST ghost) {
		return game.doesGhostRequireAction(ghost);
	}

	public int getDistanceToNearestGhost(GHOST g) {
		int index = game.getGhostCurrentNodeIndex(g);
		double minDistance = Double.MAX_VALUE;
		int result = -1;

		for (GHOST ghost : GHOST.values()) {
			int curr = game.getGhostCurrentNodeIndex(ghost);
			double ghostDistance = game.getDistance(curr, index, DM.PATH);
			if (minDistance > ghostDistance) {
				minDistance = ghostDistance;
				result = curr;
			}
		}

		return result;
	}

	public int getNumberOfEdibleGhosts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getDistanceFromMsPacManToEdibleGhost() {
		// TODO Auto-generated method stub
		return 0;
	}

}
