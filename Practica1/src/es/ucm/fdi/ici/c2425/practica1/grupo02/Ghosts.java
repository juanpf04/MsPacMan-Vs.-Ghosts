package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Ghost;
import pacman.game.Game;

public final class Ghosts extends GhostController {

	private final static int LIMIT_DISTANCE = 20;
	private final static double UMBRAL_DENSITY = 1.2;

	private Game game;

	private int mspacmanNode;

	private EnumMap<GHOST, MOVE> moves;
	private List<Ghost> ghosts;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");

		// Initialize data structures

		this.moves = new EnumMap<>(GHOST.class);

		this.ghosts = new ArrayList<>();

		for (GHOST ghost : GHOST.values()) {
			this.ghosts.add(new Ghost(ghost, -1, 0, 0, MOVE.NEUTRAL));
		}
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		this.moves.clear();
		this.game = game;

		mspacmanNode = this.game.getPacmanCurrentNodeIndex();

		for (Ghost ghost : this.ghosts) {
			// Copy ghost data
			GHOST type = ghost.type;
			ghost.currentNodeIndex = this.game.getGhostCurrentNodeIndex(type);
			ghost.lastMoveMade = this.game.getGhostLastMoveMade(type);
			ghost.edibleTime = this.game.getGhostEdibleTime(type);
			ghost.lairTime = this.game.getGhostLairTime(type);

			if (this.game.doesGhostRequireAction(type)) {

				// flight mode
				if (this.game.isGhostEdible(type))
					this.moves.put(type, this.getBestFlightMove(ghost));

				else if (this.isPacmanCloseToPowerPill()) {
					int closestPPill = Methods.getClosestPowerPill(this.game, ghost.currentNodeIndex);
					int pacmanDist = this.game.getShortestPathDistance(ghost.currentNodeIndex, closestPPill);
					int ghostDist = this.game.getShortestPathDistance(this.mspacmanNode, closestPPill);
					if (pacmanDist > ghostDist) {
						MOVE move = this.game.getNextMoveTowardsTarget(ghost.currentNodeIndex, closestPPill,
								this.game.getGhostLastMoveMade(type), DM.EUCLID);
						this.moves.put(type, move);
					} else
						this.moves.put(type, this.getBestFlightMove(ghost));

				}

				// attack mode
				else
					this.moves.put(type, this.getBestAttackMove(ghost));

			}
		}

		return this.moves;
	}

	/**
	 * Get the best flight move for a give ghost
	 * 
	 * @param ghost the ghost we want the flight move for
	 * @return
	 */
	private MOVE getBestAttackMove(Ghost ghost) {
		if (this.getGhostDensity(ghost.currentNodeIndex) >= UMBRAL_DENSITY)
			return this.game.getNextMoveAwayFromTarget(ghost.currentNodeIndex, this.getGhostCenterIndex(),
					ghost.lastMoveMade, DM.EUCLID);

		return this.game.getNextMoveTowardsTarget(ghost.currentNodeIndex, this.mspacmanNode, ghost.lastMoveMade,
				DM.EUCLID);
	}

	/**
	 * Get the best flight move for a give ghost
	 *
	 * @param ghost the ghost we want the flight move for
	 * @return
	 */
	private MOVE getBestFlightMove(Ghost ghost) {
		int ghostCenter = getGhostCenterIndex();

		// if other ghosts aren't close enough just move away from pacman
		int moveAwayPoint = mspacmanNode;

		// check if we need to move away from the other ghosts too
		if (getGhostDensity(ghostCenter) >= UMBRAL_DENSITY)
			moveAwayPoint = ghostCenter;

		return game.getNextMoveAwayFromTarget(ghost.currentNodeIndex, moveAwayPoint, ghost.lastMoveMade, DM.EUCLID);
	}

	/**
	 * Calculates ghost density at a certain point using an exponential density
	 * function. Range is between 0 and 1, where 1 is high density.
	 * 
	 * @param position position at which we want to calculate the ghost density at
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(int position) {
		double density = 0.0;

		for (Ghost ghost : this.ghosts) {
			double distance = game.getEuclideanDistance(ghost.currentNodeIndex, position);

			if (distance < LIMIT_DISTANCE)
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution

		}

		return density;
	}

	/**
	 * Get the index of the node that is the center of the ghosts.
	 * 
	 * @return index of the node that is the center of the ghosts.
	 */
	private int getGhostCenterIndex() {
		int bestNode = -1;
		double minDistanceSum = Double.MAX_VALUE;

		for (Ghost g1 : this.ghosts) {
			double currentDistanceSum = 0;
			for (Ghost g2 : this.ghosts)
				if (g1 != g2)
					currentDistanceSum += game.getEuclideanDistance(g1.currentNodeIndex, g2.currentNodeIndex);

			// Check if this node has the smallest total distance
			if (currentDistanceSum < minDistanceSum) {
				minDistanceSum = currentDistanceSum;
				bestNode = g1.currentNodeIndex;
			}
		}

		return bestNode;
	}

	/**
	 * Check if MsPacman is close to a power pill.
	 */
	private boolean isPacmanCloseToPowerPill() {
		for (int powerPill : game.getActivePowerPillsIndices())
			if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), powerPill,
					game.getPacmanLastMoveMade()) < LIMIT_DISTANCE)
				return true;

		return false;
	}

}