package es.ucm.fdi.ici.c2425.practica5.grupo02.mspacman;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import constantes.Weithgs;
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
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.Average;
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.CachedLinearCaseBase;
import es.ucm.fdi.ici.c2425.practica5.grupo02.CBRengine.CustomPlainTextConnector;
import es.ucm.fdi.ici.c2425.practica5.grupo02.similitud.Enumerado;
import pacman.game.Constants.MOVE;

public class MsPacManCBRengine implements StandardCBRApplication {

	private String opponent;
	private MOVE action;
	private MsPacManStorageManager storageManager;

	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;
	
	
	final static String TEAM = "grupo02";  //Cuidado!! poner el grupo aquí
	
	
	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2425/practica5/"+TEAM+"/mspacman/plaintextconfig.xml";
	final static String CASE_BASE_PATH = "cbrdata"+File.separator+TEAM+File.separator+"mspacman"+File.separator;

	
	public MsPacManCBRengine(MsPacManStorageManager storageManager)
	{
		this.storageManager = storageManager;
	}
	
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
	
	@Override
	public void configure() throws ExecutionException {
		connector = new CustomPlainTextConnector();
		caseBase = new CachedLinearCaseBase();
		
		connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		
		//Do not use default case base path in the xml file. Instead use custom file path for each opponent.
		//Note that you can create any subfolder of files to store the case base inside your "cbrdata/grupoXX" folder.
		connector.setCaseBaseFile(CASE_BASE_PATH, opponent+".csv");
		
		this.storageManager.setCaseBase(caseBase);
		
		simConfig = new NNConfig();
		Attribute[] atributos = {new Attribute("score",MsPacManDescription.class), new Attribute("timeEdibleGhost",MsPacManDescription.class),
				new Attribute("nearestPPillDistance",MsPacManDescription.class),new Attribute("nearestPillDistance",MsPacManDescription.class),
				new Attribute("nearestGhostDistance",MsPacManDescription.class), new Attribute("nearestEdibleGhostDistance",MsPacManDescription.class),
				new Attribute("edibleGhosts",MsPacManDescription.class), new Attribute("numberJailGhosts",MsPacManDescription.class), 
				new Attribute("relativePosGhost",MsPacManDescription.class), new Attribute("relativePosEdibleGhost",MsPacManDescription.class)};
		//setWeight
		simConfig.setDescriptionSimFunction(new Average());
		simConfig.addMapping(atributos[0], new Interval(15000));
		simConfig.setWeight(atributos[0], Weithgs.SCORE);
		
		simConfig.addMapping(atributos[1], new Interval(200));
		simConfig.setWeight(atributos[1], Weithgs.TIME_EDIBLE);
		
		simConfig.addMapping(atributos[2], new Interval(300));
		simConfig.setWeight(atributos[2], Weithgs.DISTANCE_POWERPILL);
		
		
		simConfig.addMapping(atributos[3], new Interval(300));
		simConfig.setWeight(atributos[3], Weithgs.DISTANCE_PILL);
		
		
		simConfig.addMapping(atributos[4], new Interval(300));
		simConfig.setWeight(atributos[4], Weithgs.DISTANCE_NOT_EDIBLE);
		
		
		simConfig.addMapping(atributos[5], new Interval(300));
		simConfig.setWeight(atributos[5], Weithgs.DISTANCE_EDIBLE);
		
		
		simConfig.addMapping(atributos[6], new Interval(4));
		simConfig.setWeight(atributos[6], Weithgs.NUMBER_EDIBLES);
		
		
		simConfig.addMapping(atributos[7], new Interval(4));
		simConfig.setWeight(atributos[7], Weithgs.NUMBER_JAIL);
		
		simConfig.addMapping(atributos[8], new Enumerado());
		simConfig.setWeight(atributos[8], Weithgs.DISTANCE_EDIBLE);
		
		
		simConfig.addMapping(atributos[9], new Enumerado());
		simConfig.setWeight(atributos[9], Weithgs.DISTANCE_EDIBLE);
		
		
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		if(caseBase.getCases().isEmpty()) {
			this.action = MOVE.NEUTRAL;
		}
		else {
			//Compute retrieve
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, simConfig);
			
			//Compute reuse
			this.action = reuse(eval);
		}
		
		//Compute revise & retain
		CBRCase newCase = createNewCase(query);
		this.storageManager.reviseAndRetain(newCase);
		
	}

	private MOVE reuse(Collection<RetrievalResult> eval)
	{
		Map<MOVE, Integer> votacion_mayoritaria = new HashMap<MOVE, Integer>();
		Iterator<RetrievalResult> iterator = SelectCases.selectTopKRR(eval, 13).iterator();
		
		while (iterator.hasNext()) {
			RetrievalResult first = iterator.next();
			CBRCase mostSimilarCase = first.get_case();
			double similarity = first.getEval();
			
			
			if(Math.random()<.2) {
				ArrayList<CBRCase> toforget = new ArrayList<CBRCase>();
				toforget.add(mostSimilarCase);
				this.caseBase.forgetCases(toforget);
				System.out.println(mostSimilarCase.getID());
			}
			
			MsPacManResult result = (MsPacManResult) mostSimilarCase.getResult();
			MsPacManSolution solution = (MsPacManSolution) mostSimilarCase.getSolution();
			
			//Now compute a solution for the query
			
			//Here, it simply takes the action of the 1NN
			MOVE action = solution.getAction();
			
			//But if not enough similarity or bad case, choose another move randomly
			if((similarity<0.7)||(result.getScore()<100)) {
				int index = (int)Math.floor(Math.random()*4);
				if(MOVE.values()[index]==action) 
					index= (index+1)%4;
				action = MOVE.values()[index];
			}
			
			votacion_mayoritaria.put(action, votacion_mayoritaria.getOrDefault(action, 1) + 1);
		
		}
		
		Integer max = -1;
		for (Map.Entry<MOVE, Integer> entrada : votacion_mayoritaria.entrySet()) {
			Integer value = entrada.getValue();
			if(value > max) {
				max = value;
				action = entrada.getKey();
			}
		}
		
		return action;
	}
	
	
	

	/**
	 * Creates a new case using the query as description, 
	 * storing the action into the solution and 
	 * setting the proper id number
	 */
	private CBRCase createNewCase(CBRQuery query) {
		CBRCase newCase = new CBRCase();
		MsPacManDescription newDescription = (MsPacManDescription) query.getDescription();
		MsPacManResult newResult = new MsPacManResult();
		MsPacManSolution newSolution = new MsPacManSolution();
		int newId = this.caseBase.getNextId();
		newId+= storageManager.getPendingCases();
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
