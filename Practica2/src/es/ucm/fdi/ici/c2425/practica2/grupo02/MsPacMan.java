package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions.*;
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
		Transition noEdibleTime = new WithoutEdibleTimeForChaseTransition();
		Transition eatPowerPill = new PacmanEatPowerPillTransition();

		// --------------------------------------------

		FSM cfsm_pills = new FSM("Pills");
        GraphFSMObserver pills_observer = new GraphFSMObserver(cfsm_pills.toString());
        cfsm_pills.addObserver(pills_observer);

        SimpleState safePills = new SimpleState("safe longest path", new SafeLongestPathPillsAction());
        SimpleState morePills = new SimpleState("more pills", new MorePillsPillsAction());
        SimpleState chooseAny = new SimpleState("choose any", new ChooseAnyPillsAction());

        Transition severalPaths1 = new SafetyPacmanTransition();
        Transition sameNumberOfPills = new IndifferentNumbersPills();
        //Transition pathWithMaximumNumberPills = new MaximumPillsPath();
        Transition pathSelected = new PathSelected();
        Transition pacmanNotRequieresAction = new PacmanNotRequieresAction();

        cfsm_pills.add(safePills, pacmanNotRequieresAction, safePills);
        cfsm_pills.add(safePills, severalPaths1, morePills);
        cfsm_pills.add(morePills, sameNumberOfPills, chooseAny);
		//cfsm_pills.add(morePills, pathWithMaximumNumberPills, safePills);
		cfsm_pills.add(chooseAny, pathSelected, safePills);

		cfsm_pills.ready(safePills);

		CompoundState pills = new CompoundState("pills", cfsm_pills);

		// --------------------------------------------

		FSM cfsm_flee = new FSM("Flee");
		GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
		cfsm_flee.addObserver(flee_observer);


		SimpleState safetyPath = new SimpleState("Safety path", new SafetyPathFleeAction());
		SimpleState edibleGhost = new SimpleState("More edible ghosts", new MoreEdibleGhostFleeAction());
		SimpleState morePillsFlee = new SimpleState("More pills", new MorePillsFleeAction());
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
		System.out.println("Creado");
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
	public
	MOVE getMove(Game game, long timeDue) {
		Input in = new MsPacManInput(game);
		return fsm.run(in);
	}

}