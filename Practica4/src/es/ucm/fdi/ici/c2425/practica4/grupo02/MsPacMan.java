package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.io.File;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.MsPacManFuzzyMemory;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions.*;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import es.ucm.fdi.ici.fuzzy.observers.ConsoleFuzzyEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	static final String RULES_PATH = "src"+File.separator+"es"+File.separator+"ucm"+File.separator+"fdi"+File.separator+"ici"+File.separator+"c2425"+File.separator+"practica4"+File.separator+"grupo02"+File.separator+"mspacman"+File.separator;
	static final String RULES_FILE = "mspacman.fcl";

	private FuzzyEngine fuzzyEngine;
	private MsPacManFuzzyMemory fuzzyMemory;

	public MsPacMan() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		String rulesFile = String.format("%s%s", RULES_PATH, RULES_FILE);

		this.fuzzyMemory = new MsPacManFuzzyMemory();

		// Fill Actions
		MaxActionSelector actionSelector = new MaxActionSelector(); 
		actionSelector.addAction(new GoToPPillAction(), 50);
		actionSelector.addAction(new RunAwayFromBLINKY(), 50);
		actionSelector.addAction(new RunAwayFromINKY(), 50);
		actionSelector.addAction(new RunAwayFromPINKY(), 50);
		actionSelector.addAction(new RunAwayFromSUE(), 50);
		actionSelector.addAction(new ChaseBLINKY(), 50);
		actionSelector.addAction(new ChaseINKY(), 50);
		actionSelector.addAction(new ChasePINKY(), 50);
		actionSelector.addAction(new ChaseSUE(), 50);
		actionSelector.addAction(new GoToPillAction(), 50);

		// Create Rule Engine
		this.fuzzyEngine = new FuzzyEngine("MsPacMan", rulesFile, "FuzzyMsPacMan", actionSelector);

		// Add observer to MsPacMan
		ConsoleFuzzyEngineObserver observer = new ConsoleFuzzyEngineObserver("MsPacMan", "MsPacManRules");
		this.fuzzyEngine.addObserver(observer);
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		MsPacManInput input = new MsPacManInput(game);
		//input.parseInput();
		this.fuzzyMemory.getInput(input);

		HashMap<String, Double> fvars = input.getFuzzyValues();
		fvars.putAll(this.fuzzyMemory.getFuzzyValues());

		return this.fuzzyEngine.run(fvars, game);
	}

}
