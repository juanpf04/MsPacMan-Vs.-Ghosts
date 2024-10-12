package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.RandomAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.RandomTransition;
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

		SimpleState last_pills = new SimpleState("state1", new RandomAction());

		Transition tran1 = new RandomTransition(.3);
		Transition tran2 = new RandomTransition(.2);
		Transition tran3 = new RandomTransition(.1);
		Transition tran4 = new RandomTransition(.4);

		// --------------------------------------------
		
		FSM cfsm_pills = new FSM("Pills");
		GraphFSMObserver pills_observer = new GraphFSMObserver(cfsm_pills.toString());
		cfsm_pills.addObserver(pills_observer);

		SimpleState cstate1 = new SimpleState("cstate1", new RandomAction());
		SimpleState cstate2 = new SimpleState("cstate2", new RandomAction());
		Transition ctran1 = new RandomTransition(.35);
		Transition ctran2 = new RandomTransition(.25);
		cfsm_pills.add(cstate1, ctran1, cstate2);
		cfsm_pills.add(cstate2, ctran2, cstate1);
		cfsm_pills.ready(cstate1);
		CompoundState pills = new CompoundState("pills", cfsm_pills);

		// --------------------------------------------

		FSM cfsm_flee = new FSM("Flee");
		GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
		cfsm_flee.addObserver(flee_observer);

		SimpleState cstate11 = new SimpleState("cstate1", new RandomAction());
		SimpleState cstate22 = new SimpleState("cstate2", new RandomAction());
		Transition ctran11 = new RandomTransition(.35);
		Transition ctran22 = new RandomTransition(.25);
		cfsm_flee.add(cstate11, ctran11, cstate22);
		cfsm_flee.add(cstate22, ctran22, cstate11);
		cfsm_flee.ready(cstate11);
		CompoundState flee = new CompoundState("flee", cfsm_flee);
		
		// --------------------------------------------

		FSM cfsm_chase = new FSM("Chase");
		GraphFSMObserver chase_observer = new GraphFSMObserver(cfsm_chase.toString());
		cfsm_chase.addObserver(chase_observer);

		SimpleState cstate14 = new SimpleState("cstate1", new RandomAction());
		SimpleState cstate24 = new SimpleState("cstate2", new RandomAction());
		Transition ctran14 = new RandomTransition(.35);
		Transition ctran24 = new RandomTransition(.25);
		cfsm_chase.add(cstate14, ctran14, cstate24);
		cfsm_chase.add(cstate24, ctran24, cstate14);
		cfsm_chase.ready(cstate14);
		CompoundState chase = new CompoundState("chase", cfsm_chase);
		
		// --------------------------------------------

		fsm.add(pills, tran1, flee);
		fsm.add(flee, tran2, chase);
		fsm.add(chase, tran3, last_pills);
		fsm.add(last_pills, tran4, pills);

		fsm.ready(pills);

		JFrame frame = new JFrame();
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
		main.add(pills_observer.getAsPanel(true, null), BorderLayout.SOUTH);
		main.add(flee_observer.getAsPanel(true, null), BorderLayout.SOUTH);
		main.add(chase_observer.getAsPanel(true, null), BorderLayout.SOUTH);
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