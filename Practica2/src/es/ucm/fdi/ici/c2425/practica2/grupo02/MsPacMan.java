package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.RandomAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_chase.ShortEdibleTime;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee.EdibleGhostCloserThanGhost;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee.EvaluatePills;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee.PowerPillCloserThanGhost;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills.IndifferentNumbersPills;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills.MaximumPillsPath;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class MsPacMan extends PacmanController {

	FSM fsm;

	public MsPacMan() {
		setName("JPacman");

		fsm = new FSM("MsPacMan");

		GraphFSMObserver observer = new GraphFSMObserver(fsm.toString());
		fsm.addObserver(observer);

		//Transiciones de la maquina de estados

		Transition dangerFromPills = new PacmanInDangerTransition("from Pills");
		Transition dangerFromChase = new PacmanInDangerTransition("from Chase");
		Transition safety = new SafetyPacmanTransition();
		Transition die = new PacManDiedTransition();
		Transition noEdibleTime = new WithoutEdibleTimeForChaseTransition();
		Transition eatPowerPill = new PacmanEatPowerPillTransition();

		// --------------------------------------------

		FSM cfsm_pills = new FSM("Pills");
		GraphFSMObserver pills_observer = new GraphFSMObserver(cfsm_pills.toString());
		cfsm_pills.addObserver(pills_observer);

		SimpleState morePills = new SimpleState("more pills", new RandomAction());
		SimpleState safePills = new SimpleState("safe pills", new RandomAction());
		SimpleState nearestPill = new SimpleState("early pills", new RandomAction());
		
		Transition severalPaths1 = new SafetyPacmanTransition();
		Transition severalPaths2 = new SafetyPacmanTransition();
		Transition sameNumberOfPills = new IndifferentNumbersPills();
		Transition pathWithMaximumNumberPills = new MaximumPillsPath();
		
		cfsm_pills.add(safePills, pathWithMaximumNumberPills, morePills);
		cfsm_pills.add(morePills, severalPaths1, safePills);
		cfsm_pills.add(morePills, sameNumberOfPills, nearestPill);
		cfsm_pills.add(nearestPill, severalPaths2, safePills);

		cfsm_pills.ready(morePills);

		CompoundState pills = new CompoundState("pills", cfsm_pills);

		// --------------------------------------------

		FSM cfsm_flee = new FSM("Flee");
		GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
		cfsm_flee.addObserver(flee_observer);

		SimpleState safetyPath = new SimpleState("Safety path", new RandomAction());
		SimpleState edibleGhost = new SimpleState("More edible ghosts", new RandomAction());
		SimpleState morePillsFlee = new SimpleState("More pills", new RandomAction());
		SimpleState powerPill = new SimpleState("PowerPill", new RandomAction());
		
		Transition edibleGhostNearestThanGhost = new EdibleGhostCloserThanGhost();
		Transition powerPillCloserThanGhostFromSafetyPath = new PowerPillCloserThanGhost("From safety Path");
		Transition powerPillCloserThanGhostFromPills = new PowerPillCloserThanGhost("From pills");
		Transition evaluatePills = new EvaluatePills();

		cfsm_flee.add(safetyPath, edibleGhostNearestThanGhost, edibleGhost);
		cfsm_flee.add(safetyPath, powerPillCloserThanGhostFromSafetyPath, powerPill);
		cfsm_flee.add(edibleGhost, evaluatePills, morePillsFlee);
		cfsm_flee.add(morePillsFlee, powerPillCloserThanGhostFromPills, powerPill);

		cfsm_flee.ready(safetyPath);
		CompoundState flee = new CompoundState("flee", cfsm_flee);

		// --------------------------------------------

		FSM cfsm_chase = new FSM("Chase");
		GraphFSMObserver chase_observer = new GraphFSMObserver(cfsm_chase.toString());
		cfsm_chase.addObserver(chase_observer);

		SimpleState moreGhosts = new SimpleState("more ghosts", new RandomAction());
		SimpleState nearestGhost = new SimpleState("nearest ghost", new RandomAction());
		SimpleState safetyGhost = new SimpleState("safety ghost", new RandomAction());
		
		Transition withoutTimeFromMoreGhost = new ShortEdibleTime("From more ghosts");
		Transition withoutTimeFromSafetyGhost = new ShortEdibleTime("From safety ghosts");
		Transition edibleGhostClose = new EdibleGhostCloserThanGhost();


		cfsm_chase.add(moreGhosts, withoutTimeFromMoreGhost, nearestGhost);
		cfsm_chase.add(moreGhosts, edibleGhostClose, safetyGhost);
		cfsm_chase.add(safetyGhost, withoutTimeFromSafetyGhost, nearestGhost);

		cfsm_chase.ready(moreGhosts);
		CompoundState chase = new CompoundState("chase", cfsm_chase);

		// --------------------------------------------

		fsm.add(pills, dangerFromPills, flee);
		fsm.add(chase, dangerFromChase, flee);
		fsm.add(chase, noEdibleTime, pills);
		fsm.add(chase, die, pills);
		fsm.add(flee, die, pills);
		fsm.add(flee, safety, pills);
		fsm.add(flee, eatPowerPill, chase);

		fsm.ready(pills);

		JFrame frame = new JFrame();
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
		main.add(pills_observer.getAsPanel(true, null), BorderLayout.WEST);
		main.add(flee_observer.getAsPanel(true, null), BorderLayout.SOUTH);
		main.add(chase_observer.getAsPanel(true, null), BorderLayout.EAST);
		frame.getContentPane().add(main);
		frame.pack();
		frame.setVisible(true);

	}

	public void preCompute(String opponent) {
		fsm.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	@Override
	public MOVE getMove(Game game, long timeDue) {
		Input in = new MsPacManInput(game);
		return fsm.run(in);
	}

}