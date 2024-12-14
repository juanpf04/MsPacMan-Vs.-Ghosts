package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.gaia.jcolibri.util.FileIO;
import es.ucm.fdi.ici.c2425.practica5.grupo02.VariablePQ;
import es.ucm.fdi.ici.c2425.practica5.grupo02.Weithgs;
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.Average;
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.CachedLinearCaseBase;
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.CustomPlainTextConnector;
import es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman.similitud.Enumerado;
import pacman.game.Constants.MOVE;

public class MsPacManCBRengine implements StandardCBRApplication {

	private final static String TEAM = "grupo02";
	private final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2425/practica5/" + TEAM + "/mspacman/plaintextconfig.xml";
	private final static String CASE_BASE_PATH = "cbrdata" + File.separator + TEAM + File.separator + "mspacman" + File.separator;

	private String opponent;
	private MOVE action;
	private MsPacManStorageManager storageManager;

	private CustomPlainTextConnector genericConnector;
	private CachedLinearCaseBase genericCaseBase;

	private CustomPlainTextConnector connector;
	private CachedLinearCaseBase caseBase;
	private NNConfig simConfig;

	public MsPacManCBRengine(MsPacManStorageManager storageManager) {
		this.storageManager = storageManager;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	@Override
	public void configure() throws ExecutionException {
		this.genericConnector = new CustomPlainTextConnector();
		this.genericCaseBase = new CachedLinearCaseBase();

		this.connector = new CustomPlainTextConnector();
		this.caseBase = new CachedLinearCaseBase();

		this.genericConnector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		this.genericConnector.setCaseBaseFile(CASE_BASE_PATH, "GenericGhosts.csv");

		this.connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		this.connector.setCaseBaseFile(CASE_BASE_PATH, this.opponent + ".csv");

		this.storageManager.setCaseBase(this.caseBase);

		// Similarity configuration

		this.simConfig = new NNConfig();
		this.simConfig.setDescriptionSimFunction(new Average());
		Attribute attribute; // TODO revisar similitud y pesos

		attribute = new Attribute("score", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(15000));
		this.simConfig.setWeight(attribute, Weithgs.SCORE);

		attribute = new Attribute("edibleTime", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(200));
		this.simConfig.setWeight(attribute, Weithgs.TIME_EDIBLE);

		attribute = new Attribute("ppillDistance", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(240));
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_POWERPILL);

		attribute = new Attribute("pillDistance", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(240));
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_PILL);

		attribute = new Attribute("ghostDistance", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(240));
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_NOT_EDIBLE);

		attribute = new Attribute("edibleGhostDistance", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(240));
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_EDIBLE);

		attribute = new Attribute("edibleGhosts", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(4));
		this.simConfig.setWeight(attribute, Weithgs.NUMBER_EDIBLES);

		attribute = new Attribute("jailGhosts", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Interval(4));
		this.simConfig.setWeight(attribute, Weithgs.NUMBER_JAIL);

		attribute = new Attribute("relativePosGhost", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Enumerado());
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_EDIBLE);

		attribute = new Attribute("relativePosEdibleGhost", MsPacManDescription.class);
		this.simConfig.addMapping(attribute, new Enumerado());
		this.simConfig.setWeight(attribute, Weithgs.DISTANCE_EDIBLE);

		new Equal(); // FIXME prueba
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		this.caseBase.init(this.connector);
		this.genericCaseBase.init(this.genericConnector);
		return this.caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		this.action = MOVE.NEUTRAL;

		// Compute specific retrieve
		if (!this.caseBase.getCases().isEmpty()) {

			// Compute retrieve
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(this.caseBase.getCases(), query,
					this.simConfig);

			// Compute reuse
			this.action = reuse(eval);
		}

		// Compute general retrieve if no solution
		if (!this.genericCaseBase.getCases().isEmpty() && this.action == MOVE.NEUTRAL) {

			// Compute retrieve
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(this.genericCaseBase.getCases(),
					query, this.simConfig);

			// Compute reuse
			this.action = reuse(eval);
		}

		// Compute random action if no solution
		if (this.action == MOVE.NEUTRAL) {
			int index = (int) Math.floor(Math.random() * 4);
			this.action = MOVE.values()[index];
		}

		// Compute revise & retain
		CBRCase newCase = createNewCase(query);
		this.storageManager.reviseAndRetain(newCase);

	}

	private MOVE reuse(Collection<RetrievalResult> eval) {
		VariablePQ<MOVE> majority_voting = new VariablePQ<>();
		Iterator<RetrievalResult> topCases = SelectCases.selectTopKRR(eval, 13).iterator();

		while (topCases.hasNext()) {
			RetrievalResult retrieval = topCases.next();
			CBRCase similarCase = retrieval.get_case();
			double similarity = retrieval.getEval();

			// MsPacManResult result = (MsPacManResult) similarCase.getResult();
			MsPacManSolution solution = (MsPacManSolution) similarCase.getSolution();

			// If enough similarity
			if (similarity >= 0.7)
				majority_voting.increase(solution.getAction(), 1);
		}

		// Takes the action of the KNNs with majority voting
		return majority_voting.isEmpty() ? MOVE.NEUTRAL : majority_voting.top();
	}

	/**
	 * Creates a new case using the query as description, storing the action into
	 * the solution and setting the proper id number
	 */
	private CBRCase createNewCase(CBRQuery query) {
		CBRCase newCase = new CBRCase();
		MsPacManDescription newDescription = (MsPacManDescription) query.getDescription();
		MsPacManResult newResult = new MsPacManResult();
		MsPacManSolution newSolution = new MsPacManSolution();
		int newId = this.caseBase.getNextId();
		newId += this.storageManager.getPendingCases();
		newDescription.setId(newId);
		newResult.setId(newId);
		newSolution.setId(newId);
		newSolution.setAction(this.action);
		newCase.setDescription(newDescription);
		newCase.setResult(newResult);
		newCase.setSolution(newSolution);
		return newCase;
	}

	public MOVE getSolution() {
		return this.action;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		this.genericCaseBase.close();
		this.caseBase.close();
	}

}
