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

public class MsPacMan extends PacmanController{

	private static final int RANGE = 200;
	private static final int PILL_VALUE = 10;
	private static final int VALUE_PATH_GHOST_NOT_EDIBLE = -1500;
	private static final int VALUE_PATH_GHOST_EDIBLE = +1000;
	private static final int VALUE_PER_NODE = 20;
	private static final int POWER_PILL_VALUE = 30;
	private static final int ALL_GHOSTS_OUTSIDE = 500;
	
	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Grupo02");
	}
	
    @Override
    public MOVE getMove(Game game, long timeDue) {
    	boolean juctionIndice = false;
    	int pacmanNode = game.getPacmanCurrentNodeIndex();
    	juctionIndice = game.isJunction(pacmanNode);
    	
    	if(juctionIndice) {
    		Map<Integer, GHOST> ghostIndices = new HashMap<Integer, GHOST>();
    		Map<MOVE, Variables> caminos = new HashMap<MOVE, Variables>();
    		
    		ghostIndices(game, ghostIndices);
    		this.initializePaths(caminos,game, ghostIndices, pacmanNode);
    		
    		return analyzeBestPath(caminos, game);
    
    	}
    	
        return MOVE.NEUTRAL;
    }
    
    public String getName() {
    	return "Fantasmikos";
    }
    
    //Pasar a clase general. Se encarga de iniciar los caminos posibles tras el estudio de los mismos desde el nodo de intersección en el que estamos
    private void initializePaths(Map<MOVE, Variables> caminos, Game game, Map<Integer,GHOST> ghostIndices, int pacmanNode) {
    	
    	EnumMap<MOVE, Integer> movements = game.getCurrentMaze().graph[pacmanNode].allNeighbourhoods.get(game.getPacmanLastMoveMade());
    	
    	if(movements != null) {
	    	for (Map.Entry<MOVE, Integer> entry : movements.entrySet()) {
	    		
    			 List<Integer> nodes = path(game, entry.getValue(), entry.getKey());
    			 Variables camino = studyPath(game, nodes, pacmanNode, ghostIndices);
    			 caminos.put(entry.getKey(), camino);
	    		
	    	}
    	}
		
    }
    
    //Guardamos en un mapa el nodo de cada fantasma.
    private void ghostIndices(Game game, Map<Integer, GHOST> ghostIndices) {
    	
    	for(GHOST g: GHOST.values()) {
    		int index = game.getGhostCurrentNodeIndex(g);
    		
    		if(ghostIndices.get(index) == null) {
    			ghostIndices.put(index, g);
    		}
    	}
    }
    
    //Comprobar si todos los fantasmas están fuera de su guarida
    private boolean ghostsOutOfHiding(Game game) {
    	int nGhosts = 0;
    	
    	for(GHOST g: GHOST.values()) {
    		if(game.getGhostCurrentNodeIndex(g) != game.getGhostInitialNodeIndex()) {
    			nGhosts++;
    		}
    	}
    	
    	return nGhosts == 4;
    }
    
    private List<Integer> path(Game game, int node, MOVE m) {
    	List<Integer> nodes = new ArrayList<>();
    	EnumMap<MOVE, Integer> move = game.getCurrentMaze().graph[node].allNeighbourhoods.get(m);
    	MOVE mv = m;
    	if(move != null) nodes.add(node);
    	while(move.containsKey(mv)) {
    		Integer index = move.get(mv);
    		nodes.add(index);
    		
    		move = game.getCurrentMaze().graph[index].allNeighbourhoods.get(mv);
    	}
    	
    	return nodes;
    	
    }
    //Estudiamos el camino
    private Variables studyPath(Game game, List<Integer> nodes, int initialNode,Map<Integer,GHOST> ghostIndices) {
    	int i = 0;
    	Variables v = new Variables(0,0);
    	
    	while(i < nodes.size() && !game.isJunction(game.getCurrentMaze().graph[nodes.get(i)].numNeighbouringNodes)) {
    		Node nodo = game.getCurrentMaze().graph[nodes.get(i)];
    		
    	
    		if(ghostIndices.containsKey(i) && game.getDistance(initialNode, nodes.get(i), DM.MANHATTAN) <= RANGE) {
    			v.setNearestGhosts(ghostIndices.get(i));
    		}
    		if(nodo.pillIndex != -1) {
    			v.setNumberOfPills(v.getNumberOfPills() +1);
    		}
    		else if(nodo.powerPillIndex != -1) {
    			v.activatePowerPill(true);
    		}
    		
    		
    		i++;
    	}
    	
    	v.setDistanceFromNearestIndex(game.getDistance(initialNode, nodes.get(i < nodes.size() ? i : i - 1), DM.MANHATTAN));
    	
    	return v;
    }
    
  //Evaluamos cual es el camino mas eficiente mediante un sistema de rangos númericos. Dependiendo de las situaciones que se den se suman/restan puntos
    private MOVE analyzeBestPath(Map<MOVE, Variables> caminos, Game game) {
    	int puntos_camino = 0;
    	TreeMap<Integer, MOVE> mejor_camino = new TreeMap<Integer,MOVE>();
    	
    	
		for (Map.Entry<MOVE, Variables> entry : caminos.entrySet()) {
			Variables camino = entry.getValue();
			
			if(camino.getNumberOfGhosts() > 0) {
				for(GHOST g: camino.getNearestGhosts()) {
						if(!game.isGhostEdible(g)) puntos_camino += VALUE_PATH_GHOST_NOT_EDIBLE;
						else puntos_camino += VALUE_PATH_GHOST_EDIBLE;		
				}
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
			puntos_camino = 0;
		}
    	
    	return mejor_camino.lastEntry().getValue();
    }

}