package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsFuzzyMemory;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.CoverExitAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.CoverLastPillsAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.DisperseAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.GoToGhostAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.GoToPowePillAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import es.ucm.fdi.ici.fuzzy.observers.ConsoleFuzzyEngineObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	static final String RULES_PATH = "src"+File.separator+"es"+File.separator+"ucm"+File.separator+"fdi"+File.separator+"ici"+File.separator+"c2425"+File.separator+"practica4"+File.separator+"grupo02"+File.separator+"ghosts"+File.separator;
	static final String RULES_FILE = "ghosts.fcl";

	private EnumMap<GHOST, FuzzyEngine> ghostFuzzyEngines;
	private GhostsFuzzyMemory fuzzyMemory;
	
	private GhostsInfo info;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		String rulesFile = String.format("%s%s", RULES_PATH, RULES_FILE);
		
		this.info = new GhostsInfo();

		this.fuzzyMemory = new GhostsFuzzyMemory();

		this.ghostFuzzyEngines = new EnumMap<GHOST, FuzzyEngine>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			
			// Fill Actions
			MaxActionSelector actionSelector = new MaxActionSelector();
			actionSelector.addAction(new ChaseAction(ghost), 50);
			actionSelector.addAction(new RunAwayAction(ghost), 50);
			actionSelector.addAction(new CoverExitAction(ghost, this.info), 50);
			actionSelector.addAction(new CoverLastPillsAction(ghost), 50);
			actionSelector.addAction(new DisperseAction(ghost), 50);
			actionSelector.addAction(new GoToGhostAction(ghost, false, info), 50);
			actionSelector.addAction(new GoToPowePillAction(ghost, info), 50);
			
			// Create Rule Engine
			FuzzyEngine engine = new FuzzyEngine(ghost.name(), rulesFile, "FuzzyGhosts", actionSelector);
			this.ghostFuzzyEngines.put(ghost, engine);

			// Add observer to every Ghost
//			ConsoleFuzzyEngineObserver observer = new
//			ConsoleFuzzyEngineObserver(ghost.name(), "GhostsRules");
//			engine.addObserver(observer);
		}

		// Add observer only to BLINKY
		ConsoleFuzzyEngineObserver observer = new 
		ConsoleFuzzyEngineObserver(GHOST.BLINKY.name(), "GhostsRules");
		this.ghostFuzzyEngines.get(GHOST.BLINKY).addObserver(observer);
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		// Parse input
		GhostsInput input = new GhostsInput(game, this.info);
		this.fuzzyMemory.getInput(input);

		for (GHOST ghost : GHOST.values()) {
			FuzzyEngine engine = this.ghostFuzzyEngines.get(ghost);

			// get fuzzy values
			HashMap<String, Double> fvars = input.getFuzzyValues(ghost);
			fvars.putAll(this.fuzzyMemory.getFuzzyValues(ghost));

			MOVE move = null;
			try {
				// run the engine
				move = engine.run(fvars, game);
			} catch (Exception e) {
				
			}
			result.put(ghost, move);				
		}

		return result;
	}

}
