package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManInput.MsPacManInfo;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManStorageManager {

	private MsPacManInfo info;

	private CBRCaseBase caseBase;
	private NNConfig simConfig;

	private Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 3;
	private final static int MAX_DISTANCE = 75;

	public MsPacManStorageManager(MsPacManInfo info) {
		this.buffer = new Vector<CBRCase>();
		
		this.info = info;
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

		
		int score = 0;
		
		//TODO puntuar el cambio de cada atributo para darle un valor a la revision	
		// y valorar si el cambio es positivo o negativo
		
		// El score se basara en la mejra de puntos, mayor distancia a los fantasmas, menor distancia a las pastillas, etc.
		
		result.setScore(score);
	}

	private void retainCase(CBRCase bCase) {
		MsPacManResult result = (MsPacManResult) bCase.getResult();
		
		// TODO
		// Calcular la similaridad con los casos de la base de casos
		// si no hay casos similares, almacenar el caso si el resultado es bueno
		// si hay casos similares, comparar el resultado con los resultados de los casos similares
		// si el resultado es mejor, almacenar el caso y eliminar los casos anteriores
		// si no, no almacenar el caso
		
		
		double similary = 0.0;

		if(similary >= 0.95)

		if (result.getScore() > 0) 
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
		int currentScore = this.info.score;
		
		return currentScore - oldScore;

	}

	// Metrica para comparar el numero de fantasmas comestibles
	private int metrica_number_edible_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getEdibleGhosts();
		int currentEdibleGhostsNumber = this.info.edibleGhosts;
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}

	// Metrica sobre la distancia del fantasma comestible mas cercano
	private int metrica_nearest_edible_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getEdibleGhostDistance();
		int currentDistance = this.info.edibleGhostDistance;
		return currentDistance - oldDistance;
	}

	// Metrica sobre la distancia del fantasma no comestible mas cercano
	private int metrica_nearest_ghost_distance(MsPacManDescription description) {
		int oldDistance = description.getGhostDistance();
		int currentDistance = this.info.ghostDistance;
		return currentDistance - oldDistance;
	}


	// Metrica sobre pills mas cercanas
	private int metrica_pills_distance(MsPacManDescription description) {
		int oldPillDistance = description.getPillDistance();
		int currentPillDistance = this.info.pillDistance;
		return currentPillDistance - oldPillDistance;
	}


	// Metrica sobre la distancia de la powerpill mas cercana
	private int metrica_powerpills_distance(MsPacManDescription description) {
		int oldDistance = description.getPpillDistance();
		int currentDistance = this.info.ppillDistance;
		return currentDistance - oldDistance;
	}

	// Metrica para comparar el numero de fantasmas en la jaula
	private int metrica_number_jail_ghosts(MsPacManDescription description) {
		int oldEdibleGhostsNumber = description.getJailGhosts();
		int currentEdibleGhostsNumber = this.info.jailGhosts;
		return currentEdibleGhostsNumber - oldEdibleGhostsNumber;
	}


	// Metrica posicion relativa a un edible ghost setRelativePosEdibleGhost
	private int metrica_pos_relative_edible_ghost(MsPacManDescription description) {
		POS oldPosRelative = (POS) description.getRelativePosEdibleGhost();
		POS currentPosRelative = this.info.relativePosEdibleGhost;

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
		POS currentPosRelative = this.info.relativePosGhost;

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


	// Metrica para el tiempo de los fantasmas comestibles
	private int metrica_time_edible_ghost(MsPacManDescription description) {
		int oldTime = description.getEdibleTime();
		int currentTime = this.info.edibleTime;
		return currentTime - oldTime;
	}

}
