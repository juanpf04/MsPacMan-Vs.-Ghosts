package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;
import java.util.List;

public class MsPacMan extends PacmanController {

	private static final int RANGE = 200;
	private static final int PILL_VALUE = 10;
	private static final int VALUE_PATH_GHOST_NOT_EDIBLE = -1500;
	private static final int VALUE_PATH_GHOST_EDIBLE = +1000;
	private static final int VALUE_PER_NODE = 20;
	private static final int POWER_PILL_VALUE = 30;
	private static final int ALL_GHOSTS_OUTSIDE = 500;

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
	private Map<Integer, GHOST> ghostIndices;

	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");
		ghostIndices = new HashMap<Integer, GHOST>();
	}

	private void reset(Game game) {
		this.game = game;
		this.currentNode = this.game.getPacmanCurrentNodeIndex();
		this.lastMove = this.game.getPacmanLastMoveMade();
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		this.reset(game);

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
		return this.game.getPossibleMoves(this.currentNode, this.lastMove).length > 1;
	}

	/**
	 * Returns the best move for MsPacMan.
	 * 
	 * @return The best move for MsPacMan.
	 */
	private MOVE bestMove() {
		return this.bestPath(this.currentNode, this.lastMove, 3, null).startMove; // 1 = profundidad
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

			// calcular nuevas posiciones fantasmas
			// calcular nuevas posiciones pills y ppills

			PathInfo bestSecondPath = this.bestPath(0, null, depth - 1, null);

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
			endNode = this.game.getNeighbour(currentNode, currentMove);
			if (endNode == -1) {
				// solo va a haber un movimiento posible
				currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];
				endNode = this.game.getNeighbour(currentNode, currentMove);
			}
		}

		path.points += game.getShortestPathDistance(startNode, endNode, startMove) * VALUE_PER_NODE;
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
		int puntuacion = 0;

		Node n = game.getCurrentMaze().graph[node];

		if (ghostIndices.containsKey(node)) {
			puntuacion += VALUE_PATH_GHOST_NOT_EDIBLE;
		}
		if (n.pillIndex != -1) {
			puntuacion += PILL_VALUE;
		} else if (n.powerPillIndex != -1) {
			puntuacion += POWER_PILL_VALUE;
		}

		return puntuacion;
	}

}