package es.ucm.fdi.ici.c2425.practica3.grupo02;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
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

	private static final String RULES_PATH = "es" + File.separator + "ucm" + File.separator + "fdi" + File.separator
			+ "ici" + File.separator + "c2425" + File.separator + "practica3" + File.separator + "grupo02"
			+ File.separator;
	private static final String RULES_FILE = "ghostsrules.clp";

	private EnumMap<GHOST, RuleEngine> ghostRuleEngines;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		String rulesFile = String.format("%s%s", RULES_PATH, RULES_FILE);

		this.ghostRuleEngines = new EnumMap<GHOST, RuleEngine>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			HashMap<String, RulesAction> map = new HashMap<String, RulesAction>();

			// Fill Actions
			map.put("chase", new ChaseAction(ghost));
			map.put("runAway", new RunAwayAction(ghost));
			map.put("goToNearestPPillToPacman", new GoToNearestPPillToPacmanAction(ghost));
			map.put("protectEdibleGhost", new ProtectEdibleGhostAction(ghost));
			map.put("goToSafeGhost", new GoToSafeGhostAction(ghost));
			map.put("disperse", new DisperseAction(ghost));
			map.put("goToLastPills", new GoToLastPillsAction(ghost));
			map.put("blockExits", new BlockExitsAction(ghost));

			RuleEngine engine = new RuleEngine(ghost.name(), rulesFile, map);
			this.ghostRuleEngines.put(ghost, engine);

			// add observer to every Ghost
			ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(ghost.name(), true);
			engine.addObserver(observer);
		}

		// add observer only to BLINKY
//		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(GHOST.BLINKY.name(), true);
//		this.ghostRuleEngines.get(GHOST.BLINKY).addObserver(observer);

	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		// Process input
		GhostsInput input = new GhostsInput(game);

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
