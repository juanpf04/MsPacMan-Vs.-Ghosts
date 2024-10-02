package es.ucm.fdi.ici.c2425.practica1.grupo02;

import java.util.EnumMap;
import java.util.HashSet;

import java.util.Set;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public final class Ghosts extends GhostController {

	private final static int LIMIT_DISTANCE = 60;
	private final static int LIMIT_DISTANCE_BETWEEN_PACMAN_PPILL = 30;
	private static final int VALUE_PER_NODE = 20;
	private static final int DEPTH = 3;
	private final static int PACMAN = -10000;
	
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
	
	
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private Game game;
	
	private int pacmanNode;
	private Set<Integer> ppillsNodes;
	private boolean[] nodos; //MARCA
	
	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");
		
		this.ppillsNodes = new HashSet<>();
	}

	private void update(Game game) {
		this.game = game;
		this.pacmanNode = game.getPacmanCurrentNodeIndex();
		
		int ppills[] = game.getActivePowerPillsIndices();
		for (int ppill : ppills)
			this.ppillsNodes.add(ppill);
		
	}
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		this.update(game);
		
		
		for(GHOST g: GHOST.values()) {
			int ghostNode = game.getGhostCurrentNodeIndex(g);
			MOVE lastMove = game.getGhostLastMoveMade(g);
			
			if(game.doesGhostRequireAction(g)) {
				
				if(game.isGhostEdible(g)) {
					moves.put(g, this.runAwayFromPacman(ghostNode, lastMove));
				}
				else {
					
					if(danger(game,ghostNode, pacmanNode)) {
						moves.put(g, this.runAwayFromPacman(ghostNode, lastMove));
					}
					else {
						if(game.getShortestPathDistance(pacmanNode, ghostNode) <= LIMIT_DISTANCE)
							moves.put(g, this.chasePacman(ghostNode, lastMove));
						else
							moves.put(g, game.getApproximateNextMoveTowardsTarget(ghostNode, pacmanNode, lastMove, DM.PATH));
					}
				}
			}
		}
		
		return moves;
	}
	
	private boolean danger(Game game, int ghostNode, int pacmanNode) {
		return pacmanNearToPowerPill(pacmanNode, game);
	}
	
	private boolean pacmanNearToPowerPill(int pacmanNode, Game game) {
		
		for(int i: game.getActivePowerPillsIndices())
			if(game.getShortestPathDistance(pacmanNode, i) <= LIMIT_DISTANCE_BETWEEN_PACMAN_PPILL)
				return true;
		
		return false;
	}

	
	private MOVE runAwayFromPacman(int currentNode, MOVE lastMove) {
		return bestPath(currentNode, lastMove, DEPTH, null, true).startMove;
	}
	
	private MOVE chasePacman(int currentNode, MOVE lastMove) {
		return bestPath(currentNode, lastMove, DEPTH, null, false).startMove;
	}
	
	private PathInfo bestPath(int currentNode, MOVE lastMove, int depth, Object currentState, boolean state) {
		PathInfo bestPath = new PathInfo();

		if (depth == 0)
			return bestPath;

		MOVE[] possibleMoves = this.game.getPossibleMoves(currentNode, lastMove);

		//MARCAR SI UN CAMINO YA HA SIDO TOMADO POR UN FANTASMA
		
		for (MOVE move : possibleMoves) {
			PathInfo path = this.getPath(currentNode, move,state);

			PathInfo bestSecondPath = this.bestPath(path.endNode, path.endMove, depth - 1, null, state);
		
			//DESMARCAR

			int points = path.points + bestSecondPath.points;

			if (bestPath.startMove == null || bestPath.points < points) {
				bestPath.startMove = move;
				bestPath.points = points;
			}

		}

		return bestPath;
	}
	
	private PathInfo getPath(int startNode, MOVE startMove, boolean state) {
		PathInfo path = new PathInfo(0, startNode, -1, startMove, null);

		int currentNode = startNode;
		MOVE currentMove = startMove;

		int endNode = this.game.getNeighbour(startNode, currentMove);

		while (!game.isJunction(endNode)) {
			path.points += getNodePoints(endNode, state);

			currentNode = endNode;
			currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];
			endNode = this.game.getNeighbour(currentNode, currentMove);

		}

		path.points += game.getShortestPathDistance(startNode, endNode, currentMove) * ( state == true ? VALUE_PER_NODE : VALUE_PER_NODE * -1); // TODO: revisar
		path.endNode = endNode;
		path.endMove = this.game.getMoveToMakeToReachDirectNeighbour(currentNode, endNode);

		return path;
	}
	
	//state, true para indicar que el fantasma es comestible, false para indicar que no lo es
	private int getNodePoints(int node, boolean state) { // FIXME: implement

		int points = 0;

		
		if(node == this.pacmanNode && state) {
			points += PACMAN;
		}
		else if(node == this.pacmanNode)
			points += PACMAN * -1;
		
		if (node != -1 && !this.ppillsNodes.contains(node)) {
			
			if(state) 
				points += Constants.PILL * -1;
			else 
				points += Constants.PILL;
			
		}
		else if (node != -1) {
			
			if(state || game.getShortestPathDistance(pacmanNode, node) < LIMIT_DISTANCE_BETWEEN_PACMAN_PPILL)
				points += Constants.POWER_PILL * -1;
			else
				points += Constants.POWER_PILL;
			
		}

		return points;
	}
}