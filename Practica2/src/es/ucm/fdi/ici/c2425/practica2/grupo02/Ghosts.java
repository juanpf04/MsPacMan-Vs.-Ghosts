package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.EnumMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions.ChaseAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions.RunAwayAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions.GhostsEdibleTransition;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions.GhostsNotEdibleAndMsPacManFarPPill;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions.MsPacManNearPPillTransition;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.RandomAction;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.RandomTransition;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	EnumMap<GHOST, FSM> fsms;

	public Ghosts() {
		setName("Fantasmikos");

		fsms = new EnumMap<GHOST, FSM>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			FSM fsm = new FSM(ghost.name());

			// fsm.addObserver(new ConsoleFSMObserver(ghost.name())); // Por consola
			GraphFSMObserver graphObserver = new GraphFSMObserver(ghost.name());
			fsm.addObserver(graphObserver);

			SimpleState lair = new SimpleState("lair", new RandomAction());

			Transition die = new RandomTransition(.1);
			Transition noLairTime = new RandomTransition(.1);
			Transition pacmanDies = new RandomTransition(.1);
			Transition edible = new RandomTransition(.3);
			Transition notEdible = new RandomTransition(.2);
			Transition pacmanNearToPPill = new RandomTransition(.1);
			Transition pacmanFarToPPill = new RandomTransition(.1);
			Transition nearToPacMan = new RandomTransition(.4);
			Transition someoneNearToPacMan = new RandomTransition(.4);

			// --------------------------------------------

			FSM cfsm_block = new FSM("Block");
			GraphFSMObserver block_observer = new GraphFSMObserver(cfsm_block.toString());
			cfsm_block.addObserver(block_observer);

			SimpleState blockExit = new SimpleState("block exit", new RandomAction());
			//TODO mirar nodeIndex de las powerPills para inicializar los estados
			SimpleState blockPPill1 = new SimpleState("block powerpill 1", new BlockPowerPillAction(12));
			SimpleState blockPPill2 = new SimpleState("block powerpill 2", new BlockPowerPillAction(12));
			SimpleState blockPPill3 = new SimpleState("block powerpill 3", new BlockPowerPillAction(12));
			SimpleState blockPPill4 = new SimpleState("block powerpill 4", new BlockPowerPillAction(12));
			SimpleState blockLastPills = new SimpleState("block last pills", new RandomAction());
			//TODO transicion existe powerpill, hay alguien mas cerca en exit y ppill cercana a pacman
			Transition PPill = new RandomTransition(.35); 
			Transition ctran2 = new RandomTransition(.25);
			cfsm_block.add(blockExit, ctran1, nearestPill);
			cfsm_block.add(nearestPill, ctran2, safePills);
			cfsm_block.ready(blockExit);
			CompoundState block = new CompoundState("Block", cfsm_block);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("Flee");
			GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
			cfsm_flee.addObserver(flee_observer);

			SimpleState fleePacMan = new SimpleState("cstate1", new RandomAction());
			SimpleState fleePPill = new SimpleState("cstate2", new RandomAction());
			SimpleState disperse = new SimpleState("cstate2", new RandomAction());
			SimpleState fleeToGhost = new SimpleState("cstate2", new RandomAction());
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
			//TODO mirar si renta pasar edible ghost a cover(block)
			SimpleState chasePacMan = new SimpleState("chase pacman", new RandomAction());
			SimpleState chaseEdibleGhost = new SimpleState("chase edible ghost", new RandomAction());
			Transition edibleGhostNearToPacMan = new RandomTransition(.35);
			Transition nearToEdibleGhost = new RandomTransition(.25);
			cfsm_chase.add(cstate14, ctran14, cstate24);
			cfsm_chase.add(cstate24, ctran24, cstate14);
			cfsm_chase.ready(cstate14);
			CompoundState chase = new CompoundState("chase", cfsm_chase);

			// --------------------------------------------

			fsm.add(block, danger, flee);
			fsm.add(chase, danger, flee);
			fsm.add(chase, noEdibleTime, block);
			fsm.add(chase, die, block);
			fsm.add(flee, die, block);
			fsm.add(flee, safety, block);
			fsm.add(flee, eatPowerPill, chase);

			fsm.ready(block);

			JFrame frame = new JFrame();
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
			main.add(block_observer.getAsPanel(true, null), BorderLayout.WEST);
			main.add(flee_observer.getAsPanel(true, null), BorderLayout.SOUTH);
			main.add(chase_observer.getAsPanel(true, null), BorderLayout.EAST);
			frame.getContentPane().add(main);
			frame.pack();
			frame.setVisible(true);

			fsms.put(ghost, fsm);
		}
	}

	public void preCompute(String opponent) {
		for (FSM fsm : fsms.values())
			fsm.reset();
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		GhostsInput in = new GhostsInput(game);

		for (GHOST ghost : GHOST.values()) {
			FSM fsm = fsms.get(ghost);
			MOVE move = fsm.run(in);
			result.put(ghost, move);
		}

		return result;

	}

}
