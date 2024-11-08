package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsFuzzyMemory;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.MaxActionSelector;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import es.ucm.fdi.ici.fuzzy.observers.ConsoleFuzzyEngineObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private static final String RULES_PATH = "src" + File.separator + "es" + File.separator + "ucm" + File.separator
			+ "fdi" + File.separator + "ici" + File.separator + "c2425" + File.separator + "practica4" + File.separator
			+ "grupo02" + File.separator + "ghosts" + File.separator;
	private static final String RULES_FILE = "ghosts.fcl";

	private EnumMap<GHOST, FuzzyEngine> ghostFuzzyEngines;
	private GhostsFuzzyMemory fuzzyMemory;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		String rulesFile = String.format("%s%s", RULES_PATH, RULES_FILE);

		this.fuzzyMemory = new GhostsFuzzyMemory();

		this.ghostFuzzyEngines = new EnumMap<GHOST, FuzzyEngine>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			// Fill Actions
			Action[] actions = { new ChaseAction(ghost), new RunAwayAction(ghost) };
			ActionSelector actionSelector = new MaxActionSelector(actions);

			// Create Rule Engine

			FuzzyEngine engine = new FuzzyEngine(ghost.name(), rulesFile, "FuzzyGhosts", actionSelector);
			this.ghostFuzzyEngines.put(ghost, engine);

			// add observer to every Ghost
			// ConsoleFuzzyEngineObserver observer = new
			// ConsoleFuzzyEngineObserver("MsPacMan", "MsPacManRules");
			// engine.addObserver(observer);
		}

		// add observer only to BLINKY
		ConsoleFuzzyEngineObserver observer = new ConsoleFuzzyEngineObserver(GHOST.BLINKY.name(), "FuzzyGhosts");
		this.ghostFuzzyEngines.get(GHOST.BLINKY).addObserver(observer);
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		GhostsInput input = new GhostsInput(game);
		this.fuzzyMemory.getInput(input);

		for (GHOST ghost : GHOST.values()) {
			FuzzyEngine engine = this.ghostFuzzyEngines.get(ghost);

			// get fuzzy values
			HashMap<String, Double> fvars = input.getFuzzyValues(ghost);
			fvars.putAll(this.fuzzyMemory.getFuzzyValues(ghost));

			// run the engine
			MOVE move = engine.run(fvars, game);
			result.put(ghost, move);
		}

		return result;
	}

}
