package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.ici.c2425.practica5.grupo02.POS;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManInput.MsPacManInfo;

public class MsPacManStorageManager {

	private MsPacManInfo info;

	private CBRCaseBase caseBase;
	private NNConfig simConfig;

	private Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 3;

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

		int score = metrica_score(description); // por ahora solo se mira el score

		// TODO puntuar el cambio de cada atributo para darle un valor a la revision
		// y valorar si el cambio es positivo o negativo

		// El score se basara en la mejra de puntos, mayor distancia a los fantasmas,
		// menor distancia a las pastillas, etc.
		
		// por ejemplo score = score + X*si se ha alejado de los fantasmas + Y*si se ha acercado a las pills
		// + Z*si se ha alejado de las powerpills - W*si se ha alejado de los fantasmas comestibles
		
		// Adjust score based on the situation
		score += 5 * metrica_number_edible_ghosts(description);  // Encourages capturing edible ghosts
		score -= 3 * metrica_nearest_edible_ghost_distance(description);  // Prefers proximity to edible ghosts
		score += 3 * metrica_nearest_ghost_distance(description);  // Prefers distance from non-edible ghosts
		score -= 2 * metrica_pills_distance(description);  // Prefers proximity to normal pills
		score -= 4 * metrica_powerpills_distance(description);  // Prefers proximity to power pills
		score += 2 * metrica_number_jail_ghosts(description);  // Rewards sending ghosts to the jail
		score += 3 * metrica_pos_relative_edible_ghost(description);  // Positional advantage near edible ghosts
		score -= 2 * metrica_pos_relative_ghost(description);  // Penalizes positional disadvantage near non-edible ghosts
		score += 1 * metrica_time_edible_ghost(description);  // Rewards more time for edible ghosts
		

		result.setScore(score);
	}

	private void retainCase(CBRCase bCase) {
		MsPacManResult newResult = (MsPacManResult) bCase.getResult();
		List<CBRCase> toForget = new ArrayList<CBRCase>();
		boolean better = false, similar = false;

		// Retrieve similarity scores for the new case against the case base
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(this.caseBase.getCases(), bCase,
				this.simConfig);

		Iterator<RetrievalResult> topCases = SelectCases.selectTopKRR(eval, 5).iterator();

		while (topCases.hasNext()) {
			RetrievalResult retrieval = topCases.next();
			CBRCase similarCase = retrieval.get_case();
			double similarity = retrieval.getEval();

			if (similarity >= 0.95) {
				MsPacManResult oldResult = (MsPacManResult) similarCase.getResult();
				if (oldResult.getScore() < newResult.getScore()) {
					toForget.add(similarCase);
					better = true;
				}
				similar = true;
			}
		}

		// If there are no similar cases or the new case is better than the similar
		// cases, store the new case
		if (!similar || better)
			StoreCasesMethod.storeCase(this.caseBase, bCase);

		// Forget the similar cases
		this.caseBase.forgetCases(toForget);
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
			if ((oldPosRelative == POS.BOTH && (currentPosRelative == POS.FRONT || currentPosRelative == POS.BACK)
					|| (currentPosRelative == POS.BOTH
							&& (oldPosRelative == POS.FRONT || oldPosRelative == POS.BACK)))) {

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
			if ((oldPosRelative == POS.BOTH && (currentPosRelative == POS.FRONT || currentPosRelative == POS.BACK)
					|| (currentPosRelative == POS.BOTH
							&& (oldPosRelative == POS.FRONT || oldPosRelative == POS.BACK)))) {

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
