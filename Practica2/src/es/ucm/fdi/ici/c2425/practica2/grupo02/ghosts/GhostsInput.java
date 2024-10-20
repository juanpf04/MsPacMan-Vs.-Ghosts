package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends Input {

	/*
	 * Data structure to hold all information relative to the ghosts. Works as a
	 * Struct.
	 */
	public static class GhostsInfo {

		public int minDistanceFromPacmanToPPill;
		public int closestPPillToPacman;

		public int[] activePills;

		// Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
		public int nearestEdibleGhostToPacman;
		public int distanceFromPacmanToNearestEdibleGhost;

		// nearest not edible ghost to nearest edible ghost to pacman
		public int nearestGhost;

		public List<Integer> exits;

		// Distance from ghost to nearest pill
		public Map<GHOST, Integer> distancesFromGhostToPill;
		// Distance from ghost to nearest power pill of pacman
		public Map<GHOST, Integer> distancesFromGhostToPPill;
		public Map<GHOST, Integer> distancesFromGhostToPacman;
		public Map<GHOST, Integer> distancesFromPacmanToGhost;

		// Distance from ghost to nearest edible ghost to pacman
		public Map<GHOST, Integer> distancesFromGhostToEdibleGhost;
		public Map<GHOST, Integer> distancesFromEdibleGhostToGhost;

		public Map<GHOST, Boolean> isGhostBehindPacman;
		public Map<GHOST, Boolean> isGhostEdible;
		public Map<GHOST, Boolean> isGhostInLair;
		public Map<GHOST, Boolean> doesGhostRequireAction;

		public Map<GHOST, Double> ghostDensity; // Density of ghosts around a certain ghost

		public int edibleGhosts;

		public GhostsInfo() {
			this.exits = new ArrayList<>();
			this.isGhostBehindPacman = new HashMap<>();
			this.isGhostEdible = new HashMap<>();
			this.isGhostInLair = new HashMap<>();
			this.doesGhostRequireAction = new HashMap<>();
			this.distancesFromGhostToPill = new HashMap<>();
			this.distancesFromGhostToPPill = new HashMap<>();
			this.distancesFromGhostToPacman = new HashMap<>();
			this.distancesFromPacmanToGhost = new HashMap<>();
			this.ghostDensity = new HashMap<>();
		}
	}

	private GhostsInfo info;

	public GhostsInput(Game game, GhostsInfo info) {
		super(game);
		this.info = info;
		this.parseInput();
	}

	@Override
	public void parseInput() {

		if (this.info == null) // if info is null, we can't do anything
			return;

		// - Basics ------------------------------------------

		int pacmanIndex = this.game.getPacmanCurrentNodeIndex();
		MOVE pacmanMove = this.game.getPacmanLastMoveMade();
		int pacmanNextJunction = this.getNextJunctionNode(pacmanIndex, pacmanMove);
		int[] activePills = this.game.getActivePillsIndices();
		int[] activePowerPills = this.game.getActivePowerPillsIndices();

		// -------------------------------------------

		this.info.minDistanceFromPacmanToPPill = Integer.MAX_VALUE;

		for (int ppill : game.getPowerPillIndices()) {
			int distanceFromPacmanToPPill = game.getShortestPathDistance(pacmanIndex, ppill, pacmanMove);
			if (distanceFromPacmanToPPill < this.info.minDistanceFromPacmanToPPill) {
				this.info.minDistanceFromPacmanToPPill = distanceFromPacmanToPPill;
				this.info.closestPPillToPacman = ppill;
			}
		}

		// -------------------------------------------

		this.info.distanceFromPacmanToNearestEdibleGhost = Integer.MAX_VALUE;
		int nearestEdibleGhost = -1;
		this.info.edibleGhosts = 0;

		for (GHOST ghost : GHOST.values()) {
			int ghostIndex = this.game.getGhostCurrentNodeIndex(ghost);
			MOVE ghostMove = this.game.getGhostLastMoveMade(ghost);
			boolean ghostEdible = this.game.getGhostEdibleTime(ghost) > 0;
			boolean ghostInLair = this.game.getGhostLairTime(ghost) > 0;
			boolean ghostRequiresAction = this.game.doesGhostRequireAction(ghost);
			int distanceFromGhostToPacman = this.game.getShortestPathDistance(ghostIndex, pacmanIndex, ghostMove);
			int distanceFromPacmanToGhost = this.game.getShortestPathDistance(pacmanIndex, ghostIndex, pacmanMove);

			// -------------------------------------------

			boolean ghostBehindPacman = this.game.getShortestPathDistance(ghostIndex, pacmanNextJunction,
					ghostMove) < distanceFromGhostToPacman;

			this.info.isGhostBehindPacman.put(ghost, ghostBehindPacman);

			// -------------------------------------------

			this.info.distancesFromGhostToPacman.put(ghost, distanceFromGhostToPacman);
			this.info.distancesFromPacmanToGhost.put(ghost, distanceFromPacmanToGhost);
			if (ghostEdible) {
				this.info.edibleGhosts++;

				if (this.info.distanceFromPacmanToNearestEdibleGhost > distanceFromGhostToPacman) {
					this.info.nearestEdibleGhostToPacman = ghostIndex;
				}
			}

			// -------------------------------------------

			// -------------------------------------------

			// -------------------------------------------
		}

		// -------------------------------------------

		// if there is an edible ghost, set node of not edible ghost closest to them
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
		this.info.nearestGhost = closestGhost;

	}

	public GhostsInfo getInfo() {
		return this.info;
	}

	private int getNextJunctionNode(int node, MOVE move) {
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

	private int getGeometricCenterOfActivePills() {
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

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function. Range is between 0 (very dispersed) and 1.2+ (very dense)
	 * 
	 * @param ghost index will be point of density evaluation
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(int node) {
		double density = 0.0;

		for (GHOST ghost : GHOST.values()) {
			double distance = game.getEuclideanDistance(game.getGhostCurrentNodeIndex(ghost), node);

			if (distance < 20)
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution

		}

		return density;
	}

	private int getDistanceToNearestGhost(GHOST g) {
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
}
