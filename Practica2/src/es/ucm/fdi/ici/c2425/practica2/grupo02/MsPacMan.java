package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.SafePaths;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions.MoreGhostsChaseAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions.NearestGhostChaseAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions.SafetyGhostChaseAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.MoreEdibleGhostFleeAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.MorePillsFleeAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.PowerPillFleeAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.SafetyPathFleeAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions.ChooseAnyPillsAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions.MorePillsPillsAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions.SafeLongestPathPillsAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_chase.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills.*;
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

		SafePaths safePathsPills = new SafePaths();
		
		SimpleState safePills = new SimpleState("safe longest path", new SafeLongestPathPillsAction(safePathsPills));
		SimpleState morePills = new SimpleState("more pills", new MorePillsPillsAction(safePathsPills));
		SimpleState chooseAny = new SimpleState("choose any", new ChooseAnyPillsAction());
		
		Transition severalPaths1 = new SafetyPacmanTransition();
		Transition severalPaths2 = new SafetyPacmanTransition();
		Transition sameNumberOfPills = new IndifferentNumbersPills();
		Transition pathWithMaximumNumberPills = new MaximumPillsPath();
		
		cfsm_pills.add(safePills, pathWithMaximumNumberPills, morePills);
		cfsm_pills.add(morePills, severalPaths1, safePills);
		cfsm_pills.add(morePills, sameNumberOfPills, chooseAny);
		cfsm_pills.add(chooseAny, severalPaths2, safePills);

		cfsm_pills.ready(morePills);

		CompoundState pills = new CompoundState("pills", cfsm_pills);

		// --------------------------------------------

		FSM cfsm_flee = new FSM("Flee");
		GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
		cfsm_flee.addObserver(flee_observer);

		SafePaths safePathsFlee = new SafePaths();

		SimpleState safetyPath = new SimpleState("Safety path", new SafetyPathFleeAction(safePathsFlee));
		SimpleState edibleGhost = new SimpleState("More edible ghosts", new MoreEdibleGhostFleeAction(safePathsFlee));
		SimpleState morePillsFlee = new SimpleState("More pills", new MorePillsFleeAction(safePathsFlee));
		SimpleState powerPill = new SimpleState("PowerPill", new PowerPillFleeAction());
		
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

		SimpleState moreGhosts = new SimpleState("more ghosts", new MoreGhostsChaseAction());
		SimpleState nearestGhost = new SimpleState("nearest ghost", new NearestGhostChaseAction());
		SimpleState safetyGhost = new SimpleState("safety ghost", new SafetyGhostChaseAction());
		
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