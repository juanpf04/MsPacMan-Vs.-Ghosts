package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import java.util.AbstractMap.SimpleEntry;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class MsPacManInput extends Input {

	private static final int SAFE_DISTANCE = 20;
    private static final int SEARCH_DEPTH = 10; // Profundidad de búsqueda para evaluar caminos

	private MsPacManInfo info;
	private SafePaths safePaths;

	public MsPacManInput(Game game) {
		super(game);
		info = new MsPacManInfo(game);
		safePaths = new SafePaths();

	}

	@Override
	public void parseInput() {
		// does nothing.

	}
	
	//TRANSITIONS---------------------------------------------------------------------------------------------------------------------

		//ESTADOS COMPUESTOS---------------------------------------------------------------------------------------------------------
		public boolean edibleTimeYet() {
			for(GHOST ghost: GHOST.values()){
				if(this.getGame().getGhostEdibleTime(ghost) > 0 && this.getGame().getGhostLairTime(ghost) == 0){
					return true;
				}
			}
			return false;
		}
		public boolean pacmanEatPowerPill(){
			boolean sol = false;

			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			for(int PowerPill: this.getGame().getActivePowerPillsIndices()){
				sol = pacmanNode == PowerPill || Math.abs(pacmanNode - PowerPill) <= 4;
			}

			return sol;
		}
		public boolean getClosestNotEdibleGhost(int range) {
			return getEveryTypeClosestGhostDistance(range) != Integer.MAX_VALUE;
		}
		
		//ESTADO PILLS--------------------------------------------------------------------------------------------------------------
		public boolean sameNumberOfPills() {
			int currentNode = this.getGame().getPacmanCurrentNodeIndex();
			MOVE currentMove = (MOVE) this.getGame().getPacmanLastMoveMade();

			Map<MOVE, Integer> numberPills = new HashMap<MOVE, Integer>();

			MOVE[] moves = (MOVE[]) this.getGame().getPossibleMoves(currentNode, currentMove);

			for(MOVE move: moves){
				currentMove = move;
				int nPills = 0;
				int endNode = this.getGame().getNeighbour(currentNode, currentMove);

				while(endNode != -1 &&!this.getGame().isJunction(endNode)){
					
					if(this.getGame().isPillStillAvailable(endNode)) nPills++;

					currentNode = endNode;
					currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];
					endNode = this.game.getNeighbour(currentNode, currentMove);

				}
				numberPills.put(move, nPills);
				nPills = 0;
			}

			
			if(numberPills.isEmpty())
				return false;

			// Obtener el primer valor
			Iterator<Integer> iterator = numberPills.values().iterator();
			int firstValue = iterator.next();

			// Comparar todos los valores con el primer valor
			while (iterator.hasNext()) {
				if (iterator.next() != firstValue) {//Si en algun momento, algun valor es diferente, hay mas g en ese camino
					return false;
				}
			}

			return true;

		}
		public boolean moreThanOneSavePath() {
			return this.safePaths.size() > 1;
		}

		//ESTADO CHASE-----------------------------------------------------------------------------------------------------------
		public boolean edibleGhostClose() {
			return edibleGhostCloseDistance(40) != Integer.MAX_VALUE;
		}
		public boolean withoutEdibleTime() {
			boolean ghostEdible = false;
			int contador = 0;

			for(GHOST ghost: GHOST.values()){
				//La medida para comprobar que le queda poco tiempo me la he inventado
				if(this.getGame().getGhostEdibleTime(ghost) == 0 || this.getGame().getGhostEdibleTime(ghost) < 3){
					ghostEdible = true;
					contador++;
				}
			}

			return ghostEdible && contador == 4;
		}


		//ESTADO FLEE--------------------------------------------------------------------------------------------------------
		public boolean edibleGhostNearestThanGhost(int range) {
			return edibleGhostCloseDistance(range) < ghostCloseDistance(range);
		}
		public boolean sameNumberOfEdibleGhostInEachPath(){
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
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
	//Para el estudio de los caminos tenog que ver como reutilizar el codigo del backtracking
	//Ya que asi estudio hasta 3 nodos de profundidad. Además ver como en ese estudio ver todas las variables que nos hacen falta para de
	//determinar el mejor movimientos posible
	
		//ESTADO PILLS----------------------------------------------------------------------------------------------------------
		public MOVE chooseAny() {
			return !safePaths.isEmpty() ? safePaths.getSafePaths().keySet().iterator().next():MOVE.NEUTRAL;
		}
		
		//ESTADO CHASE / FLEE---------------------------------------------------------------------------------------------------
		
		//Usado en chase / flee
		public MOVE pathWithMoreEdibleGhosts() {
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			Map<MOVE, Integer> edibleGhostsCounts = new HashMap<>();

			for (MOVE move : this.safePaths.getSafePaths().keySet()) {
				int edibleGhostCount = evaluatePathEdibleGhosts(pacmanNode, move, SEARCH_DEPTH);
				edibleGhostsCounts.put(move, edibleGhostCount);
			}

			// Seleccionar el camino con el mayor número de píldoras
			MOVE bestMove = MOVE.NEUTRAL;
			int maxPillCount = Integer.MIN_VALUE;

			for (Map.Entry<MOVE, Integer> entry : edibleGhostsCounts.entrySet()) {
				if (entry.getValue() > maxPillCount) {
					maxPillCount = entry.getValue();
					bestMove = entry.getKey();
				}
			}

			return bestMove;
		}
		public MOVE moveToNearestEdibleGhost() {
			int pacmanNode = info.getPacmanCurrentNodeIndex();
			int min_distance = Integer.MAX_VALUE;
			MOVE solucion = MOVE.NEUTRAL;
			
			for(GHOST ghost: GHOST.values()){
				int distance = this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
				
				if(this.getGame().getGhostEdibleTime(ghost) > 0 && distance < min_distance){
					min_distance = distance;
					solucion = this.getGame().getNextMoveTowardsTarget(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost), info.getPacmanLastMoveMade(), DM.PATH);
				}
				
			}

			return solucion;
		}
		public MOVE moveToSafetyGhost() {
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			Map<MOVE, Integer> safetyGhostsCounts = new HashMap<>();

			for (MOVE move : safePaths.getSafePaths().keySet()) {
				int safetyGhostCount = evaluatePathWithSafetyGhosts(pacmanNode, move, SEARCH_DEPTH);
				safetyGhostsCounts.put(move, safetyGhostCount);
			}

			// Seleccionar el camino con el mayor número de píldoras
			MOVE bestMove = MOVE.NEUTRAL;
			int maxPillCount = Integer.MIN_VALUE;

			for (Map.Entry<MOVE, Integer> entry : safetyGhostsCounts.entrySet()) {
				if (entry.getValue() > maxPillCount) {
					maxPillCount = entry.getValue();
					bestMove = entry.getKey();
				}
			}

			return bestMove;
		}
		
		//ESTADO FLEE------------------------------------------------------------------------------------------------------------
		//Usado en flee y pills
		public MOVE pathWithMorePills() {
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			Map<MOVE, Integer> pathPillCounts = new HashMap<>();

			for (MOVE move : safePaths.getSafePaths().keySet()) {
				int pillCount = evaluatePathPills(pacmanNode, move, SEARCH_DEPTH);
				pathPillCounts.put(move, pillCount);
			}

			// Seleccionar el camino con el mayor número de píldoras
			MOVE bestMove = MOVE.NEUTRAL;
			int maxPillCount = Integer.MIN_VALUE;

			for (Map.Entry<MOVE, Integer> entry : pathPillCounts.entrySet()) {
				if (entry.getValue() > maxPillCount) {
					maxPillCount = entry.getValue();
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
		public MOVE moveToSafetyPath(double range) {
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			this.safePaths = new SafePaths();
			MOVE bestMove = MOVE.NEUTRAL;
			int bestSafetyScore = Integer.MIN_VALUE;

			MOVE[] possiblesMoves = this.getGame().getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
			for (MOVE move : possiblesMoves) {
				int safetyScore = evaluatePathSafety(pacmanNode, move, SEARCH_DEPTH);
				
				if (safetyScore > bestSafetyScore) { // Estudia que camino tiene mejor puntuacion
					bestMove = move;
					bestSafetyScore = safetyScore;
					this.safePaths.addSafePath(bestMove, bestSafetyScore);
				}
			}

			return bestMove;
		}

		
	//METODOS AUXILIARES DE LA CLASE------------------------------------------------------------------------------------------

		//Inicializa un conjunto con los nodos de los fantasmas no comestibles
		private void initGhost(Set<Integer> ghosts){
			for(GHOST ghost: GHOST.values()){
				if(!this.getGame().isGhostEdible(ghost))
				ghosts.add(this.getGame().getGhostCurrentNodeIndex(ghost));
			}
		}

		//Devuelve la distancia minima a CUALQUIER FANTASMA
		private double getEveryTypeClosestGhostDistance(int range){
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		
			int min_distance = Integer.MAX_VALUE;
			
			for(GHOST ghost: GHOST.values()){
				int distancia = this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
				int distance = Math.min(min_distance,distancia);
					min_distance = distance <= range ? distance : min_distance;
			}

			return min_distance;
		}
	
		//Devuelve la distancia minima a un fantasma comestible
		private double edibleGhostCloseDistance(int range){
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
			int min_distance = Integer.MAX_VALUE;
			
			for(GHOST ghost: GHOST.values()){
				int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost)));
				
				if(this.getGame().getGhostEdibleTime(ghost) > 0 && this.getGame().getGhostLairTime(ghost) == 0)
					min_distance = distance <= range ? distance : min_distance;
			}

			return min_distance;
		}

		private double ghostCloseDistance(int range){
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
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
			int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		
			for(int PowerPill: this.getGame().getActivePowerPillsIndices()){
			int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, PowerPill));
				if(distance <= range)
					min_distance = Math.min(min_distance, distance);
			}

			return min_distance;
		}
	
		//Estudia los caminos seguros y los almacenamos
		private void studySecurePaths(){
			this.safePaths = new SafePaths();

			MsPacManInfo info = new MsPacManInfo(this.getGame());
			int currentNode = info.getPacmanCurrentNodeIndex();

			MOVE[] moves = this.getGame().getPossibleMoves(currentNode, info.getPacmanLastMoveMade());
			Map<MOVE, Integer[]> paths = new HashMap<>();

			Set<Integer> ghosts = new HashSet<>();
			initGhost(ghosts);

			for (MOVE move : moves) {
				MOVE currentMove = move;
				int endNode = this.getGame().getNeighbour(currentNode, currentMove);
				int ghostCount = 0;
				int longitud = 0;

				while(endNode != -1 && !this.getGame().isJunction(endNode)){

					if (ghosts.contains(endNode)) {
						ghostCount++;
					}

					currentNode = endNode;
					currentMove = this.game.getPossibleMoves(currentNode, currentMove)[0];
					endNode = this.game.getNeighbour(currentNode, currentMove);
					longitud++;
				}
				
				Integer[] path = {ghostCount, longitud};
				paths.put(move, path);
			}

			// Filtrar los caminos seguros
			for (Map.Entry<MOVE, Integer[]> entry : paths.entrySet()) {
				if (entry.getValue()[0] == 0) { // Considera un camino seguro si no hay fantasmas
					safePaths.addSafePath(entry.getKey(), 0);
				}
			}
		}

		//Devuelve la media de la distancia de la distancia del pacman a cada fantasma por tick de simulacion
		public double getMediaDistaciaSegura() {
			double media = 0;

			for(GHOST ghost: GHOST.values()){
				if(this.getGame().isGhostEdible(ghost) && this.getGame().getGhostLairTime(ghost) == 0){
					media += this.getGame().getShortestPathDistance(this.getGame().getPacmanCurrentNodeIndex(), this.getGame().getGhostCurrentNodeIndex(ghost));
				}
			}

			return media/4;
		}

		//Devuelve si el pacman esta en un nodo de interseccion
		public boolean pacmanRequieresAction(){
			int[] nodes = this.getGame().getJunctionIndices();

			for(int node: nodes){
				if(this.getGame().getPacmanCurrentNodeIndex() == node){
					return true;
				}
			}
			return false;
		}
	
		//BUSQUEDA EN PROFUNDIDAD IMPLEMENTADA CON PILAS PARA EL CAMINO MAS SEGURO
		private int evaluatePathSafety(int startNode, MOVE move, int depth) {
			//Pila que lleva el nodo actual y la profundidad de la busqueda
			Stack<SimpleEntry<Integer, Integer>> stack = new Stack<>();

			stack.push(new SimpleEntry<>(startNode, 0));
			int safetyScore = 0;

			//Estudiamos el camino hasta la profundidad dada
			while (!stack.isEmpty()) {
				SimpleEntry<Integer, Integer> current = stack.pop();
				int currentNode = current.getKey();
				int currentDepth = current.getValue();

				if (currentDepth > depth) {
					continue;
				}

				// Evaluar la seguridad del nodo actual
				safetyScore += evaluateNodeSafety(currentNode, currentDepth);

				// Seguimos con la busqueda 
				for (MOVE nextMove : this.getGame().getPossibleMoves(currentNode)) {
					int nextNode = this.getGame().getNeighbour(currentNode, nextMove);
					if (nextNode != -1) {
						stack.push(new SimpleEntry<>(nextNode, currentDepth + 1));
					}
				}
			}

			return safetyScore;
		}
	
		private int evaluateNodeSafety(int node, int depth) {
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

			safetyScore += depth; //Valorar la longtud del camino, asi se devolvera el mas seguro y el mas largo
        	return safetyScore;
		}

		//BUSQUEDA EN PROFUNDIDAD IMPLEMENTADA PARA PILLS
		private int evaluatePathPills(int startNode, MOVE move, int depth) {
			Stack<SimpleEntry<Integer, Integer>> stack = new Stack<>();
			stack.push(new SimpleEntry<>(startNode, 0));
			int pillCount = 0;
	
			while (!stack.isEmpty()) {
				SimpleEntry<Integer, Integer> current = stack.pop();
				int currentNode = current.getKey();
				int currentDepth = current.getValue();
	
				if (currentDepth > depth) {
					continue;
				}
	
				// Contar las píldoras en el nodo actual
				if (this.getGame().getPillIndex(currentNode) != -1 && this.getGame().isPillStillAvailable(this.getGame().getPillIndex(currentNode))) {
					pillCount++;
				}
	
				// Explorar los vecinos
				for (MOVE nextMove : this.getGame().getPossibleMoves(currentNode)) {
					int nextNode = this.getGame().getNeighbour(currentNode, nextMove);
					if (nextNode != -1) {
						stack.push(new SimpleEntry<>(nextNode, currentDepth + 1));
					}
				}
			}
	
			return pillCount;
		}
	
		//BUSQUEDA EN PROFUNDIDAD PARA LOS FANTASMAS COMESTIBLES
		private int evaluatePathEdibleGhosts(int startNode, MOVE move, int depth){
			Stack<SimpleEntry<Integer, Integer>> stack = new Stack<>();
			stack.push(new SimpleEntry<>(startNode, 0));
			int ghostCount = 0;
	
			while (!stack.isEmpty()) {
				SimpleEntry<Integer, Integer> current = stack.pop();
				int currentNode = current.getKey();
				int currentDepth = current.getValue();
	
				if (currentDepth > depth) {
					continue;
				}
	
				// Contar los fantasmas comestibles en el nodo actual
				for (GHOST ghost : GHOST.values()) {
					if (this.getGame().isGhostEdible(ghost) && this.getGame().getGhostCurrentNodeIndex(ghost) == currentNode) {
						ghostCount++;
					}
				}
	
				// Explorar los vecinos
				for (MOVE nextMove : this.getGame().getPossibleMoves(currentNode)) {
					int nextNode = this.getGame().getNeighbour(currentNode, nextMove);
					if (nextNode != -1) {
						stack.push(new SimpleEntry<>(nextNode, currentDepth + 1));
					}
				}
			}
	
			return ghostCount;
		}

		//BUSQUEDA EN PROFUNDIDAD PARA LOS FANTASMAS COMESTBILES MAS SEGUROS
		private int evaluatePathWithSafetyGhosts(int startNode, MOVE move, int depth){
			Stack<SimpleEntry<Integer, Integer>> stack = new Stack<>();
			stack.push(new SimpleEntry<>(startNode, 0));
			int ghostCount = 0;
	
			while (!stack.isEmpty()) {
				SimpleEntry<Integer, Integer> current = stack.pop();
				int currentNode = current.getKey();
				int currentDepth = current.getValue();
	
				if (currentDepth > depth) {
					continue;
				}
	
				// Contar los fantasmas comestibles en el nodo actual
				for (GHOST ghost : GHOST.values()) {
					if (!this.getGame().isGhostEdible(ghost) && this.getGame().getGhostCurrentNodeIndex(ghost) == currentNode) {
						ghostCount-= 2;
					}
					else if(this.getGame().getGhostCurrentNodeIndex(ghost) == currentNode){
						ghostCount++;
					}
				}
	
				// Explorar los vecinos
				for (MOVE nextMove : this.getGame().getPossibleMoves(currentNode)) {
					int nextNode = this.getGame().getNeighbour(currentNode, nextMove);
					if (nextNode != -1) {
						stack.push(new SimpleEntry<>(nextNode, currentDepth + 1));
					}
				}
			}
	
			return ghostCount;
		}
	}
