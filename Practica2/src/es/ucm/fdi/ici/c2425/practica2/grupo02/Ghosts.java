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
			SimpleState blockPPill1 = new SimpleState("block powerpill 1", new RandomAction());
			SimpleState blockPPill2 = new SimpleState("block powerpill 2", new RandomAction());
			SimpleState blockPPill3 = new SimpleState("block powerpill 3", new RandomAction());
			SimpleState blockPPill4 = new SimpleState("block powerpill 4", new RandomAction());
			SimpleState blockLastPills = new SimpleState("block last pills", new RandomAction());

			//TODO transicion existe powerpill, hay alguien mas cerca en exit y ppill cercana a pacman
			Transition blockExitToPPill1 = new RandomTransition(.35); 
			Transition blockExitToPPill2 = new RandomTransition(.25);
			Transition blockExitToPPill3 = new RandomTransition(.35); 
			Transition blockExitToPPill4 = new RandomTransition(.25);
			Transition blockExitToPPillLast = new RandomTransition(.25);

			cfsm_block.add(blockExit, blockExitToPPill1, blockPPill1);
			cfsm_block.add(blockExit, blockExitToPPill2, blockPPill2);
			cfsm_block.add(blockExit, blockExitToPPill3, blockPPill3);
			cfsm_block.add(blockExit, blockExitToPPill4, blockPPill4);
			cfsm_block.add(blockExit, blockExitToPPillLast, blockLastPills);

			cfsm_block.ready(blockExit);

			CompoundState block = new CompoundState("Block", cfsm_block);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("Flee");
			GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
			cfsm_flee.addObserver(flee_observer);

			SimpleState fleePacMan = new SimpleState("flee pacman", new RandomAction());
			SimpleState fleePPill = new SimpleState("flee ppill", new RandomAction());
			SimpleState fleeDisperse = new SimpleState("flee disperse", new RandomAction());
			SimpleState fleeToGhost = new SimpleState("flee to ghost", new RandomAction());

			Transition fleePacmanToDisperse = new RandomTransition(.35); 
			Transition fleePacmanToPPill = new RandomTransition(.25);
			Transition fleePacmanToGhost = new RandomTransition(.35); 
			Transition fleeDisperseToPacman = new RandomTransition(.25);
			Transition fleeDisperseToPPill = new RandomTransition(.25);
			Transition fleePPillToPacman = new RandomTransition(.25);
			Transition fleePPillToDisperse = new RandomTransition(.25);

			cfsm_flee.add(fleePacMan, fleePacmanToDisperse, fleeDisperse);
			cfsm_flee.add(fleePacMan, fleePacmanToPPill, fleePPill);
			cfsm_flee.add(fleePacMan, fleePacmanToGhost, fleeToGhost);
			cfsm_flee.add(fleeDisperse, fleeDisperseToPacman, fleePacMan);
			cfsm_flee.add(fleeDisperse, fleeDisperseToPPill, fleePPill);
			cfsm_flee.add(fleePPill, fleePPillToPacman, fleePacMan);
			cfsm_flee.add(fleePPill, fleePPillToDisperse, fleeDisperse);

			cfsm_flee.ready(fleePacMan);

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

			cfsm_chase.add(chasePacMan, edibleGhostNearToPacMan, chaseEdibleGhost);
			cfsm_chase.add(chaseEdibleGhost, nearToEdibleGhost, chasePacMan);

			cfsm_chase.ready(chasePacMan);

			CompoundState chase = new CompoundState("chase", cfsm_chase);

			// --------------------------------------------

			fsm.add(block, edible, flee);
			fsm.add(block, pacmanNearToPPill, flee);
			fsm.add(block, nearToPacMan, chase);
			fsm.add(block, pacmanDies, lair);

			fsm.add(chase, edible, flee);
			fsm.add(chase, pacmanNearToPPill, flee);
			fsm.add(chase, pacmanDies, lair);
			fsm.add(chase, someoneNearToPacMan, block);

			fsm.add(flee, die, lair);
			fsm.add(flee, pacmanDies, lair);
			fsm.add(flee, notEdible, chase);

			fsm.ready(block);

			JFrame frame = new JFrame();
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			main.add(graphObserver.getAsPanel(true, null), BorderLayout.CENTER);
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
