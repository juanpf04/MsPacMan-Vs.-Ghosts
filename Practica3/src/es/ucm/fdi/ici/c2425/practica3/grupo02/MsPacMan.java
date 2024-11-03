package es.ucm.fdi.ici.c2425.practica3.grupo02;

import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica3.grupo02.mspacman.actions.*;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import es.ucm.fdi.ici.rules.RulesInput;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	private static final String RULES_FILE = "mspacmanrules.clp";

	private RuleEngine pacmanRuleEngines;

	public MsPacMan() {
		setName("JPacman");
		setTeam("Team 02");

		HashMap<String, RulesAction> map = new HashMap<String, RulesAction>();

		// Fill Actions
		RulesAction pacmanChaseFromNearestEdibleGhost = new ChaseAction();
		RulesAction pacmanRunAwayFromNearestGhost = new RunAwayAction();
		RulesAction pacmanChasePills = new ChasePills();
		RulesAction pacmanChasePowerPills = new ChasePowerPill();
		RulesAction pacmanRandomMove = new RandomMove();
		RulesAction pacmanAvoidCorner = new AvoidCorner();
		RulesAction pacmanMoveToCenter = new PacmanMoveToCenter();

		map.put("pacmanChase", pacmanChaseFromNearestEdibleGhost);
		map.put("pacmanRunAway", pacmanRunAwayFromNearestGhost);
		map.put("pacmanChasePills", pacmanChasePills);
		map.put("pacmanChasePowerPill", pacmanChasePowerPills);
		map.put("pacmanRandomMove", pacmanRandomMove);
		map.put("pacmanAvoidCorner", pacmanAvoidCorner);
		map.put("pacmanMoveToCenter", pacmanMoveToCenter);

		String rulesFile = String.format("%s%s", Utils.RULES_PATH, RULES_FILE);
		RuleEngine engine = new RuleEngine("MSPACMAN", rulesFile, map);
		this.pacmanRuleEngines = engine;
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {

		// Process input
		RulesInput input = new MsPacManInput(game);

		// Reset rule engine
		this.pacmanRuleEngines.reset();
		// Load facts
		this.pacmanRuleEngines.assertFacts(input.getFacts());

		MOVE move = this.pacmanRuleEngines.run(game);
		return move;
	}

}
