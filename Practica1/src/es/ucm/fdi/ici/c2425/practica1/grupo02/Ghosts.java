package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.EnumMap;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class Ghosts extends GhostController {

	private final static int LIMIT_DISTANCE = 60;

	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();

	private Map<GHOST, Integer> ghostIndices = new HashMap<>();
	private int mspacmanIndex;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		this.moves.clear();

		mspacmanIndex = game.getPacmanCurrentNodeIndex();

		saveGhostIndices(game);

		for (GHOST ghost : GHOST.values()) {
			if (game.doesGhostRequireAction(ghost)) {

				// flight mode
				if (game.isGhostEdible(ghost) || isPacmanCloseToPowerPill(game)) {
					System.out.println("getting flight move");
					this.moves.put(ghost, getBestFlightMove(game, ghost));
				}

				// attack mode
				else {
					this.moves.put(ghost, getBestAttackMove(game, ghost));
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
	private MOVE getBestAttackMove(Game game, GHOST ghost) {
		return game.getNextMoveTowardsTarget(
				ghostIndices.get(ghost),
				mspacmanIndex,
				game.getGhostLastMoveMade(ghost),
				DM.EUCLID);
	}

	/**
	 * Get the best flight move for a give ghost
	 *
	 * @param game  the current game
	 * @param ghost the ghost we want the flight move for
	 * @return
	 */
	private MOVE getBestFlightMove(Game game, GHOST ghost) {
		int ghostCenter = getGhostCenterIndex(game);
		System.out.println(ghostCenter);
		int moveAwayPoint;

		// check if we need to move away from the other ghosts too
		if (getGhostDensity(game, ghostCenter) >= 2.0) {
			//int[] path = game.getShortestPath(mspacmanIndex, ghostCenter);
			//moveAwayPoint = path[path.length / 2];
			moveAwayPoint = mspacmanIndex;
		}
		// if other ghosts arent close enough just move away from pacman
		else {
			moveAwayPoint = mspacmanIndex;
		}

		return game.getNextMoveAwayFromTarget(
				ghostIndices.get(ghost),
				moveAwayPoint,
				game.getGhostLastMoveMade(ghost),
				DM.EUCLID);
	}

	/**
	 * Save all current ghost indices in the corresponding Map<> attribute
	 * 
	 * @param game the current game
	 */
	private void saveGhostIndices(Game game) {
		this.ghostIndices.clear();
		for (GHOST g : GHOST.values()) {
			int index = game.getGhostCurrentNodeIndex(g);
			this.ghostIndices.put(g, index);
		}
	}

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function
	 * 
	 * @param game     the current game
	 * @param position position at which we want to calculate the ghost density at
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(Game game, int position) {
		double density = 0.0;
		for (GHOST ghost : GHOST.values()) {
			double distance = game.getEuclideanDistance(ghostIndices.get(ghost), position);
			density += Math.exp(-2 * distance); // Exponentially decaying contribution
		}
		System.out.println("Density: " + density);
		return density;
	}

	private int getGhostCenterIndex(Game game) {
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
	 * @param game current game
	 * @return
	 */
	private boolean isPacmanCloseToPowerPill(Game game) {
		for (int powerPill : game.getActivePowerPillsIndices()) {
			if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), powerPill,
					game.getPacmanLastMoveMade()) < LIMIT_DISTANCE)
				return true;
		}

		return false;
	}

}