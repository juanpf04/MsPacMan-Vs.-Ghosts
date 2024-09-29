package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.Set;
import java.util.HashSet;

import pacman.controllers.PacmanController;
import pacman.game.Constants.*;
import pacman.game.Constants;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private static final int VALUE_PATH_GHOST_NOT_EDIBLE = -1500;
	private static final int VALUE_PER_NODE = 20;
	private static final int DEPTH = 3;

	// Works as a struct to store the path information
	private class PathInfo {
		public int points;
		public int startNode;
		public int endNode;
		public MOVE startMove;
		public MOVE endMove;

		public PathInfo() {
			this(0, -1, -1, null, null);
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

	private int currentNode;
	private MOVE lastMove;
	private int eatMultiplier;

	private Set<Integer> ghostsNodes;
	private Set<Integer> eGhostsNodes;
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
		this.eatMultiplier = 1;
		this.ghostsNodes = new HashSet<>();
		this.eGhostsNodes = new HashSet<>();
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

		// Update MsPacMan

		this.currentNode = this.game.getPacmanCurrentNodeIndex();
		this.lastMove = this.game.getPacmanLastMoveMade();

		// Update Ghosts

		this.ghostsNodes.clear();
		this.eGhostsNodes.clear();
		for (GHOST ghost : GHOST.values()) {
			if (this.game.getGhostLairTime(ghost) <= 0) {
				int ghostNode = this.game.getGhostCurrentNodeIndex(ghost);

				if (this.game.isGhostEdible(ghost))
					this.eGhostsNodes.add(ghostNode);
				else
					this.ghostsNodes.add(ghostNode);
			}
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
	 * Checks if MsPacMan needs to make a decision.
	 * 
	 * @return True if needs to make a decision, false otherwise.
	 */
	private boolean doesMsPacManRequireAction() {
		// Check if MsPacMan is in a junction (has more than one possible move)
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
	private PathInfo getBestPath(int currentNode, MOVE lastMove, int depth) {
		PathInfo bestPath = new PathInfo();

		if (depth == 0) // If there are no more paths to check
			return bestPath;

		// Get the possible moves
		MOVE[] possibleMoves = this.game.getPossibleMoves(currentNode, lastMove);

		// Make a copy of the pills and power pills
		Set<Integer> pillsCopy = new HashSet<>(this.pillsNodes);
		Set<Integer> powerPillsCopy = new HashSet<>(this.powerPillsNodes);

		// for each possible move check the path and the best next path
		for (MOVE move : possibleMoves) {
			PathInfo path = this.getPath(currentNode, move);

			PathInfo bestNextPath = this.getBestPath(path.endNode, path.endMove, depth - 1);

			// Restore the pills and power pills
			this.pillsNodes = new HashSet<Integer>(pillsCopy);
			this.powerPillsNodes = new HashSet<Integer>(powerPillsCopy);

			// Check if the path is the best
			int points = path.points + bestNextPath.points;
			if (bestPath.startMove == null || bestPath.points < points) {
				bestPath.startMove = move;
				bestPath.points = points;
			}

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
	private PathInfo getPath(int startNode, MOVE startMove) {
		PathInfo path = new PathInfo(0, startNode, -1, startMove, null);

		int currentNode = startNode;
		MOVE currentMove = startMove;

		// Get the first node of the path
		int endNode = this.game.getNeighbour(startNode, startMove);

		// while the end node is not a junction
		while (!game.isJunction(endNode)) {
			// Add the points of the node to the path
			path.points += getNodePoints(endNode);

			// Update the current node
			currentNode = endNode;

			// Since it's on a path, it only has one possible move, but we check the move it
			// has to make in case it's going to hit a wall
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];

			// Get the next node
			endNode = this.game.getNeighbour(currentNode, currentMove);

		}

		// Update the path
		// path.points += game.getShortestPathDistance(startNode, endNode, currentMove)
		// * VALUE_PER_NODE; // TODO: revisar
		path.endNode = endNode;
		path.endMove = this.game.getMoveToMakeToReachDirectNeighbour(currentNode, endNode);

		return path;
	}

	/**
	 * Returns the points of a node.
	 * 
	 * @param node The node index.
	 * @return The points of a node.
	 */
	private int getNodePoints(int node) {
		int points = 0;

//		if (this.ghostsNodes.contains(node)) // FIXME: revisar
//			points += VALUE_PATH_GHOST_NOT_EDIBLE;
//
//		if (this.eGhostsNodes.contains(node))
//			points += ++this.eatMultiplier * Constants.GHOST_EAT_SCORE;

		// Check if the node is a pill or a power pill
		// Remove it from the set of pills or power pills if contained
		// to avoid counting it again
		// Update the points

		if (this.pillsNodes.remove(node))
			points += Constants.PILL;

		else if (this.powerPillsNodes.remove(node))
			points += Constants.POWER_PILL;

		return points;
	}

}