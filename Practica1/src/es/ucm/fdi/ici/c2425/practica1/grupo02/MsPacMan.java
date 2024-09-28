package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.Set;
import java.util.HashSet;

import pacman.controllers.PacmanController;
import pacman.game.Constants.*;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.internal.Node;

public class MsPacMan extends PacmanController {

	private static final int VALUE_PATH_GHOST_NOT_EDIBLE = -1500;
	private static final int VALUE_PATH_GHOST_EDIBLE = +1000;
	private static final int VALUE_PER_NODE = 20;
	private static final int DEPTH = 3;

	public class PathInfo {
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
	private int currentLevel;
	private int eatMultiplier;

	private Set<Integer> ghostsNodes;
	private Set<Integer> eGhostsNodes;
	private Set<Integer> pillsNodes;
	private Set<Integer> ppillsNodes;

	// TODO: implementar marcadores de posiciones fantasmas, pills y ppills

	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");

		// Initialize variables
		this.game = null;
		this.currentLevel = -1;
		this.eatMultiplier = 1;
		this.ghostsNodes = new HashSet<>();
		this.eGhostsNodes = new HashSet<>();
		this.pillsNodes = new HashSet<>();
		this.ppillsNodes = new HashSet<>();
	}

	/**
	 * Updates the game state and variables if needed.
	 * 
	 * @param game The game state.
	 */
	private void update(Game game) {
		if (this.game == null)
			this.game = game;

		this.currentNode = this.game.getPacmanCurrentNodeIndex();
		this.lastMove = this.game.getPacmanLastMoveMade();

		for (GHOST ghost : GHOST.values()) {
			if (this.game.getGhostLairTime(ghost) <= 0) {
				int ghostNode = this.game.getGhostCurrentNodeIndex(ghost);

				if (this.game.isGhostEdible(ghost))
					this.eGhostsNodes.add(ghostNode);
				else
					this.ghostsNodes.add(ghostNode);
			}
		}

		if (this.currentLevel != this.game.getCurrentLevel()) {
			this.currentLevel = this.game.getCurrentLevel();

			int pills[] = this.game.getActivePillsIndices();
			for (int pill : pills)
				this.pillsNodes.add(pill);

			int ppills[] = this.game.getActivePowerPillsIndices();
			for (int ppill : ppills)
				this.pillsNodes.add(ppill);
		}
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		this.update(game);

		if (this.doesMsPacManRequireAction())
			return this.bestMove();

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
	private MOVE bestMove() {
		return this.bestPath(this.currentNode, this.lastMove, DEPTH, null).startMove;
	}

	/**
	 * Returns the best path for MsPacMan.
	 * 
	 * @param currentNode  The current node index.
	 * @param lastMove     The last move.
	 * @param depth        The depth of the search.
	 * @param currentState The current state of the game.
	 * @return The best path for MsPacMan.
	 */
	private PathInfo bestPath(int currentNode, MOVE lastMove, int depth, Object currentState) {
		PathInfo bestPath = new PathInfo();

		if (depth == 0)
			return bestPath;

		MOVE[] possibleMoves = this.game.getPossibleMoves(currentNode, lastMove);

		for (MOVE move : possibleMoves) {
			PathInfo path = this.getPath(currentNode, move);

			// TODO: marcar nuevas posiciones fantasmas
			// TODO: marcar nuevas posiciones pills y ppills

			PathInfo bestSecondPath = this.bestPath(0, null, depth - 1, null);

			// TODO: desmarcar nuevas posiciones fantasmas
			// TODO: desmarcar nuevas posiciones pills y ppills

			int points = path.points + bestSecondPath.points;

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

		int endNode = this.game.getNeighbour(startNode, currentMove);

		while (!game.isJunction(endNode)) {
			path.points += getNodePoints(endNode);

			currentNode = endNode;
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];
			endNode = this.game.getNeighbour(currentNode, currentMove);

		}

		// path.points += game.getShortestPathDistance(startNode, endNode, startMove) *
		// VALUE_PER_NODE; // TODO: revisar
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
	private int getNodePoints(int node) { // FIXME: implement
		int points = 0;

		if (this.ghostsNodes.contains(node))
			points += VALUE_PATH_GHOST_NOT_EDIBLE;
		if (this.eGhostsNodes.contains(node))
			points += ++this.eatMultiplier * Constants.GHOST_EAT_SCORE;

		if (this.pillsNodes.contains(node))
			points += Constants.PILL;
		else if (this.ppillsNodes.contains(node))
			points += Constants.POWER_PILL;

		return points;
	}

}