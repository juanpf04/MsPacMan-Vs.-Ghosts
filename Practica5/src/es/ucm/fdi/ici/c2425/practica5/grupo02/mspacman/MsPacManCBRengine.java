package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.gaia.jcolibri.util.FileIO;
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
		this.connector = new CustomPlainTextConnector();
		this.caseBase = new CachedLinearCaseBase();

		this.connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));

		// Do not use default case base path in the xml file. Instead use custom file
		// path for each opponent.
		// Note that you can create any subfolder of files to store the case base inside
		// your "cbrdata/grupoXX" folder.
		this.connector.setCaseBaseFile(CASE_BASE_PATH, this.opponent + ".csv");

		// TODO cargar base de datos especifica y generica

		this.storageManager.setCaseBase(this.caseBase);

		// Similarity configuration

		this.simConfig = new NNConfig();
		this.simConfig.setDescriptionSimFunction(new Average());
		Attribute attribute;

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
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		this.caseBase.init(this.connector);
		return this.caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		// TODO si esta vacia voy a la generica
		// si la similitud es baja, voy a la generica
		// si en la generica es baja o vacia, hago random.
		if (caseBase.getCases().isEmpty()) {
			this.action = MOVE.NEUTRAL;
		} else {
			// Compute retrieve
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query,
					this.simConfig);

			// Compute reuse
			this.action = reuse(eval);
		}

		// Compute revise & retain
		CBRCase newCase = createNewCase(query);
		this.storageManager.reviseAndRetain(newCase);

	}

	private MOVE reuse(Collection<RetrievalResult> eval) {
		Map<MOVE, Integer> votacion_mayoritaria = new HashMap<MOVE, Integer>();
		Iterator<RetrievalResult> iterator = SelectCases.selectTopKRR(eval, 13).iterator();

		while (iterator.hasNext()) {
			RetrievalResult first = iterator.next();
			CBRCase mostSimilarCase = first.get_case();
			double similarity = first.getEval();

			if (Math.random() < .2) {
				ArrayList<CBRCase> toforget = new ArrayList<CBRCase>();
				toforget.add(mostSimilarCase);
				this.caseBase.forgetCases(toforget);
				System.out.println(mostSimilarCase.getID());
			}

			MsPacManResult result = (MsPacManResult) mostSimilarCase.getResult();
			MsPacManSolution solution = (MsPacManSolution) mostSimilarCase.getSolution();

			// Now compute a solution for the query

			// Here, it simply takes the action of the 1NN
			MOVE action = solution.getAction();

			// But if not enough similarity or bad case, choose another move randomly
			if ((similarity < 0.7) || (result.getScore() < 100)) {
				int index = (int) Math.floor(Math.random() * 4);
				if (MOVE.values()[index] == action)
					index = (index + 1) % 4;
				action = MOVE.values()[index];
			}

			votacion_mayoritaria.put(action, votacion_mayoritaria.getOrDefault(action, 1) + 1);

		}

		Integer max = -1;
		for (Map.Entry<MOVE, Integer> entrada : votacion_mayoritaria.entrySet()) {
			Integer value = entrada.getValue();
			if (value > max) {
				max = value;
				this.action = entrada.getKey();
			}
		}

		return this.action;
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
		this.caseBase.close();
	}

}
