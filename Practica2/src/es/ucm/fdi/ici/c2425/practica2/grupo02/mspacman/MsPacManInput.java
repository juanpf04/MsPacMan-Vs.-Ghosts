package es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class MsPacManInput extends Input {

	private MsPacManInfo info;
	public MsPacManInput(Game game) {
		super(game);
		info = new MsPacManInfo(game);
	}

	@Override
	public void parseInput() {
		// does nothing.

	}

	//TRANSITIONS

    public boolean edibleGhostNearestThanGhost(int range) {
        return edibleGhostCloseDistance(range) < getClosestNotEdibleGhostDistance(range);
    }
	
	public boolean pacmanEatPowerPill(){

		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
        for(int PowerPill: this.getGame().getActivePowerPillsIndices()){
            return pacmanNode == PowerPill || Math.abs(pacmanNode - PowerPill) <= 2;
        }

        return false;
	}

	public boolean getClosestNotEdibleGhost(int range) {
		return getClosestNotEdibleGhostDistance(range) != Integer.MAX_VALUE;
    }
	
	public boolean withoutEdibleTime() {
		boolean ghostEdible = true;
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

	public boolean edibleGhostClose() {
		return edibleGhostCloseDistance(30) != Integer.MAX_VALUE;
    }

	public boolean powerPillCloserThanGhost(int range) {
		return powerPillDistance(range) < getClosestNotEdibleGhostDistance(range);
	}

	public boolean sameNumberOfEdibleGhostInEachPath(){
		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		Map<MOVE, Integer> paths = new HashMap<MOVE, Integer>();

		for(GHOST ghost: GHOST.values()){

			if(edibleGhostClose()){
				MOVE move = (MOVE) this.getGame().getMoveToMakeToReachDirectNeighbour(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost));
				
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

	public boolean sameNumberOfPills() {
		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		MOVE lastMove = (MOVE) this.getGame().getPacmanLastMoveMade();

		Map<MOVE, Integer> numberPills = new HashMap<MOVE, Integer>();

		MOVE[] moves = (MOVE[]) this.getGame().getPossibleMoves(pacmanNode, (pacman.game.Constants.MOVE) lastMove);

		for(MOVE move: moves){
			int nPills = 0;
			int endNode = this.getGame().getNeighbour(pacmanNode, (pacman.game.Constants.MOVE) move);

			while(!this.getGame().isJunction(endNode)){
				
				if(this.getGame().isPillStillAvailable(endNode)) nPills++;

				endNode = this.getGame().getNeighbour(endNode, (pacman.game.Constants.MOVE) move);

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
		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		MOVE lastMove = (MOVE) this.getGame().getPacmanLastMoveMade();

		MOVE[] moves = (MOVE[]) this.getGame().getPossibleMoves(pacmanNode, (pacman.game.Constants.MOVE) lastMove);
		
		//Guardo indices de los fantasmas no comestible actualmente
		Set<Integer> ghosts = new HashSet<Integer>();
		initGhost(ghosts);

		//Solucion, si la clave true tiene mas de un entero asociado, hay mas de un camino seguido
		Map<Boolean, Integer> sol = new HashMap<Boolean, Integer>();

		for(MOVE move: moves){
			
			int endNode = this.getGame().getNeighbour(pacmanNode, (pacman.game.Constants.MOVE) move);
			boolean safetyPath = true;

			//Avanzamos por el camino del movimiento actual, ademas cortamos si nuestro camino no es seguro (eficiencia)
			while (!this.getGame().isJunction(endNode) && safetyPath) {

				//Si ese nodo esta en ghosts, es porque hay un fantasma no comestible
				if(ghosts.contains(endNode)){
					safetyPath = false;
				}

				//Avanzo nodo en el camino
				endNode = this.getGame().getNeighbour(pacmanNode, (pacman.game.Constants.MOVE) move);
			}
			
			if(!sol.containsKey(safetyPath))sol.put(safetyPath, 1);
			else sol.put(safetyPath, sol.get(safetyPath) + 1);
		}

		return sol.get(true) > 1;
	}

	private void initGhost(Set<Integer> ghosts){
		for(GHOST ghost: GHOST.values()){
			if(!this.getGame().isGhostEdible(ghost))
			 ghosts.add(this.getGame().getGhostCurrentNodeIndex(ghost));
		}
	}

	private void initEdibleGhost(Set<Integer> ghosts){
		for(GHOST ghost: GHOST.values()){
			if(this.getGame().isGhostEdible(ghost))
			 ghosts.add(this.getGame().getGhostCurrentNodeIndex(ghost));
		}
	}
	
	private double getClosestNotEdibleGhostDistance(int range){
		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
	
		int min_distance = Integer.MAX_VALUE;
		
		for(GHOST ghost: GHOST.values()){
			int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost)));
			
			if(this.getGame().getGhostEdibleTime(ghost) == 0)
				min_distance = distance <= range ? distance : min_distance;
		}

		return min_distance;
	}
   
	private double edibleGhostCloseDistance(int range){
		int pacmanNode = this.getGame().getPacmanCurrentNodeIndex();
		int min_distance = Integer.MAX_VALUE;
		
		for(GHOST ghost: GHOST.values()){
			int distance = Math.min(min_distance, this.getGame().getShortestPathDistance(pacmanNode, this.getGame().getGhostCurrentNodeIndex(ghost)));
			
			if(this.getGame().getGhostEdibleTime(ghost) > 0)
				min_distance = distance <= range ? distance : min_distance;
		}

		return min_distance;
	}

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

	//ACTIONS
	//Para el estudio de los caminos tenog que ver como reutilizar el codigo del backtracking
	//Ya que asi estudio hasta 3 nodos de profundidad. Además ver como en ese estudio ver todas las variables que nos hacen falta para de
	//determinar el mejor movimientos posible

    public MOVE pathWithMoreEdibleGhosts(SafePaths safePaths) {
		int pacmanNode = info.getPacmanCurrentNodeIndex();
		
		MOVE solucion = MOVE.NEUTRAL;

		//Inicio las posiciones de los fantasmas
		Set<Integer> ghosts = new HashSet<Integer>();
		initEdibleGhost(ghosts);

		//Posibles movimientos del pacman segun su ultimo movimiento y posicion
		MOVE[] moves = this.getGame().getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());

		int max_ghosts = 0;
		for(MOVE move: moves){

			//Si el camino es seguro, lo estudio, sino paso a otro camino
			if(safePaths.getSafePaths().get(move) == 0){

				int endNode = this.getGame().getNeighbour(pacmanNode, move);
				int numerGhosts = 0;

				while (!this.getGame().isJunction(endNode)) {

					if(ghosts.contains(endNode)){
						numerGhosts++;
					}
					
					endNode = this.getGame().getNeighbour(endNode, (pacman.game.Constants.MOVE) move);
				}

				if(max_ghosts < numerGhosts){
					max_ghosts = numerGhosts;
					solucion = move;

					//Si en un camino estan los cuatro o la mayoria de ellos, ir por ese sin estudiar los demas. No estudiamos todos los caminos, + eficiencia
					if(max_ghosts >= 3) return solucion;
				}
			}
		}

		return solucion;
    }

	//Revisar: Solo calculo la distancia minima a un fantasma comestible sin tener en cuenta que puede haber un fantasma no comestible mas cerca de ese fantasma/de mí
	//Deberia estudiar los movimientos otra vez en lugar de solo los fantasmas
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
		int pacmanNode = info.getPacmanCurrentNodeIndex();
		MOVE[] moves = this.getGame().getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());

		Set<Integer> ghosts = new HashSet<Integer>();
		initGhost(ghosts);

		Set<Integer> edibleGhosts = new HashSet<Integer>();
		initEdibleGhost(edibleGhosts);

		MOVE solucion = MOVE.NEUTRAL;

		for(MOVE move: moves){
			int nEdibleGhosts = 0;
			int nGhosts = 0;
			int endNode = this.getGame().getNeighbour(pacmanNode, move);

			while (!this.getGame().isJunction(endNode)) {

				if(ghosts.contains(endNode)){
					nGhosts++;
				}
				else if(edibleGhosts.contains(endNode)){
					nEdibleGhosts++;
				}

				endNode = this.getGame().getNeighbour(pacmanNode, move);
			}
			
			if(nGhosts == 0 && nEdibleGhosts >= 1){
				solucion = move;
			}
		}

		return solucion;
	}

	public MOVE pathWithMorePills(SafePaths safePaths) {
		int pacmanNode = info.getPacmanCurrentNodeIndex();
		MOVE solucion = MOVE.NEUTRAL;

		MOVE[] moves = this.getGame().getPossibleMoves(pacmanNode, info.getPacmanLastMoveMade());
		int max_pills = Integer.MIN_VALUE;

		for(MOVE move: moves){

			if(safePaths.getSafePaths().get(move) == 0){
				int endNode = this.getGame().getNeighbour(pacmanNode, move);
				int nPills = 0;

				while (!this.getGame().isJunction(endNode)) {

					if(this.getGame().isPillStillAvailable(endNode)){
						nPills++;
					}

					endNode = this.getGame().getNeighbour(endNode, move);
				}

				if(max_pills < nPills){
					max_pills = nPills;
					solucion = move;
				}
			}
		}

		return solucion;
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

	public MOVE moveToSafetyPath(SafePaths safePaths) {
		MsPacManInfo info = new MsPacManInfo(this.getGame());
		int pacmanNode = info.getPacmanCurrentNodeIndex();

        Map<MOVE, Integer[]> paths = new HashMap<>();

		Set<Integer> ghosts = new HashSet<>();
		initGhost(ghosts);

        for (MOVE move : MOVE.values()) {
            int endNode = this.getGame().getNeighbour(pacmanNode, move);
            int ghostCount = 0;
			int longitud = 0;
			while(!this.getGame().isJunction(endNode)){

				if (ghosts.contains(endNode)) {
					ghostCount++;
				}

				endNode = this.getGame().getNeighbour(pacmanNode, move);
				longitud++;
			}
            
			Integer[] path = {ghostCount, longitud};
            paths.put(move, path);
        }

        // Filtrar los caminos seguros
        for (Map.Entry<MOVE, Integer[]> entry : paths.entrySet()) {
            if (entry.getValue()[0] == 0) { // Considera un camino seguro si no hay fantasmas
                safePaths.addSafePath(entry.getKey(), entry.getValue()[0]);
            }
        }

        // Seleccionar el camino más seguro
        MOVE safestMove = MOVE.NEUTRAL;
        int minGhosts = Integer.MAX_VALUE;
		int longitud = Integer.MIN_VALUE;
        for (Map.Entry<MOVE, Integer[]> entry : paths.entrySet()) {
			int ghostCount = entry.getValue()[0];
			int pathLength = entry.getValue()[1];

			if (ghostCount < minGhosts || (ghostCount == minGhosts && pathLength > longitud)) {
                minGhosts = ghostCount;
				longitud = pathLength;

                safestMove = entry.getKey();
            }
        }

        return safestMove;
	}

	


}
