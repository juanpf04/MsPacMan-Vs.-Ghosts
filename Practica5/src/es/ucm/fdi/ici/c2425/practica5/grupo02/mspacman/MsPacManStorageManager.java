package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

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
		result.setNearestEdibleGhostDistance(null);
		result.setNearestGhostDistance(null);
		result.setNearestPillDistance(metrica_pills_distance(description));
		result.setNearestPPillDistance(null);
		result.setNumberJailGhosts(null);
		result.setRelativePosEdibleGhost(null);
		result.setRelativePosGhost(null);
		result.setTimeEdibleGhost(null);
	}

	private void retainCase(CBRCase bCase) {
		// Store the old case right now into the case base
		// Alternatively we could store all them when game finishes in close() method

		// here you should also check if the case must be stored into persistence (too
		// similar to existing ones, etc.)
		

		StoreCasesMethod.storeCase(this.caseBase, bCase);
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
				pillCercana = pillIndex;
			}
		}
		
		return pillCercana;
	}
}
