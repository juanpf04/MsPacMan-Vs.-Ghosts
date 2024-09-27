package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;
import java.util.ArrayList;
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

		public PathInfo(int points, int startNode, int endNode, MOVE startMove, MOVE endMove) {
			this.points = points;
			this.startNode = startNode;
			this.endNode = endNode;
			this.startMove = startMove;
			this.endMove = endMove;
		}
	}

	private Game game;
	private int node;
	private Map<Integer, GHOST> ghostIndices;

	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");
		ghostIndices = new HashMap<Integer, GHOST>();
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		this.reset(game);

		if (this.game.isJunction(this.node))
			return this.bestMove();

		return MOVE.NEUTRAL;
	}

	private void reset(Game game) {
		this.game = game;
		this.node = this.game.getPacmanCurrentNodeIndex();

	}

	private MOVE bestMove() {
		return this.bestPath(this.node, this.game.getPacmanLastMoveMade(), 3, null).startMove;
	}

	private PathInfo bestPath(int currentNode, MOVE lastMove, int count, Object currentState) {
		PathInfo bestPath = new PathInfo(0, currentNode, -1, null, null);

		if (count == 0)
			return bestPath;

		MOVE[] possibleMoves = this.game.getPossibleMoves(currentNode, lastMove);

		for (MOVE move : possibleMoves) { // cada Movimiento

			// calcular puntos y nodo siguiente inters y lastMove
			PathInfo path = this.pathJP(currentNode, lastMove, null, count);
			// calcular nuevas posiciones fantasmas
			// calcular nuevas posiciones pills y ppills

			PathInfo bestSecondPath = this.bestPath(0, null, count - 1, null);

			int points = path.points + bestSecondPath.points;

			if (bestPath.startMove == null || bestPath.points < points) {
				bestPath.startMove = move;
				bestPath.points = points;
			}

		}

		return bestPath;
	}

	// Pasar a clase general. Se encarga de iniciar los caminos posibles tras el
	// estudio de los mismos desde el nodo de intersecci�n en el que estamos
	private TreeMap<Integer, MOVE> initializePaths(List<Integer> nodes, Map<Integer, Variables> nodeMap,
			int currentNode, int iteracion, MOVE lastMode, int puntuacion, TreeMap<Integer, MOVE> sol) {

		EnumMap<MOVE, Integer> movements = game.getCurrentMaze().graph[currentNode].allNeighbourhoods.get(lastMode);

		if (iteracion > 3) {
			return sol;
		}

		if (movements != null && iteracion <= 3) {
			for (Map.Entry<MOVE, Integer> entry : movements.entrySet()) {

				sol.putAll(path(entry.getValue(), entry.getKey(), nodes, puntuacion));

				initializePaths(nodes, nodeMap, nodes.get(nodes.size() - 1), iteracion + 1,
						lastMove(nodes.get(nodes.size() - 1), nodes.get(nodes.size() - 2)), puntuacion, sol);
			}
		}

		return sol;
	}

	// Guardamos en un mapa el nodo de cada fantasma.
	private void ghostIndices() {

		for (GHOST g : GHOST.values()) {
			int index = game.getGhostCurrentNodeIndex(g);

			if (ghostIndices.get(index) == null) {
				ghostIndices.put(index, g);
			}
		}
	}

	// Comprobar si todos los fantasmas est�n fuera de su guarida
	private boolean ghostsOutOfHiding() {
		int nGhosts = 0;

		for (GHOST g : GHOST.values()) {
			if (game.getGhostCurrentNodeIndex(g) != game.getGhostInitialNodeIndex()) {
				nGhosts++;
			}
		}

		return nGhosts == 4;
	}

	private PathInfo pathJP(int node, MOVE m, List<Integer> nodes, int puntuacion) {
		PathInfo sol = new PathInfo(0, node, -1, null, null);

		if (nodes == null)
			nodes = new ArrayList<>();

		EnumMap<MOVE, Integer> move = game.getCurrentMaze().graph[node].allNeighbourhoods.get(m);

		Integer index = -1;
		while (move.containsKey(m) && !game.isJunction(move.get(m))) {
			index = move.get(m);

			puntuacion += analyzeNode(node);

			nodes.add(node);

			move = game.getCurrentMaze().graph[index].allNeighbourhoods.get(m);
		}

		puntuacion += game.getShortestPathDistance(node, nodes.get(nodes.size() - 1),
				lastMove(nodes.get(nodes.size() - 1), nodes.get(nodes.size() - 2))) * VALUE_PER_NODE;

		sol.points = puntuacion;
		sol.startMove = m;

		return sol;
	}

	private Map<Integer, MOVE> path(int node, MOVE m, List<Integer> nodes, int puntuacion) {
		Map<Integer, MOVE> sol = new HashMap<>();

		// Si la lista de nodos esta vacia (primera llamada) la iniciamos
		if (nodes == null)
			nodes = new ArrayList<>();

		// Calculamos el nodo correspondiente al movimiento especificado
		EnumMap<MOVE, Integer> move = game.getCurrentMaze().graph[node].allNeighbourhoods.get(m);

		Integer index = -1;
		// Recorrer todos los caminos hasta el siguiente nodo de interseccion
		while (move.containsKey(m) && !game.isJunction(move.get(m))) {
			index = move.get(m);

			// Analiza cada nodo del camino y suma la puntuacion
			puntuacion += analyzeNode(node);

			// A�ade el nodo al array de nodos, para saber el ultimo nodo antes del actual y
			// calcular el lastMove
			nodes.add(node);

			// Avanzar nodo
			move = game.getCurrentMaze().graph[index].allNeighbourhoods.get(m);
		}

		// Una vez que hemos recorrido todo el camino hasta el nodo de interseccion
		// sumamos la puntuacion relativa a la distancia del camino
		puntuacion += game.getShortestPathDistance(node, nodes.get(nodes.size() - 1),
				lastMove(nodes.get(nodes.size() - 1), nodes.get(nodes.size() - 2))) * VALUE_PER_NODE;

		// A�adir la puntuacoin obtenida en este camino junto al move original del que
		// proviene
		sol.put(puntuacion, m);

		return sol;
	}

	private MOVE lastMove(int currentIndex, int lastIndex) {
		int diferencia = currentIndex - lastIndex;

		switch (diferencia) {
		case 1:
			return MOVE.RIGHT;
		case -1:
			return MOVE.LEFT;
		default:
			if (diferencia > 0) {
				return MOVE.UP;
			} else if (diferencia < 0) {
				return MOVE.DOWN;
			} else
				return MOVE.NEUTRAL;
		}
	}

	private int analyzeNode(int node) {
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

	// Estudiamos el camino
	private Variables studyPath(List<Integer> nodes, int initialNode) {
		int i = 0;
		Variables v = new Variables(0, 0);

		while (i < nodes.size() && !game.isJunction(game.getCurrentMaze().graph[nodes.get(i)].numNeighbouringNodes)) {
			Node nodo = game.getCurrentMaze().graph[nodes.get(i)];

			if (ghostIndices.containsKey(i) && game.getDistance(initialNode, nodes.get(i), DM.MANHATTAN) <= RANGE) {
				v.setNearestGhosts(ghostIndices.get(i));
			}
			if (nodo.pillIndex != -1) {
				v.setNumberOfPills(v.getNumberOfPills() + 1);
			} else if (nodo.powerPillIndex != -1) {
				v.activatePowerPill(true);
			}

			i++;
		}

		v.setDistanceFromNearestIndex(
				game.getDistance(initialNode, nodes.get(i < nodes.size() ? i : i - 1), DM.MANHATTAN));

		return v;
	}

	// Evaluamos cual es el camino mas eficiente mediante un sistema de rangos
	// n�mericos. Dependiendo de las situaciones que se den se suman/restan puntos
	private MOVE analyzeBestPath(Map<MOVE, Variables> caminos, Game game) {
		int puntos_camino = 0;
		TreeMap<Integer, MOVE> mejor_camino = new TreeMap<Integer, MOVE>();

		for (Map.Entry<MOVE, Variables> entry : caminos.entrySet()) {
			Variables camino = entry.getValue();

			if (camino.getNumberOfGhosts() > 0) {
				for (GHOST g : camino.getNearestGhosts()) {
					if (!game.isGhostEdible(g))
						puntos_camino += VALUE_PATH_GHOST_NOT_EDIBLE;
					else
						puntos_camino += VALUE_PATH_GHOST_EDIBLE;
				}
			}

			if (camino.powerPill() && camino.getNumberOfGhosts() > 0) {
				puntos_camino += POWER_PILL_VALUE;

				if (ghostsOutOfHiding()) {
					puntos_camino += ALL_GHOSTS_OUTSIDE;
				}
			} else if (camino.powerPill()) {
				puntos_camino += POWER_PILL_VALUE * -100;
			}

			puntos_camino += camino.getDistanceFromNearestIndex() * VALUE_PER_NODE;
			puntos_camino += camino.getNumberOfPills() * PILL_VALUE;

			mejor_camino.put(puntos_camino, entry.getKey());
			puntos_camino = 0;
		}

		return mejor_camino.lastEntry().getValue();
	}

}