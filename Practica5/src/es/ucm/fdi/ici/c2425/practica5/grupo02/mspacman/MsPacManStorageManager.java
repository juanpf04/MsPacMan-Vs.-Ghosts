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
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.MsPacManInput.MsPacManInfo;

public class MsPacManStorageManager {

	private MsPacManInfo info;

	private CBRCaseBase caseBase;
	private NNConfig simConfig;

	private Vector<CBRCase> buffer;

	private final static int TIME_WINDOW = 5;

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

		int initialScore = description.getScore();
		int currentScore = this.info.score;

		// Calculating the overall revised score
		int revisedScore = (currentScore - initialScore) * 3;

		revisedScore += computePillsChange(description.getPillDistance(), this.info.pillDistance);
		revisedScore += computePPillsChange(description.getPpillDistance(), this.info.ppillDistance);
		revisedScore += computeGhostChange(description.getGhostDistance(), this.info.ghostDistance);
		revisedScore += computeEdibleGhostChange(description.getEdibleGhostDistance(), this.info.edibleGhostDistance);
		revisedScore += computeEdibleGhostCountChange(description.getEdibleGhosts(), this.info.edibleGhosts);
		revisedScore += computeJailGhostCountChange(description.getJailGhosts(), this.info.jailGhosts);

		// Update the case result with the revised score
		result.setScore(revisedScore);
	}

	private void retainCase(CBRCase bCase) {
		MsPacManResult newResult = (MsPacManResult) bCase.getResult();
		List<CBRCase> toForget = new ArrayList<CBRCase>();
		boolean better = false, similar = false;

		// Retrieve similarity scores for the new case against the case base
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(this.caseBase.getCases(), bCase,
				this.simConfig);

		Iterator<RetrievalResult> topCases = SelectCases.selectTopKRR(eval, 10).iterator();

		while (topCases.hasNext()) {
			RetrievalResult retrieval = topCases.next();
			CBRCase similarCase = retrieval.get_case();
			double similarity = retrieval.getEval();

			if (similarity >= 0.9) {
				MsPacManResult oldResult = (MsPacManResult) similarCase.getResult();
				if (oldResult.getScore() < newResult.getScore()) {
					toForget.add(similarCase);
					better = true;
				}
				similar = true;
			}
		}

		// If there are no similar cases or the new case is better than the similar
		// cases and is positive score, store the new case
		if ((!similar || better) && newResult.getScore() > 0)
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

	/**
	 * Computes the score change based on pill distance improvement.
	 */
	private int computePillsChange(int initialPillDistance, int currentPillDistance) {
		if (initialPillDistance == Integer.MAX_VALUE || currentPillDistance == Integer.MAX_VALUE)
            return 0;
		int pillWeight = 5;
		int distanceChange = initialPillDistance - currentPillDistance; // Positive if closer
		return pillWeight * distanceChange;
	}

	/**
	 * Computes the score change based on power pill distance improvement.
	 */
	private int computePPillsChange(int initialPPillDistance, int currentPPillDistance) {
		if (initialPPillDistance == Integer.MAX_VALUE || currentPPillDistance == Integer.MAX_VALUE)
            return 0;
		int powerPillWeight = 10;
		int distanceChange = initialPPillDistance - currentPPillDistance; // Positive if closer
		return powerPillWeight * distanceChange;
	}

	/**
	 * Computes the score change based on ghost distance improvement (moving away
	 * from ghosts).
	 */
	private int computeGhostChange(int initialGhostDistance, int currentGhostDistance) {
		if (initialGhostDistance == Integer.MAX_VALUE || currentGhostDistance == Integer.MAX_VALUE)
            return 0;
		int ghostWeight = 15;
		int distanceChange = currentGhostDistance - initialGhostDistance; // Positive if further away
		return ghostWeight * distanceChange;
	}

	/**
	 * Computes the score change based on edible ghost distance improvement.
	 */
	private int computeEdibleGhostChange(int initialEdibleGhostDistance, int currentEdibleGhostDistance) {
		if (initialEdibleGhostDistance == Integer.MAX_VALUE || currentEdibleGhostDistance == Integer.MAX_VALUE)
			return 0;		
		int edibleGhostWeight = 20;
		int distanceChange = initialEdibleGhostDistance - currentEdibleGhostDistance; // Positive if closer
		return edibleGhostWeight * distanceChange;
	}

	/**
	 * Computes the score change based on edible ghost count reduction.
	 */
	private int computeEdibleGhostCountChange(int initialEdibleGhosts, int currentEdibleGhosts) {
		int edibleGhostCountWeight = 20; // Negative weight for reducing edible ghosts
		int countChange = currentEdibleGhosts - initialEdibleGhosts;
		return edibleGhostCountWeight * (countChange > 0 ? countChange : 0);
	}

	/**
	 * Computes the score change based on jail ghost count increase.
	 */
	private int computeJailGhostCountChange(int initialJailGhosts, int currentJailGhosts) {
		int jailGhostWeight = 10;
		int countChange = currentJailGhosts - initialJailGhosts;
		return jailGhostWeight * (countChange > 0 ? countChange : 0);
	}
}
