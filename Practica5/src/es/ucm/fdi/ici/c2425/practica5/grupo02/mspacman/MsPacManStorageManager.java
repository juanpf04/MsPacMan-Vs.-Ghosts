package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManStorageManager {

	private Game game;

	private CBRCaseBase caseBase;
	private NNConfig simConfig;

	private Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 3;
	private final static int MAX_DISTANCE = 75;

	public MsPacManStorageManager() {
		this.buffer = new Vector<CBRCase>();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setCaseBase(CBRCaseBase caseBase) {
		this.caseBase = caseBase;
	}

	public void setSimConfig(NNConfig simConfig) {
		this.simConfig = simConfig;
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

		//TODO puntuar el cambiuo de cada atributo para darle un valor a la revision
		// Resultados de las metricas
		result.setScore(metrica_score(description));
//		result.setEdibleTime(metrica_time_edible_ghost(description));
//		result.setPpillDistance(metrica_powerpills_distance(description));
//		result.setPillDistance(metrica_pills_distance(description));
//		result.setEdibleGhostDistance(metrica_nearest_edible_ghost_distance(description));
//		result.setGhostDistance(metrica_nearest_ghost_distance(description));
//		result.setEdibleGhosts(metrica_number_edible_ghosts(description));
//		result.setJailGhosts(metrica_number_jail_ghosts(description));
//		result.setRelativePosGhost(metrica_pos_relative_ghost(description));
//		result.setRelativePosEdibleGhost(metrica_pos_relative_edible_ghost(description));
	}

	private void retainCase(CBRCase bCase) {
		// Store the old case right now into the case base
		// Alternatively we could store all them when game finishes in close() method

		// here you should also check if the case must be stored into persistence (too
		// similar to existing ones, etc.)

		MsPacManResult result = (MsPacManResult) bCase.getResult();
		
		
		double similary = 0.0;
		// Check if the case is similar to another one
		if(similary >= 0.95)
		// si existen casos similares
		// si conseguimos un mayor resultado, eliminar el caso antiguo
		//si no, no lo almacenamos el nuevo

		// si lo debemos almacenar
		if (result.getScore() >= 100 || true) // comprobar si se ha alejao de los ghost, o acercado a ppill y todo eso 
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

	// FUNCOINES QUE MIDEN LA EFECTIVIDAD DE CADA TRIBUTO DECLARADO
	private int metrica_score(MsPacManDescription description) {
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		
		System.out.println("Score: " + (currentScore - oldScore));
		
		return currentScore - oldScore;

	}

	// Metrica para comparar el numero de fantasmas comestibles
	private int metrica_number_edible_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getEdibleGhosts();
		int currentEdibleGhostsNumber = number_edible_ghosts();
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}

	private int number_edible_ghosts() {
		int number = 0;

		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g))
				number++;
		}

		return number;
	}

	// Metrica sobre la distancia del fantasma comestible mas cercano
	private int metrica_nearest_edible_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getEdibleGhostDistance();
		int currentDistance = current_min_distance_to_nearest_edible_ghost();
		return currentDistance - oldDistance;
	}

	private int current_min_distance_to_nearest_edible_ghost() {
		int currentIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMade = game.getPacmanLastMoveMade();

		int minDistance = Integer.MAX_VALUE;

		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g))
				minDistance = Math.min(minDistance,
						game.getShortestPathDistance(currentIndex, minDistance, lastMoveMade));
		}

		return minDistance;
	}

	// Metrica sobre la distancia del fantasma no comestible mas cercano
	private int metrica_nearest_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getGhostDistance();
		int currentDistance = current_min_distance_to_nearest_ghost();
		return currentDistance - oldDistance;
	}

	private int current_min_distance_to_nearest_ghost() {
		int currentIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMade = game.getPacmanLastMoveMade();

		int minDistance = Integer.MAX_VALUE;

		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g))
				minDistance = Math.min(minDistance,
						game.getShortestPathDistance(currentIndex, minDistance, lastMoveMade));
		}

		return minDistance;
	}

	// Metrica sobre pills mas cercanas
	private int metrica_pills_distance(MsPacManDescription description) {
		int oldPillDistance = description.getPillDistance();
		int currentPillDistance = pacman_pill();
		return currentPillDistance - oldPillDistance;
	}

	private int pacman_pill() {
		int currentMsPacManIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMoveMadeByMsPacMan = game.getPacmanLastMoveMade();

		int min = Integer.MAX_VALUE;
		int pillCercana = -1;
		for (int pillIndex : game.getActivePillsIndices()) {
			int distance = game.getShortestPathDistance(currentMsPacManIndex, pillIndex, lastMoveMadeByMsPacMan);
			if (distance < min) {
				min = distance;
			}
		}
		return min;
	}

	// Metrica sobre la distancia de la powerpill mas cercana
	private int metrica_powerpills_distance(MsPacManDescription description) {
		int oldDistance = description.getPpillDistance();
		int currentDistance = pacman_powerpill();
		return currentDistance - oldDistance;
	}

	private int pacman_powerpill() {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		int min = Integer.MAX_VALUE;

		for (int powerpill : game.getActivePowerPillsIndices()) {
			min = Math.min(game.getShortestPathDistance(pacmanIndex, powerpill, lastMove), min);
		}

		return min;
	}

	// Metrica para comparar el numero de fantasmas en la jaula
	private int metrica_number_jail_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getJailGhosts();
		int currentEdibleGhostsNumber = number_jail_ghosts();
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}

	private int number_jail_ghosts() {
		int number = 0;

		for (GHOST g : GHOST.values()) {
			if (game.getGhostLairTime(g) > 0)
				number++;
		}

		return number;
	}

	// Metrica posicion relativa a un edible ghost setRelativePosEdibleGhost
	private int metrica_pos_relative_edible_ghost(MsPacManDescription description) {
		POS oldPosRelative = (POS) description.getRelativePosEdibleGhost();
		POS currentPosRelative = posicion_relativa(true);

		if (oldPosRelative == currentPosRelative) {
			return 1;
		} else {
			if ((oldPosRelative == POS.BOTH
					&& (currentPosRelative == POS.FRONT || currentPosRelative == POS.BACK)
					|| (currentPosRelative == POS.BOTH && (oldPosRelative == POS.FRONT
							|| oldPosRelative == POS.BACK)))) {

				return 1;
			} else {
				return 0;
			}
		}
	}

	// Metrica posicion relativa a un ghost setRelativePoGhost
	private int metrica_pos_relative_ghost(MsPacManDescription description) {
		POS oldPosRelative = (POS) description.getRelativePosEdibleGhost();
		POS currentPosRelative = posicion_relativa(false);

		if (oldPosRelative == currentPosRelative) {
			return 1;
		} else {
			if ((oldPosRelative == POS.BOTH
					&& (currentPosRelative == POS.FRONT || currentPosRelative == POS.BACK)
					|| (currentPosRelative == POS.BOTH && (oldPosRelative == POS.FRONT
							|| oldPosRelative == POS.BACK)))) {

				return 1;
			} else {
				return 0;
			}
		}
	}

	private POS posicion_relativa(boolean edible) {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		MOVE lastMoveOpposite = lastMove.opposite();
		boolean delante = false, detras = false;

		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost)) {
				if (game.isGhostEdible(ghost) && edible) {
					// Funcion para calcular la funcion delante y detras
					int ghostIndex = game.getGhostCurrentNodeIndex(ghost);

					int[] pathToGhostDelante = game.getShortestPath(pacmanIndex, ghostIndex, lastMove);
					int[] pathToGhostDetras = game.getShortestPath(pacmanIndex, ghostIndex, lastMoveOpposite);
					if (pathToGhostDelante.length > 0 && pathToGhostDelante.length <= MAX_DISTANCE) {
						delante = true;
					} else if (pathToGhostDetras.length > 0 && pathToGhostDetras.length <= MAX_DISTANCE) {
						detras = true;
					}
				}
			}
		}

		if (delante && detras) {
			return POS.BOTH;
		} else if (delante || detras) {
			if (delante) {
				return POS.FRONT;
			} else {
				return POS.BACK;
			}
		}
		return POS.NONE;
	}

	// Metrica para el tiempo de los fantasmas comestibles
	private int metrica_time_edible_ghost(MsPacManDescription description) {
		int oldTime = description.getEdibleTime();
		int currentTime = timeEdibleGhost();
		return currentTime - oldTime;
	}

	private int timeEdibleGhost() {
		int result = 0;

		for (GHOST g : GHOST.values()) {
			result = Math.max(game.getGhostEdibleTime(g), result);
		}
		return result;
	}

}
