package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import pacman.controllers.PacmanController;
import pacman.game.Constants.*;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.internal.Ghost;

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

	private int eatenGhosts;

	// Data structures to store the game state

	private Map<Integer, Ghost> ghostsNodes;
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

		this.eatenGhosts = this.game.getNumGhostsEaten();

		this.ghostsNodes.clear();
		for (GHOST ghost : GHOST.values()) {
			if (this.game.getGhostLairTime(ghost) <= 0) {
				int ghostNode = this.game.getGhostCurrentNodeIndex(ghost);
				MOVE ghostMove = this.game.getGhostLastMoveMade(ghost);
				int edibleTime = this.game.getGhostEdibleTime(ghost);
				int lairTime = this.game.getGhostLairTime(ghost);

				this.ghostsNodes.put(ghostNode, new Ghost(ghost, ghostNode, edibleTime, lairTime, ghostMove));
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

		// Make a copy of the game state
		Set<Integer> pillsCopy = new HashSet<>(this.pillsNodes);
		Set<Integer> powerPillsCopy = new HashSet<>(this.powerPillsNodes);
		Map<Integer, Ghost> ghostsCopy = new HashMap<>(this.ghostsNodes);

		int eatenGhostsCopy = this.eatenGhosts;

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
			this.ghostsNodes = new HashMap<>(ghostsCopy);

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
		int endNode = path.endNode;

		// Get the next node
		endNode = this.game.getNeighbour(currentNode, currentMove);

		this.moveGhosts();

		// while the end node is not a junction
		while (!game.isJunction(endNode)) {

			// Add the points of the node to the path
			int nodePoints = this.getNodePoints(endNode, depth);

			if (nodePoints < 0) {
				path.endMove = null;
				return path;
			}

			path.points += nodePoints;

			// Update the current node
			currentNode = endNode;

			// Since it's on a path, it always has only one possible move, but we check the
			// move just in case it's going to hit a wall
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];

			endNode = this.game.getNeighbour(currentNode, currentMove);

			this.moveGhosts();
		}

		path.points += this.getNodePoints(endNode, depth);

		// Update the path
		path.endNode = endNode;
		path.endMove = this.game.getMoveToMakeToReachDirectNeighbour(currentNode, endNode);

		return path;
	}

	private int getNodePoints(int node, int depth) {
		return this.getPillsPoints(node, depth) + this.getGhostPoints(node, depth);
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

		int nGhosts = (int) this.ghostsNodes.values().stream().filter(ghost -> ghost.edibleTime == 0).count();
		if (this.powerPillsNodes.remove(node))
			return (Constants.POWER_PILL + depth) - ((Constants.POWER_PILL + depth) * (Constants.NUM_GHOSTS - nGhosts));

		return 0;
	}

	/**
	 * Returns the points of a path based on the ghosts.
	 * 
	 * @param path  The path information.
	 * @param depth The depth of the search.
	 * @return The points of the path.
	 */
	private int getGhostPoints(int node, int depth) { // TODO: IMPLEMENTAR
		int points = 0;

		for (Ghost ghost : this.ghostsNodes.values()) {
			if (this.isCloseEnought(node, ghost.currentNodeIndex)) {
				if (ghost.edibleTime == 0)
					points -= Constants.GHOST_EAT_SCORE * depth;
				else
					points += Constants.GHOST_EAT_SCORE * ++this.eatenGhosts;

			}
		}

		return points;
	}

	private boolean isCloseEnought(int node1, int node2) {
		return this.game.getShortestPathDistance(node1, node2) <= Constants.EAT_DISTANCE;
	}

	/**
	 * Moves the ghosts.
	 */
	private void moveGhosts() {
		for (Ghost ghost : this.ghostsNodes.values()) {
			if (ghost.lairTime == 0) {
				if (ghost.edibleTime == 0 || ghost.edibleTime % Constants.GHOST_SPEED_REDUCTION != 0) {
					int oldNode = ghost.currentNodeIndex;

					if (this.game.isJunction(oldNode)) {
						// si esta en interseccion, cambiar de direccion a la mas desfavorable al pacman
					}

					// TODO: mover fantasma
				}
			}
		}
	}

}