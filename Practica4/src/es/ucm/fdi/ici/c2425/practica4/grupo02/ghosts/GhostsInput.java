package es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ici.fuzzy.FuzzyInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends FuzzyInput {

	/*
	 * Data structure to hold all information relative to the ghosts. Works as a
	 * Struct.
	 */
	public static class GhostsInfo {

		public int minDistanceFromPacmanToPPill;
		public int closestPPillToPacman;

		public int pillCount;
		public Map<GHOST, Integer> ppills;

		// Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
		public int nearestEdibleGhostToPacman;

		public int nearestGhost;

		public List<Integer> exits;

		// Distance from ghost to nearest power pill of pacman
		public Map<GHOST, Integer> distancesFromGhostToPPill;
		public Map<GHOST, Integer> distancesPPill;
		public Map<GHOST, Integer> distancesFromGhostToPacman;

		// Distance from ghost to nearest edible ghost to pacman
		public Map<GHOST, Integer> distancesFromGhostToEdibleGhost;
		public Map<GHOST, Integer> distancesFromEdibleGhostToGhost;

		public Map<GHOST, Boolean> isGhostBehindPacman;
		public Map<GHOST, Boolean> isGhostEdible;
		public Map<GHOST, Boolean> isGhostInLair;

		// Density of ghosts around a certain ghost
		public Map<GHOST, Double> ghostDensity;

		public Map<GHOST, Integer> closestGhostIndex;

		public GhostsInfo() {
			this.exits = new ArrayList<>();
			this.isGhostBehindPacman = new HashMap<>();
			this.isGhostEdible = new HashMap<>();
			this.isGhostInLair = new HashMap<>();

			this.distancesPPill = new HashMap<>();
			this.distancesFromGhostToPPill = new HashMap<>();
			this.distancesFromGhostToPacman = new HashMap<>();
			this.ghostDensity = new HashMap<>();
			this.distancesFromGhostToEdibleGhost = new HashMap<>();
			this.distancesFromEdibleGhostToGhost = new HashMap<>();

			this.closestGhostIndex = new HashMap<>();

			this.ppills = new HashMap<>();
			this.ppills.put(GHOST.BLINKY, 97);
			this.ppills.put(GHOST.INKY, 102);
			this.ppills.put(GHOST.PINKY, 1143);
			this.ppills.put(GHOST.SUE, 1148);

		}
	}

	private GhostsInfo info;

	public GhostsInput(Game game, GhostsInfo info) {
		super(game);
		this.info = info;
		this.reset();
		this.parseInput();
	}

	@Override
	public void parseInput() {

		if (this.info == null) // if info is null, we can't do anything
			return;

		if (this.isVisible())
			this.fillInfo();
	}

	public HashMap<String, Double> getFuzzyValues(GHOST ghost) {
		HashMap<String, Double> vars = new HashMap<String, Double>();

		vars.put("edible", this.info.isGhostEdible.get(ghost) ? 1.0 : 0.0);
		vars.put("behindPacman", this.info.isGhostBehindPacman.get(ghost) ? 1.0 : 0.0);
		vars.put("distanceNearestPPill", this.info.distancesFromGhostToPPill.get(ghost).doubleValue());
		vars.put("MSPACMANdistanceNearestPPill", this.info.minDistanceFromPacmanToPPill * 1.0);
		vars.put("MSPACMANdistance", this.info.distancesFromGhostToPacman.get(ghost).doubleValue());
		vars.put("distanceToClosestEdibleGhost", this.info.distancesFromGhostToEdibleGhost.get(ghost).doubleValue());
		vars.put("distanceToClosestNotEdibleGhost", this.info.distancesFromEdibleGhostToGhost.get(ghost).doubleValue());
		vars.put("ghostDensity", this.info.ghostDensity.get(ghost));
		vars.put("pillCount", this.info.pillCount * 1.0);
		vars.put("PPILLdistance", this.info.distancesPPill.get(ghost).doubleValue());

		return vars;
	}

	@Override
	@Deprecated // Use getFuzzyValues(GHOST ghost) instead
	public HashMap<String, Double> getFuzzyValues() {
		HashMap<String, Double> vars = new HashMap<String, Double>();

		for (GHOST ghost : GHOST.values())
			vars.putAll(getFuzzyValues(ghost));

		return vars;
	}

	public boolean isVisible() {
		return this.game.getPacmanCurrentNodeIndex() != -1;
	}

	private void reset() {
		for (GHOST ghost : GHOST.values()) {
			if (this.game.getGhostLairTime(ghost) == 0)
				continue;

			this.info.isGhostBehindPacman.put(ghost, false);
			this.info.isGhostEdible.put(ghost, false);
			this.info.isGhostInLair.put(ghost, false);
			this.info.distancesFromGhostToPPill.put(ghost, -1);
			this.info.distancesFromGhostToPacman.put(ghost, -1);
			this.info.ghostDensity.put(ghost, 0.0);
			this.info.distancesFromGhostToEdibleGhost.put(ghost, -1);
			this.info.distancesFromEdibleGhostToGhost.put(ghost, -1);
			this.info.closestGhostIndex.put(ghost, -1);
			this.info.distancesPPill.put(ghost, -1);
		}
	}

	private void fillInfo() {
		int pacmanIndex = this.game.getPacmanCurrentNodeIndex();
		MOVE pacmanMove = this.game.getPacmanLastMoveMade();
		int pacmanNextJunction = -1;
		if (pacmanIndex >= 0 && pacmanMove != null) {
			pacmanNextJunction = this.getNextJunctionNode(pacmanIndex, pacmanMove);
		}

		this.info.pillCount = 50;// activePills.length;

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

//		this.info.distanceFromPacmanToNearestEdibleGhost = Integer.MAX_VALUE;
		this.info.nearestEdibleGhostToPacman = -1;
//		this.info.edibleGhosts = 0;
		GHOST nearestEdibleGhost = GHOST.BLINKY; // will be either overwritten or not used

		for (GHOST ghost : GHOST.values()) {
			int ghostIndex = this.game.getGhostCurrentNodeIndex(ghost);
			MOVE ghostMove = this.game.getGhostLastMoveMade(ghost);

			this.info.distancesPPill.put(ghost,
					game.getShortestPathDistance(ghostIndex, this.info.ppills.get(ghost), ghostMove));

			// state maps
			this.info.closestGhostIndex.put(ghost, closestGhostIndex(ghostIndex));
			this.info.isGhostEdible.put(ghost, this.game.getGhostEdibleTime(ghost) > 0);
			this.info.isGhostInLair.put(ghost, this.game.getGhostLairTime(ghost) > 0);
//			this.info.doesGhostRequireAction.put(ghost, this.game.doesGhostRequireAction(ghost));

			// distance maps
			if (!this.info.isGhostInLair.get(ghost) && pacmanNextJunction >= 0) {
				int distanceFromGhostToPPill = game.getShortestPathDistance(ghostIndex, this.info.closestPPillToPacman,
						ghostMove);
				int distanceFromGhostToPacman = this.game.getShortestPathDistance(ghostIndex, pacmanIndex, ghostMove);

				this.info.distancesFromGhostToPPill.put(ghost, distanceFromGhostToPPill);
				this.info.distancesFromGhostToPacman.put(ghost, distanceFromGhostToPacman);

				boolean ghostBehindPacman = this.game.getShortestPathDistance(ghostIndex, pacmanNextJunction,
						ghostMove) > this.info.distancesFromGhostToPacman.get(ghost);

				this.info.isGhostBehindPacman.put(ghost, ghostBehindPacman);
			}

			// other
			this.info.ghostDensity.put(ghost, getGhostDensity(game.getGhostCurrentNodeIndex(ghost)));

			// -------------------------------------------

			this.info.exits.clear();
			for (int i : getExits()) {
				this.info.exits.add(i);
			}

			// -------------------------------------------

			int distanceFromPacmanToNearestEdibleGhost = Integer.MAX_VALUE;
			if (this.info.isGhostEdible.get(ghost)) {

				if (distanceFromPacmanToNearestEdibleGhost > this.info.distancesFromGhostToPacman.get(ghost)) {
					nearestEdibleGhost = ghost;
					this.info.nearestEdibleGhostToPacman = ghostIndex;
					distanceFromPacmanToNearestEdibleGhost = this.info.distancesFromGhostToPacman.get(ghost);
				}
			}

		}

		// -------------------------------------------

		// if there is an edible ghost, set node of not edible ghost closest to them
		int closestGhost = -1;
		for (GHOST ghost : GHOST.values()) {
			this.info.distancesFromGhostToEdibleGhost.put(ghost, Integer.MAX_VALUE);
			this.info.distancesFromEdibleGhostToGhost.put(ghost, Integer.MAX_VALUE);
		}
		if (this.info.nearestEdibleGhostToPacman != -1) {
			double ghostDistance = Integer.MAX_VALUE;

			for (GHOST ghost : GHOST.values()) {
				if (!game.isGhostEdible(ghost)) {
					int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
					double currDistance = game.getDistance(ghostIndex, this.info.nearestEdibleGhostToPacman, DM.PATH);
					this.info.distancesFromGhostToEdibleGhost.put(ghost, (int) currDistance);
					this.info.distancesFromEdibleGhostToGhost.put(nearestEdibleGhost, (int) currDistance);

					if (currDistance < ghostDistance) {
						closestGhost = ghostIndex;
						ghostDistance = currDistance;
					}
				}
			}
		}
		this.info.nearestGhost = closestGhost;
	}

	private int[] getExits() {
		int nextJunction = getNextJunctionNode(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		MOVE[] nextMoves = game.getPossibleMoves(nextJunction);
		int[] nextNextJunctions = new int[nextMoves.length];
		int iterator = 0;
		for (MOVE move : nextMoves) {
			nextNextJunctions[iterator] = getNextJunctionNode(game.getNeighbour(nextJunction, move), move);
		}
		return nextNextJunctions;
	}

	private int getNextJunctionNode(int node, MOVE move) {
		int nextNode = this.game.getNeighbour(node, move);
		if (nextNode < 0) {
			return node;
		}
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

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function. Range is between 1.0 (very dispersed) and 1.4+ (very dense)
	 * 
	 * @param ghost index will be point of density evaluation
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(int node) {
		double density = 0.0;

		for (GHOST ghost : GHOST.values()) {
			double distance = game.getEuclideanDistance(game.getGhostCurrentNodeIndex(ghost), node);

			if (distance < 50)
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution

		}

		return density;
	}

	private int closestGhostIndex(int index) {
		double minDistance = Double.MAX_VALUE;
		GHOST theChosenOne = GHOST.BLINKY;

		for (GHOST ghost : GHOST.values()) {
			double currDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghost), index, DM.PATH);
			if (minDistance > currDistance) {
				minDistance = currDistance;
				theChosenOne = ghost;
			}
		}

		return game.getGhostCurrentNodeIndex(theChosenOne);
	}
}
