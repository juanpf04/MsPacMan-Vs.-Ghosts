package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import es.ucm.fdi.ici.c2425.practica5.grupo02.similitud.RelativePosition;

public class MsPacManStorageManager {

	private Game game;
	private CBRCaseBase caseBase;
	private Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 3;

	public MsPacManStorageManager() {
		this.buffer = new Vector<CBRCase>();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setCaseBase(CBRCaseBase caseBase) {
		this.caseBase = caseBase;
	}

	public void reviseAndRetain(CBRCase newCase) {
		this.buffer.add(newCase);

		// Buffer not full yet.
		if (this.buffer.size() < TIME_WINDOW)
			return;

		CBRCase bCase = this.buffer.remove(0);
		reviseCase(bCase);
		retainCase(bCase);

	}

	private void reviseCase(CBRCase bCase) {
		MsPacManDescription description = (MsPacManDescription) bCase.getDescription();
		
		MsPacManResult result = (MsPacManResult) bCase.getResult();
		
		//Resultados de las metricas
		result.setScore(metrica_score(description));
		result.setEdibleGhosts(metrica_number_edible_ghosts(description));
		result.setNearestEdibleGhostDistance(metrica_nearest_edible_ghost_distance(description));
		result.setNearestGhostDistance(metrica_nearest_ghost_distance(description));
		result.setNearestPillDistance(metrica_pills_distance(description));
		result.setNearestPPillDistance(metrica_powerpills_distance(description));
		result.setNumberJailGhosts(metrica_number_jail_ghosts(description));
		result.setRelativePosEdibleGhost(null);
		result.setRelativePosGhost(null);
		result.setTimeEdibleGhost(null);
	}

	private void retainCase(CBRCase bCase) {
		// Store the old case right now into the case base
		// Alternatively we could store all them when game finishes in close() method

		// here you should also check if the case must be stored into persistence (too
		// similar to existing ones, etc.)
		
		MsPacManResult result = (MsPacManResult) bCase.getResult();
		
		if (result.getScore() > 0 && similar(result)) { // si es un caso similar y obtiene mas puntos
			List<CBRCase> cases = new ArrayList<CBRCase>();
			cases.add(bCase);
			this.caseBase.learnCases(cases);
		}
		
		if(game.getScore() > 0) // si los puntos son positivos
			StoreCasesMethod.storeCase(this.caseBase, bCase);
	}
	
	private boolean similar(MsPacManResult result) { // TODO 
		boolean similar = true;
		
		similar &= true;
		similar &= true;
		similar &= true;
		similar &= true;
		similar &= true;
		similar &= true;
		similar &= true;
		similar &= true;
		
		return similar;
	}

	public void close() {
		for (CBRCase oldCase : this.buffer) {
			reviseCase(oldCase);
			retainCase(oldCase);
		}
		this.buffer.removeAllElements();
	}

	public int getPendingCases() {
		return this.buffer.size();
	}
	
	//FUNCOINES QUE MIDEN LA EFECTIVIDAD DE CADA TRIBUTO DECLARADO
	private int metrica_score(MsPacManDescription description) {
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		return currentScore - oldScore;
		
	}
	
	//Metrica para comparar el numero de fantasmas comestibles
	private int metrica_number_edible_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getEdibleGhosts();
		int currentEdibleGhostsNumber = number_edible_ghosts();
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}
	private int number_edible_ghosts() {
		int number = 0;
		
		for(GHOST g:GHOST.values()) {
			if(game.isGhostEdible(g)) number++;
		}
		
		return number;
	}
	
	//Metrica sobre la distancia del fantasma comestible mas cercano
	private int metrica_nearest_edible_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getNearestEdibleGhostDistance();
		int currentDistance = current_min_distance_to_nearest_edible_ghost();
		return currentDistance - oldDistance;
	}
	private int current_min_distance_to_nearest_edible_ghost() {
		int currentIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMade = game.getPacmanLastMoveMade();
		
		int minDistance = Integer.MAX_VALUE;
		
		for(GHOST g: GHOST.values()) {
			if(game.isGhostEdible(g))
				minDistance = Math.min(minDistance, game.getShortestPathDistance(currentIndex, minDistance, lastMoveMade));
		}
		
		return minDistance;
	}
	
	//Metrica sobre la distancia del fantasma no comestible mas cercano
	private int metrica_nearest_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getNearestGhostDistance();
		int currentDistance = current_min_distance_to_nearest_ghost();
		return currentDistance - oldDistance;
	}
	private int current_min_distance_to_nearest_ghost() {
		int currentIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMade = game.getPacmanLastMoveMade();
		
		int minDistance = Integer.MAX_VALUE;
		
		for(GHOST g: GHOST.values()) {
			if(!game.isGhostEdible(g))
				minDistance = Math.min(minDistance, game.getShortestPathDistance(currentIndex, minDistance, lastMoveMade));
		}
		
		return minDistance;
	}
	
	
	//Metrica sobre pills mas cercanas
	private int metrica_pills_distance(MsPacManDescription description) {
		int oldPillDistance = description.getNearestPillDistance();
		int currentPillDistance = pacman_pill();
		return currentPillDistance - oldPillDistance;
	}
	private int pacman_pill() {
		int currentMsPacManIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMadeByMsPacMan = game.getPacmanLastMoveMade();
		
		int min = Integer.MAX_VALUE;
		int pillCercana = -1;
		for(int pillIndex : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(currentMsPacManIndex, pillIndex, lastMoveMadeByMsPacMan);
			if(distance < min) {
				min = distance;
			}
		}
		return min;
	}
	
	//Metrica sobre la distancia de la powerpill mas cercana
	private int metrica_powerpills_distance(MsPacManDescription description) {
		int oldDistance = description.getNearestPPillDistance();
		int currentDistance = pacman_powerpill();
		return currentDistance - oldDistance;
	}
	private int pacman_powerpill() {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		int min = Integer.MAX_VALUE;
		
		for(int powerpill : game.getActivePowerPillsIndices()) {
			min = Math.min(game.getShortestPathDistance(pacmanIndex,powerpill, lastMove), min);
		}
		
		return min;
	}
	
	//Metrica para comparar el numero de fantasmas en la jaula
	private int metrica_number_jail_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getNumberJailGhosts();
		int currentEdibleGhostsNumber = number_jail_ghosts();
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}
	private int number_jail_ghosts() {
		int number = 0;
		
		for(GHOST g:GHOST.values()) {
			if(game.getGhostLairTime(g) > 0) number++;
		}
		
		return number;
	}
	
	//Metrica posicion relativa a un edible ghost setRelativePosEdibleGhost
	private int metrica_pos_relative_edible_ghost(MsPacManDescription description) {
		RelativePosition oldPosRelative = (RelativePosition) description.getRelativePosEdibleGhost();
		RelativePosition currentPosRelative =posicion_relativa_edible();
		return 0;
	}
	
	private RelativePosition posicion_relativa_edible() {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		MOVE lastMoveOpposite = lastMove.opposite();
		
		//Funcion para calcular la funcion delante y detras
		
		//Si esta delante, Indicar delante
		//Si esta detras, indicar detras
		//Si esta en a,bas, indicar ambas
		//Si no hay ninguno, indicar ninguno
		
		return RelativePosition.AMBOS;
	}
}
