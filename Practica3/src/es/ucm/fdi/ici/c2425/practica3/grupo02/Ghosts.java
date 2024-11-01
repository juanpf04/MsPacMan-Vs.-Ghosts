package es.ucm.fdi.ici.c2425.practica3.grupo02;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica3.grupo02.ghosts.actions.*;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import es.ucm.fdi.ici.rules.RulesInput;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {
	private static final String RULES_PATH = "es" + File.separator + "ucm" + File.separator + "fdi" + File.separator
			+ "ici" + File.separator + "c2425" + File.separator + "practica3" + File.separator + "grupo02"
			+ File.separator;
	HashMap<String, RulesAction> map;

	EnumMap<GHOST, RuleEngine> ghostRuleEngines;

	public Ghosts() {
		this.setName("Fantasmikos");
		this.setTeam("Team 02");

		this.map = new HashMap<String, RulesAction>();

		// Fill Actions
		for (GHOST ghost : GHOST.values()) {
			this.map.put(ghost.toString() + "chases", new ChaseAction(ghost));
			this.map.put(ghost.toString() + "runsAway", new RunAwayAction(ghost));
			this.map.put(ghost.toString() + "goesToNearestPPillToPacman", new GoToNearestPPillToPacmanAction(ghost));
			this.map.put(ghost.toString() + "protectsEdibleGhost", new ProtectEdibleGhostAction(ghost));
			this.map.put(ghost.toString() + "goesToSafeGhost", new GoToSafeGhostAction(ghost));
			this.map.put(ghost.toString() + "disperses", new DisperseAction(ghost));
			this.map.put(ghost.toString() + "goesToLastPills", new GoToLastPillsAction(ghost));
			this.map.put(ghost.toString() + "blocksExits", new BlockExitsAction(ghost));
		}

		this.ghostRuleEngines = new EnumMap<GHOST, RuleEngine>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			String rulesFile = String.format("%s%srules.clp", RULES_PATH, ghost.name().toLowerCase());
			RuleEngine engine = new RuleEngine(ghost.name(), rulesFile, this.map);
			this.ghostRuleEngines.put(ghost, engine);

			// add observer to every Ghost
			// ConsoleRuleEngineObserver observer = new
			// ConsoleRuleEngineObserver(ghost.name(), true);
			// engine.addObserver(observer);
		}

		// add observer only to BLINKY
		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver(GHOST.BLINKY.name(), true);
		this.ghostRuleEngines.get(GHOST.BLINKY).addObserver(observer);

	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {

		// Process input
		RulesInput input = new GhostsInput(game);
		// load facts
		// reset the rule engines
		for (RuleEngine engine : this.ghostRuleEngines.values()) {
			engine.reset();
			engine.assertFacts(input.getFacts());
		}

		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			RuleEngine engine = this.ghostRuleEngines.get(ghost);
			MOVE move = engine.run(game);
			result.put(ghost, move);
		}

		return result;
	}

}
