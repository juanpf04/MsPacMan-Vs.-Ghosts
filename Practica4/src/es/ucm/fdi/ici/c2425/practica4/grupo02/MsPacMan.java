package es.ucm.fdi.ici.c2425.practica4.grupo02;

import java.io.File;
import java.util.HashMap;

import es.ucm.fdi.ici.Action;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.MaxActionSelector;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.MsPacManFuzzyMemory;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions.GoToPPillAction;
import es.ucm.fdi.ici.c2425.practica4.grupo02.mspacman.actions.RunAwayAction;
import es.ucm.fdi.ici.fuzzy.ActionSelector;
import es.ucm.fdi.ici.fuzzy.FuzzyEngine;
import es.ucm.fdi.ici.fuzzy.observers.ConsoleFuzzyEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private static final String RULES_PATH = "bin"+File.separator+"es"+File.separator+"ucm"+File.separator+"fdi"+File.separator+"ici"+File.separator+"c2425"+File.separator+"practica4"+File.separator+"grupo02"+File.separator+"mspacman"+File.separator;
	FuzzyEngine fuzzyEngine;
	MsPacManFuzzyMemory fuzzyMemory;
	
	
	public MsPacMan()
	{
		setName("MsPacMan XX");

		fuzzyMemory = new MsPacManFuzzyMemory();
		
		Action[] actions = {new GoToPPillAction(), new RunAwayAction()};
		
		ActionSelector actionSelector = new MaxActionSelector(actions);
		 
		fuzzyEngine = new FuzzyEngine("MsPacMan",
				RULES_PATH+"mspacman.fcl",
				"FuzzyMsPacMan",
				actionSelector);

		ConsoleFuzzyEngineObserver observer = new ConsoleFuzzyEngineObserver("MsPacMan","MsPacManRules");
		fuzzyEngine.addObserver(observer);
		
		
	}
	
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		MsPacManInput input = new MsPacManInput(game);
		input.parseInput();
		fuzzyMemory.getInput(input);
		
		HashMap<String, Double> fvars = input.getFuzzyValues();
		fvars.putAll(fuzzyMemory.getFuzzyValues());
		
		return fuzzyEngine.run(fvars,game);
	}

}
