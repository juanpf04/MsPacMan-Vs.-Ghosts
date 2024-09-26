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

public class MsPacMan extends PacmanController{

	private static final int RANGE = 200;
	private static final int PILL_VALUE = 10;
	private static final int VALUE_PATH_GHOST_NOT_EDIBLE = -1500;
	private static final int VALUE_PATH_GHOST_EDIBLE = +1000;
	private static final int VALUE_PER_NODE = 20;
	private static final int POWER_PILL_VALUE = 30;
	private static final int ALL_GHOSTS_OUTSIDE = 500;
	
    @Override
    public MOVE getMove(Game game, long timeDue) {
    	boolean juctionIndice = false;
    	int pacmanNode = game.getPacmanCurrentNodeIndex();
    	juctionIndice = game.isJunction(pacmanNode);
    	
    	if(juctionIndice) {
    		MOVE[] allPossibleMoves = game.getPossibleMoves(pacmanNode);
    		Map<Integer, GHOST> ghostIndices = new HashMap<Integer, GHOST>();
    		Map<MOVE, Variables> caminos = new HashMap<MOVE, Variables>();
    		
    		ghostIndices(game, ghostIndices);
    		this.initializePaths(caminos, allPossibleMoves,game, ghostIndices, pacmanNode);
    		
    		return analyzeBestPath(caminos, game);
    
    	}
    	
        return MOVE.NEUTRAL;
    }
    
    public String getName() {
    	return "Fantasmikos";
    }
    
    private void initializePaths(Map<MOVE, Variables> caminos, MOVE[] allPossibleMoves, Game game, Map<Integer,GHOST> ghostIndices, int pacmanNode) {
    	for(MOVE m: allPossibleMoves) {
			int nodes[] = game.getCurrentMaze().graph[pacmanNode].allNeighbouringNodes.get(m);
			Variables camino = studyPath(game, nodes, pacmanNode, ghostIndices);
			caminos.put(m, camino);
		}
    }
    
    private void ghostIndices(Game game, Map<Integer, GHOST> ghostIndices) {
    	
    	for(GHOST g: GHOST.values()) {
    		int index = game.getGhostCurrentNodeIndex(g);
    		
    		if(ghostIndices.get(index) != null) {
    			ghostIndices.put(index, g);
    		}
    	}
    }
    
    private boolean ghostsOutOfHiding(Game game) {
    	int nGhosts = 0;
    	
    	for(GHOST g: GHOST.values()) {
    		if(game.getGhostCurrentNodeIndex(g) != game.getGhostInitialNodeIndex()) {
    			nGhosts++;
    		}
    	}
    	
    	return nGhosts == 4;
    }
    
    private Variables studyPath(Game game, int nodes[], int initialNode,Map<Integer,GHOST> ghostIndices) {
    	int i = 0;
    	Variables v = new Variables(0,0);
    	
    	while(i < nodes.length && game.isJunction(game.getCurrentMaze().graph[nodes[i]].numNeighbouringNodes)) {
    		
    		if(ghostIndices.containsKey(i) && game.getDistance(initialNode, nodes[i], DM.MANHATTAN) <= RANGE) {
    			v.setNearestGhosts(ghostIndices.get(i));
    		}
    		if(game.isPillStillAvailable(game.getCurrentMaze().graph[nodes[i]].pillIndex)) {
    			v.setNumberOfPills(v.getNumberOfPills() +1);
    		}
    		else if(game.isPowerPillStillAvailable(game.getCurrentMaze().graph[nodes[i]].powerPillIndex)) {
    			v.activatePowerPill(true);
    		}
    		
    		
    		i++;
    	}
    	
    	v.setDistanceFromNearestIndex(game.getDistance(initialNode, nodes[i], DM.MANHATTAN));
    	
    	return v;
    }
    
    private MOVE analyzeBestPath(Map<MOVE, Variables> caminos, Game game) {
    	int puntos_camino = 0;
    	TreeMap<Integer, MOVE> mejor_camino = new TreeMap<Integer,MOVE>();
    	
    	
		for (Map.Entry<MOVE, Variables> entry : caminos.entrySet()) {
			Variables camino = entry.getValue();
			
			for(GHOST g: camino.getNearestGhosts()) {
					if(!game.isGhostEdible(g)) puntos_camino += VALUE_PATH_GHOST_NOT_EDIBLE;
					else puntos_camino += VALUE_PATH_GHOST_EDIBLE;		
			}
			
		
			if(camino.powerPill() && camino.getNumberOfGhosts() > 0) {
				puntos_camino += POWER_PILL_VALUE;
				
				if(ghostsOutOfHiding(game)) {
					puntos_camino += ALL_GHOSTS_OUTSIDE;
				}
			}
			else if(camino.powerPill()) {
				puntos_camino += POWER_PILL_VALUE * -100;
			}
		
			puntos_camino += camino.getDistanceFromNearestIndex() * VALUE_PER_NODE;
			puntos_camino += camino.getNumberOfPills() * PILL_VALUE;
			
			
			mejor_camino.put(puntos_camino, entry.getKey());
		}
    	
    	return mejor_camino.lastEntry().getValue();
    }

}