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
    	int pacmanNode = game.getPacmanCurrentNodeIndex();
    	
    	if(game.isJunction(pacmanNode)) {
    		Map<Integer, GHOST> ghostIndices = new HashMap<Integer, GHOST>();
    		
    		ghostIndices(game, ghostIndices);
    		TreeMap<Integer, MOVE> sol = this.initializePaths(new ArrayList<>(),new HashMap<>(),game, ghostIndices, pacmanNode, 0, game.getPacmanLastMoveMade(), 0, new TreeMap<>());
    		return sol.get(sol.size()- 1);
    	}
    	
        return MOVE.NEUTRAL;
    }
    
    public String getName() {
    	return "Fantasmikos";
    }
    
    //Pasar a clase general. Se encarga de iniciar los caminos posibles tras el estudio de los mismos desde el nodo de intersección en el que estamos
    private TreeMap<Integer, MOVE> initializePaths(List<Integer>  nodes, Map<Integer, Variables> nodeMap, Game game, Map<Integer,GHOST> ghostIndices, int currentNode, int iteracion, MOVE lastMode,int puntuacion, TreeMap<Integer, MOVE> sol) {
    	
    	EnumMap<MOVE, Integer> movements = game.getCurrentMaze().graph[currentNode].allNeighbourhoods.get(lastMode);
    	
    	if(iteracion > 3) {
    		return sol;
    	}
    		
    	if(movements != null && iteracion <= 3) {
	    	for (Map.Entry<MOVE, Integer> entry : movements.entrySet()) {
	    		
	    		 sol.putAll(path(game, entry.getValue(), entry.getKey(), ghostIndices, nodes, puntuacion));
    			 
    			initializePaths(nodes, nodeMap, game, ghostIndices, nodes.get(nodes.size() - 1), iteracion + 1, lastMove(nodes.get(nodes.size() - 1), nodes.get(nodes.size() - 2)), puntuacion, sol); 
	    	} 	
    	}
    	
    	return sol;
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
    
    private Map<Integer, MOVE> path(Game game, int node, MOVE m, Map<Integer, GHOST> ghostIndices, List<Integer> nodes, int puntuacion) {
    	Map<Integer, MOVE> sol = new HashMap<>();
    	
    	//Si la lista de nodos esta vacia (primera llamada) la iniciamos
    	if(nodes == null) nodes = new ArrayList<>();
    	
    	//Calculamos el nodo correspondiente al movimiento especificado
    	EnumMap<MOVE, Integer> move = game.getCurrentMaze().graph[node].allNeighbourhoods.get(m);
    	
    	Integer index = -1;
    	//Recorrer todos los caminos hasta el siguiente nodo de interseccion
    	while(move.containsKey(m) && !game.isJunction(move.get(m))) {
    		index = move.get(m);
    		
    		//Analiza cada nodo del camino y suma la puntuacion
    		puntuacion += analyzeNode(node, game, ghostIndices);
    		
    		//Añade el nodo al array de nodos, para saber el ultimo nodo antes del actual y calcular el lastMove
    		nodes.add(node);
    		
    		//Avanzar nodo
    		move = game.getCurrentMaze().graph[index].allNeighbourhoods.get(m);
    	}
    	
    	//Una vez que hemos recorrido todo el camino hasta el nodo de interseccion sumamos la puntuacion relativa a la distancia del camino
    	puntuacion += game.getShortestPathDistance(node, nodes.get(nodes.size() - 1), lastMove(nodes.get(nodes.size() - 1), nodes.get(nodes.size() - 2))) * VALUE_PER_NODE;
    	
    	//Añadir la puntuacoin obtenida en este camino junto al move original del que proviene
    	sol.put(puntuacion,m);
    	
    	return sol; 
    }
    
    private MOVE lastMove(int currentIndex, int lastIndex) {
    	int diferencia = currentIndex - lastIndex;
    	
    	switch(diferencia) {
			case 1:
					return MOVE.RIGHT;
			case -1:
					return MOVE.LEFT;
			default:
				if(diferencia > 0) {
					return MOVE.UP;
				}
				else if(diferencia < 0) {
					return MOVE.DOWN;
				}
				else return MOVE.NEUTRAL;
			}    	
    }
    
    private int analyzeNode(int node, Game game, Map<Integer, GHOST> ghostIndices) {
    	int puntuacion = 0;
    	
    	Node n = game.getCurrentMaze().graph[node];
    	
    	if(ghostIndices.containsKey(node)) {
			puntuacion += VALUE_PATH_GHOST_NOT_EDIBLE;
		}
		if(n.pillIndex != -1) {
			puntuacion += PILL_VALUE;
		}
		else if(n.powerPillIndex != -1) {
			puntuacion += POWER_PILL_VALUE;
		}
    	
    	return puntuacion;
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