package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class Ghosts extends GhostController {

	private final static int LIMIT_DISTANCE = 25;

	private Game game;

	private int mspacmanNode;

	private EnumMap<GHOST, MOVE> moves;
	private Map<GHOST, Integer> ghostIndices;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");

		// Initialize data structures

		this.moves = new EnumMap<>(GHOST.class);
		this.ghostIndices = new HashMap<>();
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		this.moves.clear();
		this.game = game;

		mspacmanNode = this.game.getPacmanCurrentNodeIndex();

		this.saveGhostIndices();

		for (GHOST ghost : GHOST.values()) {
			if (game.doesGhostRequireAction(ghost)) {

				// flight mode
				if (game.isGhostEdible(ghost)) {
					this.moves.put(ghost, getBestFlightMove(ghost));
				}

				else if (isPacmanCloseToPowerPill()) {
					int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
					int closestPPill = Methods.getClosestPowerPill(this.game, ghostIndex);
					int pacmanDist = game.getShortestPathDistance(ghostIndex, closestPPill);
					int ghostDist = game.getShortestPathDistance(mspacmanNode, closestPPill);
					if (pacmanDist > ghostDist) {
						MOVE move = game.getNextMoveTowardsTarget(ghostIndex, closestPPill,
								game.getGhostLastMoveMade(ghost), DM.EUCLID);
						this.moves.put(ghost, move);
					} else {
						this.moves.put(ghost, getBestFlightMove(ghost));
					}
				}

				// attack mode
				else {
					this.moves.put(ghost, getBestAttackMove(ghost));
				}
			}
		}
		return this.moves;
	}

	/**
	 * Get the best flight move for a give ghost
	 * 
	 * @param game  the current game
	 * @param ghost the ghost we want the flight move for
	 * @return
	 */
	private MOVE getBestAttackMove(GHOST ghost) {
		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
		if (getGhostDensity(ghostIndex) >= 1.2) {
			return game.getNextMoveAwayFromTarget(ghostIndices.get(ghost), getGhostCenterIndex(),
					game.getGhostLastMoveMade(ghost), DM.EUCLID);
		}

		return game.getNextMoveTowardsTarget(ghostIndices.get(ghost), mspacmanNode, game.getGhostLastMoveMade(ghost),
				DM.EUCLID);
	}

	/**
	 * Get the best flight move for a give ghost
	 *
	 * @param game  the current game
	 * @param ghost the ghost we want the flight move for
	 * @return
	 */
	private MOVE getBestFlightMove(GHOST ghost) {
		int ghostCenter = getGhostCenterIndex();
		int moveAwayPoint;

		// check if we need to move away from the other ghosts too
		if (getGhostDensity(ghostCenter) >= 1.2) {
			moveAwayPoint = ghostCenter;
		}
		// if other ghosts arent close enough just move away from pacman
		else {
			moveAwayPoint = mspacmanNode;
		}

		return game.getNextMoveAwayFromTarget(ghostIndices.get(ghost), moveAwayPoint, game.getGhostLastMoveMade(ghost),
				DM.EUCLID);
	}

	/**
	 * Save all current ghost indices in the corresponding Map<> attribute
	 * 
	 * @param game the current game
	 */
	private void saveGhostIndices() {
		this.ghostIndices.clear();
		for (GHOST g : GHOST.values()) {
			int index = game.getGhostCurrentNodeIndex(g);
			this.ghostIndices.put(g, index);
		}
	}

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function. Range is between 0 and 1, where 1 is high density
	 * 
	 * @param game     the current game
	 * @param position position at which we want to calculate the ghost density at
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(int position) {
		double density = 0.0;
		for (GHOST ghost : GHOST.values()) {
			double distance = game.getEuclideanDistance(ghostIndices.get(ghost), position);
			if (distance < 25) {
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution
			}
		}
		return density;
	}

	private int getGhostCenterIndex() {
		int bestNode = -1;
		double minDistanceSum = Double.MAX_VALUE;

		for (GHOST g1 : GHOST.values()) {
			double currentDistanceSum = 0;
			for (GHOST g2 : GHOST.values()) {
				if (g1 != g2) {
					currentDistanceSum += game.getEuclideanDistance(ghostIndices.get(g1), ghostIndices.get(g2));
				}
			}
			// Check if this node has the smallest total distance
			if (currentDistanceSum < minDistanceSum) {
				minDistanceSum = currentDistanceSum;
				bestNode = ghostIndices.get(g1);
			}
		}

		return bestNode;
	}

	/**
	 * Check whether pacman is LIMIT_DISTANCE away from a PowerPill
	 * 
	 * 
	 */
	private boolean isPacmanCloseToPowerPill() {
		for (int powerPill : game.getActivePowerPillsIndices()) {
			if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), powerPill,
					game.getPacmanLastMoveMade()) < LIMIT_DISTANCE)
				return true;
		}

		return false;
	}

}