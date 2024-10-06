package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import pacman.controllers.PacmanController;
import pacman.game.Constants.*;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.internal.Ghost;

public class MsPacMan extends PacmanController {

	private static final int DEPTH = 3; // Best Depth: 4 for pills

	/*
	 * Data structure to hold all information pertaining to the ghosts.
	 */
	private class PathInfo {
		public int points;
		public int startNode;
		public int endNode;
		public MOVE startMove;
		public MOVE endMove;

		public PathInfo() {
			this(0, -1, -1, MOVE.NEUTRAL, MOVE.NEUTRAL);
		}

		public PathInfo(int points, int startNode, int endNode, MOVE startMove, MOVE endMove) {
			this.points = points;
			this.startNode = startNode;
			this.endNode = endNode;
			this.startMove = startMove;
			this.endMove = endMove;
		}
	}

	private Game game;

	private int level;

	private int currentNode;
	private MOVE lastMove;

	private int eatenGhosts;

	// Data structures to store the game state

	private List<Ghost> ghosts;
	private Set<Integer> pillsNodes;
	private Set<Integer> powerPillsNodes;

	/**
	 * Constructor for MsPacMan.
	 */
	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");

		// Initialize variables
		this.game = null;
		this.ghosts = new ArrayList<>();
		for (GHOST ghost : GHOST.values()) {
			this.ghosts.add(new Ghost(ghost, -1, 0, 0, MOVE.NEUTRAL));
		}

		this.pillsNodes = new HashSet<>();
		this.powerPillsNodes = new HashSet<>();

	}

	/**
	 * Updates the game state and variables.
	 * 
	 * @param game The game state.
	 */
	private void update(Game game) {
		this.game = game;

		this.level = this.game.getCurrentLevel();

		// Update MsPacMan

		this.currentNode = this.game.getPacmanCurrentNodeIndex();
		this.lastMove = this.game.getPacmanLastMoveMade();

		// Update Ghosts

		this.eatenGhosts = this.game.getNumGhostsEaten();

		for (Ghost ghost : this.ghosts) {
			GHOST type = ghost.type;
			ghost.currentNodeIndex = this.game.getGhostCurrentNodeIndex(type);
			ghost.lastMoveMade = this.game.getGhostLastMoveMade(type);
			ghost.edibleTime = this.game.getGhostEdibleTime(type);
			ghost.lairTime = this.game.getGhostLairTime(type);
		}

		// Update Pills and Power Pills

		this.pillsNodes.clear();
		int pills[] = this.game.getActivePillsIndices();
		for (int pill : pills)
			this.pillsNodes.add(pill);

		this.powerPillsNodes.clear();
		int ppills[] = this.game.getActivePowerPillsIndices();
		for (int ppill : ppills)
			this.powerPillsNodes.add(ppill);

	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		this.update(game);

		if (this.doesMsPacManRequireAction())
			return this.getBestMove();

		return MOVE.NEUTRAL;
	}

	/**
	 * Checks if MsPacMan needs to make a decision. Check if MsPacMan is in a
	 * junction (has more than one possible move)
	 * 
	 * @return True if needs to make a decision, false otherwise.
	 */
	private boolean doesMsPacManRequireAction() {
		return this.game.getPossibleMoves(this.currentNode, this.lastMove).length > 1;
	}

	/**
	 * Returns the best move for MsPacMan.
	 * 
	 * @return The best move for MsPacMan.
	 */
	private MOVE getBestMove() {
		return this.getBestPath(this.currentNode, this.lastMove, DEPTH).startMove;
	}

	/**
	 * Returns the best path for MsPacMan.
	 * 
	 * @param currentNode The current node index.
	 * @param lastMove    The last move.
	 * @param depth       The depth of the search (number of paths to check).
	 * @return The best path for MsPacMan.
	 */
	private PathInfo getBestPath(int currentNode, MOVE lastMove, int depth) { // TODO: revisar caso base
		PathInfo bestPath = new PathInfo();

		if (depth == 0) { // If there are no more paths to check
			//bestPath.points = (int) (-100 * this.getGhostDensity(currentNode));
			return bestPath;
		}

		// Make a copy of the game state
		Set<Integer> pillsCopy = new HashSet<>(this.pillsNodes);
		Set<Integer> powerPillsCopy = new HashSet<>(this.powerPillsNodes);
		List<Ghost> ghostsCopy = new ArrayList<>(this.ghosts);

		int eatenGhostsCopy = this.eatenGhosts;

		// Get the possible moves
		MOVE[] possibleMoves = this.game.getPossibleMoves(currentNode, lastMove);

		// for each possible move check the path and the best next path
		for (MOVE move : possibleMoves) {
			PathInfo path = this.getPath(currentNode, move, depth);

			int points = path.points;

			// OPTIMIZATION: If the path is not the best, don't check the next path
			if (path.endMove != null) {
				PathInfo bestNextPath = this.getBestPath(path.endNode, path.endMove, depth - 1);
				points += bestNextPath.points;
			}

			// Check if the path is the best
			if (bestPath.startMove == MOVE.NEUTRAL || bestPath.points < points) {
				bestPath.startMove = move;
				bestPath.points = points;
			}

			// Restore the game state
			this.pillsNodes = new HashSet<>(pillsCopy);
			this.powerPillsNodes = new HashSet<>(powerPillsCopy);
			this.ghosts = new ArrayList<>(ghostsCopy);

			this.eatenGhosts = eatenGhostsCopy;
		}

		return bestPath;
	}

	/**
	 * Returns the path for MsPacMan.
	 * 
	 * @param startNode The start node index.
	 * @param startMove The start move.
	 * @param depth     The depth of the search.
	 * @return The path for MsPacMan.
	 */
	private PathInfo getPath(int startNode, MOVE startMove, int depth) {
		PathInfo path = new PathInfo(0, startNode, -1, startMove, null);

		int currentNode = path.startNode;
		MOVE currentMove = path.startMove;

		// Get the next node
		int endNode = this.game.getNeighbour(currentNode, currentMove);

		this.updateGhosts(endNode);

		int nodePoints = this.getNodePoints(endNode, depth);

		path.points += nodePoints;

		if (nodePoints < 0)
			return path;

		// while the end node is not a junction
		while (!game.isJunction(endNode)) {

			// Update the current node
			currentNode = endNode;

			// Since it's on a path, it always has only one possible move, but we check the
			// move just in case it's going to hit a wall
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];

			endNode = this.game.getNeighbour(currentNode, currentMove);

			this.updateGhosts(endNode);

			nodePoints = this.getNodePoints(endNode, depth);

			path.points += nodePoints;

			if (nodePoints < 0)
				return path;
		}

		// Update the path
		path.endNode = endNode;
		path.endMove = currentMove;

		return path;
	}

	/**
	 * Returns the points of a node. Check if the node is a pill or a power pill,
	 * and if it's close enough to a ghost.
	 * 
	 * @param node  The node index.
	 * @param depth The depth of the search.
	 * @return The points of a node.
	 */
	private int getNodePoints(int node, int depth) {
		int ghostPoints = this.getGhostPoints(node, depth);

		if (ghostPoints < 0)
			return ghostPoints;

		return ghostPoints + this.getPillsPoints(node, depth);
	}

	/**
	 * Returns the points of a node. Check if the node is a pill or a power pill
	 * remove it from the set if contained to avoid counting it again and return the
	 * weighting points based on the depth of the search.
	 * 
	 * @param node  The node index.
	 * @param depth The depth of the search.
	 * @return The points of a node.
	 */
	private int getPillsPoints(int node, int depth) { // TODO: añadir ppills ponderen distancia a fantasmas
		if (this.pillsNodes.remove(node))
			return Constants.PILL + depth; // more early the pill, more points

		int nGhosts = (int) this.ghosts.stream().filter(ghost -> ghost.edibleTime == 0).count();
		if (this.powerPillsNodes.remove(node)) {
			// update Ghosts edible time
			int newEdibleTime = (int) (Constants.EDIBLE_TIME
					* (Math.pow(Constants.EDIBLE_TIME_REDUCTION, this.level % Constants.LEVEL_RESET_REDUCTION)));

			for (Ghost ghost : this.ghosts) {
				if (ghost.lairTime == 0) {
					ghost.edibleTime = newEdibleTime;
				} else {
					ghost.edibleTime = 0;
				}
			}

			return (Constants.POWER_PILL + depth) - ((Constants.POWER_PILL + depth) * (Constants.NUM_GHOSTS - nGhosts));
		}

		return 0;
	}

	/**
	 * Returns the points of a node based on the ghosts.
	 * 
	 * @param node  The node index.
	 * @param depth The depth of the search.
	 * @return The points of the node.
	 */
	private int getGhostPoints(int node, int depth) {

		for (Ghost ghost : this.ghosts) {

			if (ghost.lairTime == 0 && this.isCloseEnough(node, ghost.currentNodeIndex)) {

				if (ghost.edibleTime == 0)
					return -Constants.GHOST_EAT_SCORE * depth;
				else {
					ghost.edibleTime = 0;
					ghost.lairTime = (int) (ghost.type.initialLairTime
							* (Math.pow(Constants.LAIR_REDUCTION, this.level % Constants.LEVEL_RESET_REDUCTION)));
					ghost.lastMoveMade = MOVE.NEUTRAL;
					return (Constants.GHOST_EAT_SCORE * ++this.eatenGhosts) + depth;
				}
			}
		}

		return 0;
	}

	/**
	 * Checks if two nodes are close enough.
	 * 
	 * @param node1 The first node index.
	 * @param node2 The second node index.
	 * @return True if the nodes are close enough, false otherwise.
	 */
	private boolean isCloseEnough(int node1, int node2) {
		return this.game.getShortestPathDistance(node1, node2) <= Constants.EAT_DISTANCE;
	}

	/**
	 * Update the ghosts.
	 */
	private void updateGhosts(int node) { // TODO: terminar

		for (Ghost ghost : this.ghosts) {
			if (ghost.lairTime == 0) {
				if (ghost.edibleTime == 0 || ghost.edibleTime % Constants.GHOST_SPEED_REDUCTION != 0) {

					if (this.game.isJunction(ghost.currentNodeIndex)) {
						// si esta en interseccion, cambiar de direccion a la mas desfavorable al pacman
						if (ghost.edibleTime == 0) {
							ghost.lastMoveMade = this.game.getApproximateNextMoveTowardsTarget(ghost.currentNodeIndex,
									this.currentNode, ghost.lastMoveMade, DM.PATH);
						} else {
							ghost.lastMoveMade = this.game.getApproximateNextMoveAwayFromTarget(ghost.currentNodeIndex,
									node, ghost.lastMoveMade, DM.PATH);
						}

					} else {
						ghost.lastMoveMade = this.game.getPossibleMoves(ghost.currentNodeIndex, ghost.lastMoveMade)[0];
					}

					ghost.currentNodeIndex = this.game.getNeighbour(ghost.currentNodeIndex, ghost.lastMoveMade);
				}
			}

			// Update the edible time
			if (ghost.edibleTime > 0)
				ghost.edibleTime--;

			// Update the lair time
			if (ghost.lairTime > 0) {
				if (--ghost.lairTime == 0) {
					ghost.currentNodeIndex = this.game.getCurrentMaze().initialGhostNodeIndex;
				}
			}
		}
	}

	/**
	 * Calculates ghost density at a certain node using an exponential density
	 * function. Range is between 0 and 1, where 1 is high density
	 * 
	 * @param node node at which we want to calculate the ghost density at
	 * @return numerical value for ghost density
	 */
	private double getGhostDensity(int node) {
		double density = 0.0;
		for (Ghost ghost : this.ghosts) {
			double distance = this.game.getEuclideanDistance(ghost.currentNodeIndex, node);
			if (distance < 25) {
				density += Math.exp(-0.1 * distance); // Exponentially decaying contribution
			}
		}
		return density;
	}

}