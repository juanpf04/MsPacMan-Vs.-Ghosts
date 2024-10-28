package es.ucm.fdi.ici.c2425.practica3.grupo02.pacman;

import java.io.File;
import java.util.HashMap;

import es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions.ChaseAction;
import es.ucm.fdi.ici.c2425.practica3.grupo02.pacman.actions.RunAwayAction;
import es.ucm.fdi.ici.rules.RuleEngine;
import es.ucm.fdi.ici.rules.RulesAction;
import es.ucm.fdi.ici.rules.RulesInput;
import es.ucm.fdi.ici.rules.observers.ConsoleRuleEngineObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController{
    private static final String RULES_PATH = "es" + File.separator + "ucm" + File.separator + "fdi" + File.separator + "ici" + File.separator + "c2425" + File.separator + "practica3" + File.separator + "grupo02" + File.separator + "pacman" + File.separator;
	HashMap<String,RulesAction> map;
	
    private RuleEngine pacmanRuleEngines;

    public MsPacMan(){
        setName("JPacman");
		setTeam("grupo02");
		
		map = new HashMap<String,RulesAction>();
		//Fill Actions
		RulesAction pacmanChaseBlinky = new ChaseAction(GHOST.BLINKY);
		RulesAction pacmanChaseInky= new ChaseAction(GHOST.INKY);
		RulesAction pacmanChasePinky = new ChaseAction(GHOST.PINKY);
		RulesAction pacmanChaseSue = new ChaseAction(GHOST.SUE);
        
		RulesAction pacmanRunAwayBlinky = new RunAwayAction(GHOST.BLINKY);
		RulesAction pacmanRunAwayInky = new RunAwayAction(GHOST.INKY);
		RulesAction pacmanRunAwayPinky = new RunAwayAction(GHOST.PINKY);
		RulesAction pacmanRunAwaySue = new RunAwayAction(GHOST.SUE);
		
		map.put("pacmanChaseBlinky", pacmanChaseBlinky);
		map.put("pacmanChaseInky", pacmanChaseInky);
		map.put("pacmanChasePinky", pacmanChasePinky);
		map.put("pacmanChaseSue", pacmanChaseSue);	
        map.put("pacmanRunAwayBlinky", pacmanRunAwayBlinky);
		map.put("pacmanRunAwayInky", pacmanRunAwayInky);
		map.put("pacmanRunAwayPinky", pacmanRunAwayPinky);
		map.put("pacmanRunAwaySue", pacmanRunAwaySue);	
		
		
        String rulesFile = String.format("%s%srules.clp", RULES_PATH, "PACMAN");
        RuleEngine engine  = new RuleEngine("PACMAN", rulesFile, map);
		pacmanRuleEngines = engine;
		
		//add observer only to BLINKY
		ConsoleRuleEngineObserver observer = new ConsoleRuleEngineObserver("PACMAN", true);
		pacmanRuleEngines.addObserver(observer);
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Process input
		RulesInput input = new MsPacManInput(game);
		//load facts
		//reset the rule engines
        pacmanRuleEngines.reset();
        pacmanRuleEngines.assertFacts(input.getFacts());
	
		
		return pacmanRuleEngines.run(game);
    }

}
