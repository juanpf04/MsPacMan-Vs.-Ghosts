package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import pacman.controllers.PacmanController;
import pacman.game.Constants.*;
import pacman.game.Constants;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private static final int DEPTH = 4; // Best Depth: 4 for pills

	// Works as a struct to store the path information
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

	private int currentNode;
	private MOVE lastMove;

	private Map<Integer, MOVE> ghostsNodes;
	private Map<Integer, MOVE> eGhostsNodes;
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
		this.ghostsNodes = new HashMap<>();
		this.eGhostsNodes = new HashMap<>();
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
				MOVE ghostMove = this.game.getGhostLastMoveMade(ghost);

				if (this.game.isGhostEdible(ghost))
					this.eGhostsNodes.put(ghostNode, ghostMove);
				else
					this.ghostsNodes.put(ghostNode, ghostMove);
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
			PathInfo path = this.getPath(currentNode, move, depth);

			PathInfo bestNextPath = this.getBestPath(path.endNode, path.endMove, depth - 1);

			// Restore the pills and power pills
			this.pillsNodes = new HashSet<Integer>(pillsCopy);
			this.powerPillsNodes = new HashSet<Integer>(powerPillsCopy);

			// Check if the path is the best
			int points = path.points + bestNextPath.points;
			if (bestPath.startMove == MOVE.NEUTRAL || bestPath.points < points) {
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
	private PathInfo getPath(int startNode, MOVE startMove, int depth) {
		PathInfo path = new PathInfo(0, startNode, -1, startMove, null);

		int currentNode = startNode;
		MOVE currentMove = startMove;

		// Get the first node of the path
		int endNode = this.game.getNeighbour(startNode, startMove);

		// while the end node is not a junction
		while (!game.isJunction(endNode)) {
			// Add the points of the node to the path
			path.points += getNodePoints(endNode, depth);

			// Update the current node
			currentNode = endNode;

			// Since it's on a path, it only has one possible move, but we check the move it
			// has to make in case it's going to hit a wall
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];

			// Get the next node
			endNode = this.game.getNeighbour(currentNode, currentMove);

		}

		// Update the path
		path.points += this.getGhostPoints(path, depth);
		path.endNode = endNode;
		path.endMove = this.game.getMoveToMakeToReachDirectNeighbour(currentNode, endNode);

		return path;
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
	private int getNodePoints(int node, int depth) {
		if (this.pillsNodes.remove(node))
			return Constants.PILL + depth; // more early the pill, more points

		if (this.powerPillsNodes.remove(node))
			return (Constants.POWER_PILL + depth)
					- ((Constants.POWER_PILL + depth) * (Constants.NUM_GHOSTS - this.ghostsNodes.size()));

		return 0;
	}

	/**
	 * Returns the points of a path based on the ghosts.
	 * 
	 * @param path  The path information.
	 * @param depth The depth of the search.
	 * @return The points of the path.
	 */
	private int getGhostPoints(PathInfo path, int depth) { // TODO: IMPLEMENTAR
		int endNode = path.endNode;
		// int msPacManDistance = this.game.getShortestPathDistance(path.startNode,
		// endNode, path.startMove); // peta no se por que

//		for (Entry<Integer, MOVE> ghost : this.ghostsNodes.entrySet()) {
//			int ghostDisntance = this.game.getShortestPathDistance(ghost.getKey(), endNode, ghost.getValue());
//			if (ghostDisntance < msPacManDistance) {
//
//			}
//		}

		return 0;
	}

}