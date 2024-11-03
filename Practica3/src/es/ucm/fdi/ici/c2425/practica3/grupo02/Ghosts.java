package es.ucm.fdi.ici.c2425.practica3.grupo02;

import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions.*;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private static final String RULES_FILE = "ghostsrules.clp";

	private EnumMap<GHOST, RuleEngine> ghostRuleEngines;

	private GhostsInfo info;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		String rulesFile = String.format("%s%s", Utils.RULES_PATH, RULES_FILE);

		this.info = new GhostsInfo();

		this.ghostRuleEngines = new EnumMap<GHOST, RuleEngine>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			HashMap<String, RulesAction> actions = new HashMap<String, RulesAction>();

			// Fill Actions
			actions.put("chase", new ChaseAction(ghost));
			actions.put("runAway", new RunAwayAction(ghost));
			actions.put("goToNearestPPillToPacman", new GoToPowePillAction(ghost, this.info));
			actions.put("protectEdibleGhost", new GoToGhostAction(ghost, this.info));
			actions.put("goToSafeGhost", new GoToGhostAction(ghost, this.info));
			actions.put("disperse", new DisperseAction(ghost));
			actions.put("goToLastPills", new GoToLastPillsAction(ghost));
			actions.put("blockExits", new CoverExitAction(ghost, this.info));

			// Create Rule Engine
			RuleEngine engine = new RuleEngine(ghost.name(), rulesFile, actions);
			this.ghostRuleEngines.put(ghost, engine);

			// add observer to every Ghost
//			ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(ghost.name(), true);
//			engine.addObserver(observer);
		}

		// add observer only to BLINKY
//		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(GHOST.BLINKY.name(), true);
//		this.ghostRuleEngines.get(GHOST.BLINKY).addObserver(observer);

	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		// Process input
		GhostsInput input = new GhostsInput(game, this.info);

		for (GHOST ghost : GHOST.values()) {
			RuleEngine engine = this.ghostRuleEngines.get(ghost);

			// reset the rule engine
			engine.reset();
			// load facts
			engine.assertFacts(input.getFacts(ghost));

			// run the engine
			MOVE move = engine.run(game);
			result.put(ghost, move);
		}

		return result;
	}

}
