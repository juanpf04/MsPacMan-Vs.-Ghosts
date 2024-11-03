package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman;

import java.io.File;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions.*;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import es.ucm.fdi.ici.rules.RulesInput;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController{
    private static final String RULES_PATH ="C:" +File.separator + "Users" + File.separator+"lucia"+ File.separator + "Downloads"+ File.separator + "ici-workspace" +File.separator +"Practica3"+File.separator + "src"+File.separator+ "es" + File.separator + "ucm" + File.separator + "fdi" + File.separator + "ici" + File.separator + "c2425" + File.separator + "practica3" + File.separator + "grupo02" + File.separator + "pacman" + File.separator;
	HashMap<String,RulesAction> map;
	
    private RuleEngine pacmanRuleEngines;

    public MsPacMan(){
        setName("JPacman");
		setTeam("grupo02");
		
		map = new HashMap<String,RulesAction>();
		//Fill Actions
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

		
		
        String rulesFile = String.format("%s%s.clp", RULES_PATH, "pacman");
        RuleEngine engine  = new RuleEngine("PACMAN", rulesFile, map);
		pacmanRuleEngines = engine;
		
		//add observer to pacman
		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver("PACMAN", true);
		pacmanRuleEngines.addObserver(observer);
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        
		// Procesar entrada
		RulesInput input = new MsPacManInput(game);
		// Resetear el motor de reglas
		pacmanRuleEngines.reset();

		pacmanRuleEngines.assertFacts(input.getFacts());

		// Ejecutar reglas
		
		MOVE move = pacmanRuleEngines.run(game);
		System.out.println("Move calculated: " + move);
	
		return move;
		
	}

}
