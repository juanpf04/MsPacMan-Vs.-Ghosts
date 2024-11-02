package es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	/*
	 * Data structure to hold all information relative to the ghosts. Works as a
	 * Struct.
	 */
	public static class GhostsInfo {

		public int minDistanceFromPacmanToPPill; // done
		public int closestPPillToPacman; // done

		public int[] activePills; // done
		public int[] activePowerPills; // done

		// Node of nearest edible ghost to pacman. Returns -1 if no ghost is edbile.
		public int nearestEdibleGhostToPacman; // done
		public int distanceFromPacmanToNearestEdibleGhost; // done

		// nearest not edible ghost to nearest edible ghost to pacman
		public int nearestGhost; // done

		public List<Integer> exits; // done

		// Distance from ghost to center of pills
		public Map<GHOST, Integer> distancesFromGhostToPill; // done
		// Distance from ghost to nearest power pill of pacman
		public Map<GHOST, Integer> distancesFromGhostToPPill; // done
		public Map<GHOST, Integer> distancesFromGhostToPacman; // done
		public Map<GHOST, Integer> distancesFromPacmanToGhost; // done

		// Distance from ghost to nearest edible ghost to pacman
		public Map<GHOST, Integer> distancesFromGhostToEdibleGhost; // done
		public Map<GHOST, Integer> distancesFromEdibleGhostToGhost; // done

		public Map<GHOST, Boolean> isGhostBehindPacman; // done
		public Map<GHOST, Boolean> isGhostEdible; // done
		public Map<GHOST, Boolean> isGhostInLair; // done
		public Map<GHOST, Boolean> doesGhostRequireAction; // done

		public Map<GHOST, Double> ghostDensity; // Density of ghosts around a certain ghost // done

		public int edibleGhosts; // done

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
			this.distancesFromGhostToEdibleGhost = new HashMap<>();
			this.distancesFromEdibleGhostToGhost = new HashMap<>();
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
		int pacmanNextJunction = -1;
		if (pacmanIndex >= 0 && pacmanMove != null) {
			pacmanNextJunction = this.getNextJunctionNode(pacmanIndex, pacmanMove);
		}

		this.info.activePills = this.game.getActivePillsIndices();
		this.info.activePowerPills = this.game.getActivePowerPillsIndices();

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
		this.info.nearestEdibleGhostToPacman = -1;
		this.info.edibleGhosts = 0;
		GHOST nearestEdibleGhost = GHOST.BLINKY; // will be either overwritten or not used

		for (GHOST ghost : GHOST.values()) {
			int ghostIndex = this.game.getGhostCurrentNodeIndex(ghost);
			MOVE ghostMove = this.game.getGhostLastMoveMade(ghost);

			// state maps
			this.info.isGhostEdible.put(ghost, this.game.getGhostEdibleTime(ghost) > 0);
			this.info.isGhostInLair.put(ghost, this.game.getGhostLairTime(ghost) > 0);
			this.info.doesGhostRequireAction.put(ghost, this.game.doesGhostRequireAction(ghost));

			// distance maps
			if (!this.info.isGhostInLair.get(ghost) && pacmanNextJunction >= 0) {
				int pillCenter = getGeometricCenterOfActivePills();
				int distanceFromGhostToPillCenter = pillCenter == -1 ? 0
						: game.getShortestPathDistance(ghostIndex, pillCenter, ghostMove);
				int distanceFromGhostToPPill = game.getShortestPathDistance(ghostIndex, this.info.closestPPillToPacman,
						ghostMove);
				int distanceFromGhostToPacman = this.game.getShortestPathDistance(ghostIndex, pacmanIndex, ghostMove);
				int distanceFromPacmanToGhost = this.game.getShortestPathDistance(pacmanIndex, ghostIndex, pacmanMove);

				this.info.distancesFromGhostToPPill.put(ghost, distanceFromGhostToPPill);
				this.info.distancesFromGhostToPacman.put(ghost, distanceFromGhostToPacman);
				this.info.distancesFromPacmanToGhost.put(ghost, distanceFromPacmanToGhost);
				this.info.distancesFromGhostToPill.put(ghost, distanceFromGhostToPillCenter);

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

			if (this.info.isGhostEdible.get(ghost)) {
				this.info.edibleGhosts++;

				if (this.info.distanceFromPacmanToNearestEdibleGhost > this.info.distancesFromGhostToPacman
						.get(ghost)) {
					nearestEdibleGhost = ghost;
					this.info.nearestEdibleGhostToPacman = ghostIndex;
					this.info.distanceFromPacmanToNearestEdibleGhost = this.info.distancesFromGhostToPacman.get(ghost);
				}
			}

		}

		// -------------------------------------------

		// if there is an edible ghost, set node of not edible ghost closest to them
		int closestGhost = -1;
		this.info.distancesFromGhostToEdibleGhost.clear();
		this.info.distancesFromEdibleGhostToGhost.clear();
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

	/**
	 * Looks into
	 * 
	 * @return
	 */
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

	@Override
	public Collection<String> getFacts() {
		return null;
	}

	public Collection<String> getFacts(GHOST ghost) {
		Vector<String> facts = new Vector<String>();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("(GHOST (name %s) ", ghost.name()))
				.append(String.format("(edible %s) ", this.game.isGhostEdible(ghost)))
				.append(String.format("(behindPacman %s) ", this.info.isGhostBehindPacman.get(ghost)))
				.append(String.format("(distanceMSPACMANNearestPPill %d) ", this.info.distancesFromGhostToPPill.get(ghost)))
				.append(String.format("(distanceMSPACMAN %d) ", this.info.distancesFromGhostToPacman.get(ghost)))
				.append(String.format("(distanceToClosestEdibleGhost %d) ", this.info.distancesFromGhostToEdibleGhost.get(ghost)))
				.append(String.format("(distanceToClosestNotEdibleGhost %d) ", this.info.distancesFromEdibleGhostToGhost.get(ghost)))
				.append(String.format("(ghostDensity %s) ", this.info.ghostDensity.get(ghost)))
				.append(String.format("(pillCount %d))", this.info.activePills.length));
		facts.add(sb.toString());

		facts.add(String.format("(MSPACMAN (mindistancePPill %d))", this.info.minDistanceFromPacmanToPPill));

		return facts;
	}

}
