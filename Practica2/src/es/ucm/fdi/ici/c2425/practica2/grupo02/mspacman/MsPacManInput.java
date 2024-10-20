package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.Set;


import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class MsPacManInput extends Input {

	private static final int SAFE_DISTANCE = 15; // Distancia segura
    private static final int SEARCH_DEPTH = 40; // Profundidad de búsqueda para evaluar caminos

    private static final int WEIGHT_SECURITY = 3;
    private static final int WEIGHT_PILLS = 2;
    private static final int WEIGHT_EDIBLE_GHOSTS = 1;
    private static final int WEIGHT_POWER_PILLS_FLEE = 5;
    private static final int WEIGHT_POWER_PILLS_PILLS = 1;
    private static final int WEIGHT_EDIBLE_GHOST_PROXIMITY = 5;
	private static final int WEIGHT_NON_EDIBLE_GHOST_PROXIMITY = -5;

	private MsPacManInfo info;
	private SafePaths safePaths;

	public MsPacManInput(Game game) {
		super(game);
		info = new MsPacManInfo(game);
	}

	@Override
	public void parseInput() {
		// does nothing.

	}
	
	//TRANSITIONS---------------------------------------------------------------------------------------------------------------------

		//ESTADOS COMPUESTOS---------------------------------------------------------------------------------------------------------
		public boolean edibleTimeYet() {
			return !withoutEdibleTime();
		}
		public boolean pacmanEatPowerPill(int powerPills){

			if(this.getGame().getNumberOfActivePowerPills() < powerPills && edibleGhostClose()){
				powerPills = this.getGame().getNumberOfActivePowerPills();
				return true;
			}

			return false;
		}
		public boolean getClosestNotEdibleGhost(int range) {
			return ghostCloseDistance(range) != Integer.MAX_VALUE;
		}
		
		//ESTADO PILLS--------------------------------------------------------------------------------------------------------------
		public boolean sameNumberOfPills() {
			int pacmanNode =info.getPacmanCurrentNodeIndex();
			MOVE[] possibleMoves = info.getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());

			Integer nPills = null;
			for (MOVE move : possibleMoves) {
				if (SafePaths.getSafePaths().containsKey(move)) {
					int pillsForPath = SafePaths.getPillsForPath().get(move);
					if (nPills == null) {
						nPills = pillsForPath;
					} else if (nPills != pillsForPath) {
						return false; // Si encontramos un camino con un número diferente de pills = false
					}
				}
			}	

    	return nPills != null;//Si todos los caminos tienen el mismo numero de pills = true
	}
		public boolean moreThanOneSavePath() {
			return this.safePaths.size() > 1;
		}

		//ESTADO CHASE-----------------------------------------------------------------------------------------------------------
		public boolean edibleGhostClose() {
			return getClosestEdibleGhostDistance(SEARCH_DEPTH)!= Integer.MAX_VALUE;
		}
		public boolean withoutEdibleTime() {
			boolean ghostEdible = false;
			int contador = 0;
			for(GHOST ghost: GHOST.values()){
				//La medida para comprobar que le queda poco tiempo me la he inventado
				if(this.getGame().getGhostEdibleTime(ghost) <= 3 && this.getGame().getGhostLairTime(ghost) != 0){
					ghostEdible = true;
					contador++;
				}
			}

			return ghostEdible && contador == 4;
		}


		//ESTADO FLEE--------------------------------------------------------------------------------------------------------
		public boolean edibleGhostNearestThanGhost(int range) {
			return getClosestEdibleGhostDistance(range) < ghostCloseDistance(range);
		}
		public boolean sameNumberOfEdibleGhostInEachPath(){
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			Map<MOVE, Integer> paths = new HashMap<MOVE, Integer>();

			for(GHOST ghost: GHOST.values()){

				if(edibleGhostClose()){
					MOVE move = this.getGame().getMoveToMakeToReachDirectNeighbour(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
					
					if(!paths.containsKey(move)){
						paths.put(move, 1);
					}
					else{
						paths.put(move, paths.get(move) + 1);
					}
						
				}
			}
			
			if(paths.isEmpty())
				return true;

			// Obtener el primer valor
			Iterator<Integer> iterator = paths.values().iterator();
			int firstValue = iterator.next();

			// Comparar todos los valores con el primer valor
			while (iterator.hasNext()) {
				if (iterator.next() != firstValue) {//Si en algun momento, algun valor es diferente, hay mas g en ese camino
					return false;
				}
			}

			return true;
		}
		public boolean powerPillCloserThanGhost(int range) {
			return powerPillDistance(range) < getEveryTypeClosestGhostDistance(range);
		}

		
	//ACTIONS
	
		//ESTADO PILLS----------------------------------------------------------------------------------------------------------
		public MOVE chooseAny() {
			return !SafePaths.isEmpty() ? SafePaths.getSafePaths().keySet().iterator().next():MOVE.NEUTRAL;
		}
		
		//ESTADO CHASE / FLEE---------------------------------------------------------------------------------------------------
		
		//Usado en chase / flee
		public MOVE pathWithMoreEdibleGhosts() {
			MOVE bestMove = MOVE.NEUTRAL;
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			
			if(getClosestEdibleGhostDistance(SEARCH_DEPTH) != Integer.MAX_VALUE){
				SafePaths.clear();

				MOVE[] possibleMoves = info.getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
				for (MOVE move : possibleMoves) {
					int ghostScore = evaluatePathEdibleGhosts(pacmanNode, move, SEARCH_DEPTH);
					
					if (ghostScore > 0) { // Considera un camino si tiene una puntuación de fantasmas comestibles positiva
						SafePaths.addSafePath(move, ghostScore);
					}
				}

				int maxScore = Integer.MIN_VALUE;

				for (Map.Entry<MOVE, Integer> entry : SafePaths.getSafePaths().entrySet()) {
					int score = entry.getValue();
					if (score > maxScore) {
						maxScore = score;
						bestMove = entry.getKey();
					}
				}
		
			}
			else 
				bestMove = moveToNearestEdibleGhost();

			return bestMove;
		}
		public MOVE moveToNearestEdibleGhost() {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			int minDistance = Integer.MAX_VALUE;
			MOVE bestMove = MOVE.NEUTRAL;
	
			for (GHOST ghost : GHOST.values()) {
				if (this.getGame().getGhostEdibleTime(ghost) > 3 && this.getGame().getGhostLairTime(ghost) == 0) {
					int ghostNode = this.getGame().getGhostCurrentNodeIndex(ghost);
					int distance = this.getGame().getShortestPathDistance(pacmanNode, ghostNode);
	
					if (distance < minDistance) {
						minDistance = distance;
						bestMove = this.getGame().getNextMoveTowardsTarget(pacmanNode, ghostNode, DM.PATH);
					}
				}
			}

			return bestMove;
		}
		public MOVE moveToSafetyGhost() {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			SafePaths.clear();

			MOVE bestMove = MOVE.NEUTRAL;

			MOVE[] possibleMoves = info.getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
			for (MOVE move : possibleMoves) {
				int ghostScore = evaluatePathSafetyGhost(pacmanNode, move, SEARCH_DEPTH);
				
				if (ghostScore > 0) { // Considera un camino si tiene una puntuación de seguridad positiva
					SafePaths.addSafePath(move, ghostScore);
				}
			}

			int maxScore = Integer.MIN_VALUE;

			for (Map.Entry<MOVE, Integer> entry : SafePaths.getSafePaths().entrySet()) {
				int score = entry.getValue();
				if (score > maxScore) {
					maxScore = score;
					bestMove = entry.getKey();
				}
			}

			return bestMove;
		}
		
		//ESTADO FLEE------------------------------------------------------------------------------------------------------------
		//Usado en flee y pills
		public MOVE pathWithMorePills(boolean state) {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			MOVE bestMove = MOVE.NEUTRAL;

			Set<MOVE> safeMovesSet = SafePaths.getSafePaths().keySet();
			SafePaths.clear();

			MOVE[] possibleMoves = info.getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
			for (MOVE move : possibleMoves) {
				
				if(safeMovesSet.contains(move)){
					int pillScore = evaluatePathPills(pacmanNode, move, SEARCH_DEPTH, state);
					
					if (pillScore > 0) { // Considera un camino si tiene una puntuación de pills positiva
						SafePaths.addSafePath(move, pillScore);
					}
				}
			}

			int maxScore = Integer.MIN_VALUE;

			for (Map.Entry<MOVE, Integer> entry : SafePaths.getSafePaths().entrySet()) {
				int score = entry.getValue();
				if (score >= maxScore) {
					maxScore = score;
					bestMove = entry.getKey();
				}
			}

			return bestMove;
		}
		
		public MOVE moveToPowerPill() {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			MOVE solucion = MOVE.NEUTRAL;
			int min_distance = Integer.MAX_VALUE;

			for(int PowerPill: this.getGame().getActivePowerPillsIndices()){
				int distance = this.getGame().getShortestPathDistance(pacmanNode, PowerPill);
				
				if(distance < min_distance){
					min_distance = distance;
					solucion = this.getGame().getNextMoveTowardsTarget(pacmanNode, PowerPill, info.getPacmanLastMoveMade(), DM.PATH);
				}
			}

			return solucion;
		}
		
		//Usado en flee y pills
		public MOVE moveToSafetyPath(boolean state) {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			SafePaths.clear();

			MOVE bestMove = MOVE.NEUTRAL;

			MOVE[] possiblesMoves = info.getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
			for (MOVE move : possiblesMoves) {
				int safetyScore = evaluatePathSafety(pacmanNode, move, SEARCH_DEPTH, state);
				
				if (safetyScore > 0) { // Considera un camino seguro si tiene una puntuación de seguridad positiva
                	SafePaths.addSafePath(move, safetyScore);
				}
            }

			
			 int maxScore = Integer.MIN_VALUE;
	 
			 for (Map.Entry<MOVE, Integer> entry : SafePaths.getSafePaths().entrySet()) {
				 int score = entry.getValue();
				 if (score > maxScore) {
					 maxScore = score;
					 bestMove = entry.getKey();
				 }
			 }

			return bestMove;
		}

		
	//METODOS AUXILIARES DE LA CLASE------------------------------------------------------------------------------------------

		//Devuelve la distancia del fantasma comestible mas cercano
		private double getClosestEdibleGhostDistance(int range){
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			int min_distance = Integer.MAX_VALUE;
			
			for(GHOST ghost: GHOST.values()){
				int distancia = this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
				int distance = Math.min(min_distance,distancia);
					min_distance = distance <= range && this.getGame().getGhostEdibleTime(ghost) > 0 && this.getGame().getGhostLairTime(ghost) == 0? distance : min_distance;
			}

			return min_distance;
		}

		//Devuelve la distancia del fantasma mas cercano (sea edible o no)
		private double getEveryTypeClosestGhostDistance(int range){
			int pacmanNode = info.getPacmanCurrentNodeIndex();
		
			int min_distance = Integer.MAX_VALUE;
			
			for(GHOST ghost: GHOST.values()){
				int distancia = this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
				int distance = Math.min(min_distance,distancia);
					min_distance = distance <= range && this.getGame().getGhostLairTime(ghost) == 0? distance : min_distance;
			}

			return min_distance;
		}

		//Devuelve la distancia a un fantasma no comestible
		private double ghostCloseDistance(int range){
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			int min_distance = Integer.MAX_VALUE;
			
			for(GHOST ghost: GHOST.values()){
				int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost)));
				
				if(this.getGame().getGhostEdibleTime(ghost) == 0 && this.getGame().getGhostLairTime(ghost) == 0)
					min_distance = distance <= range ? distance : min_distance;
			}

			return min_distance;
		}
		//Devuelve la distancia minima a un power pill
		private double powerPillDistance(int range){
			int min_distance = Integer.MAX_VALUE;
			int pacmanNode = info.getPacmanCurrentNodeIndex();
		
			for(int PowerPill: this.getGame().getActivePowerPillsIndices()){
			int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, PowerPill));
				if(distance <= range)
					min_distance = Math.min(min_distance, distance);
			}

			return min_distance;
		}
	
		//BUSQUEDA EN PROFUNDIDAD IMPLEMENTADA CON PILAS PARA EL CAMINO MAS SEGURO
		private int evaluatePathSafety(int startNode, MOVE move, int depth, boolean state) {
			 int currentNode = startNode;
			int safetyScore = 0;
			int edibleGhostCount = 0;
			int nPills = 0, nPowerPills = 0;

			for (int i = 0; i < depth; i++) {
				currentNode = this.getGame().getNeighbour(currentNode, move);
				if (currentNode == -1) {
					break; // No hay más nodos en esta dirección
				}

				// Evaluar la seguridad del nodo actual
				safetyScore += evaluateNodeSafety(currentNode);

				int pillIndex = this.getGame().getPillIndex(currentNode);
				if(pillIndex != -1 && this.getGame().isPillStillAvailable(pillIndex)){
					nPills++;
				}

				int powerPillIndex = this.getGame().getPowerPillIndex(currentNode);
				if(powerPillIndex != -1 && this.getGame().isPowerPillStillAvailable(powerPillIndex)){
					nPowerPills++;
				}

				// Contar los fantasmas comestibles cercanos
				for (GHOST ghost : GHOST.values()) {
					if (this.getGame().getGhostEdibleTime(ghost) > 0) {
						int ghostNode = this.getGame().getGhostCurrentNodeIndex(ghost);
						int distance = this.getGame().getShortestPathDistance(currentNode, ghostNode);
						if (distance < SAFE_DISTANCE) {
							edibleGhostCount++;
						}
					}
				}

				//Valorar mas los caminos que llevan a intersecciones con mas caminos
				if (this.getGame().isJunction(currentNode)) {
					MOVE[] possibleMoves = this.getGame().getPossibleMoves(currentNode);
					safetyScore += possibleMoves.length; // Aumentar la puntuación de seguridad según la cantidad de caminos
				}
			}

			SafePaths.addPillsForPath(move, nPills);
			
			// Calcular la puntuación total ponderada
			int totalScore = (WEIGHT_SECURITY * safetyScore) +  (WEIGHT_PILLS * nPills) +(WEIGHT_EDIBLE_GHOSTS * edibleGhostCount) + (state ? WEIGHT_POWER_PILLS_FLEE : WEIGHT_POWER_PILLS_PILLS) * nPowerPills;

			return totalScore;
		}
	
		//Evaluar la seguridad de un nodo
		private int evaluateNodeSafety(int node) {
			int safetyScore = 0;

			for (GHOST ghost : GHOST.values()) {
				if (this.getGame().getGhostEdibleTime(ghost) == 0) { // Fantasma no comestible
					int ghostNode = this.getGame().getGhostCurrentNodeIndex(ghost);
					int distance = this.getGame().getShortestPathDistance(node, ghostNode);
					
					if (distance < SAFE_DISTANCE) {
						safetyScore -= (SAFE_DISTANCE - distance); // Penalizar nodos cercanos a fantasmas
					}
				}
			}

        	return safetyScore;
		}

		//BUSQUEDA EN PROFUNDIDAD IMPLEMENTADA PARA PILLS
		private int evaluatePathPills(int startNode, MOVE move, int depth, boolean state) {
			int currentNode = startNode;
			int nPills = 0;
			int nPowerPill = 0;

			for (int i = 0; i < depth; i++) {
				currentNode = this.getGame().getNeighbour(currentNode, move);
				if (currentNode == -1) {
					break; // No hay más nodos en esta dirección
				}

				// Contar las píldoras en el nodo actual
				int pill = this.getGame().getPillIndex(currentNode);
				if (pill != -1 && this.getGame().isPillStillAvailable(pill)) {
					nPills++;
				}

				// Contar las power pills en el nodo actual
				if (this.getGame().getPowerPillIndex(currentNode) != -1 && this.getGame().isPowerPillStillAvailable(this.getGame().getPowerPillIndex(currentNode))) {
					nPowerPill++;
				}
			}

			// Calcular la puntuación total ponderada teniendo en cuenta si estamos en flee o en pills (state)
			int totalScore = (WEIGHT_PILLS * nPills) + (state ? WEIGHT_POWER_PILLS_FLEE : WEIGHT_POWER_PILLS_PILLS) * nPowerPill;

			return totalScore;
		}
	
		//BUSQUEDA EN PROFUNDIDAD PARA LOS FANTASMAS COMESTIBLES
		private int evaluatePathEdibleGhosts(int startNode, MOVE move, int depth){
			int currentNode = startNode;
			int nEdibleGhosts = 0;
			int proximityScore = 0;

			for (int i = 0; i < depth; i++) {
				currentNode = this.getGame().getNeighbour(currentNode, move);
				if (currentNode == -1) {
					break; // No hay más nodos en esta dirección
				}

				// Contar los fantasmas comestibles cercanos
				for (GHOST ghost : GHOST.values()) {
					if (this.getGame().getGhostEdibleTime(ghost) > 0) {
						int ghostNode = this.getGame().getGhostCurrentNodeIndex(ghost);
						int distance = this.getGame().getShortestPathDistance(currentNode, ghostNode);
						
						if (distance < SAFE_DISTANCE) {
							nEdibleGhosts++;
							proximityScore += (SAFE_DISTANCE - distance); // Mayor proximidad, mayor puntuación
						}
					}
				}
			}

			// Calcular la puntuación total ponderada
			int totalScore = (WEIGHT_EDIBLE_GHOSTS * nEdibleGhosts) + (WEIGHT_EDIBLE_GHOST_PROXIMITY * proximityScore);

			return totalScore;
		}

		//BUSQUEDA EN PROFUNDIDAD PARA LOS FANTASMAS COMESTBILES MAS SEGUROS
		private int evaluatePathSafetyGhost(int startNode, MOVE move, int depth) {
			int currentNode = startNode;
			int safetyScore = 0;
			int nonEdibleGhostProximityScore = 0;
	
			for (int i = 0; i < depth; i++) {
				currentNode = this.getGame().getNeighbour(currentNode, move);
				if (currentNode == -1) {
					break; // No hay más nodos en esta dirección
				}
	
				// Evaluar la seguridad del nodo actual
				safetyScore += evaluateNodeSafety(currentNode);
	
				// Contar la proximidad a los fantasmas no comestibles
				for (GHOST ghost : GHOST.values()) {
					if (this.getGame().getGhostEdibleTime(ghost) == 0) { // Fantasma no comestible
						int ghostNode = this.getGame().getGhostCurrentNodeIndex(ghost);
						int distance = this.getGame().getShortestPathDistance(currentNode, ghostNode);
						
						if (distance < SAFE_DISTANCE) {
							nonEdibleGhostProximityScore += (SAFE_DISTANCE - distance); // Mayor proximidad, mayor penalización
						}
					}
				}
			}
	
			// Calcular la puntuación total ponderada
			int totalScore = (WEIGHT_SECURITY * safetyScore) + (WEIGHT_NON_EDIBLE_GHOST_PROXIMITY * nonEdibleGhostProximityScore);
	
			return totalScore;
		}		
}