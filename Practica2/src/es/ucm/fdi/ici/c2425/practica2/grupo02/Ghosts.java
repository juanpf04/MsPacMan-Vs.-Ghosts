package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;
import java.util.EnumMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostInfo;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions.*;
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
	GhostInfo info;

	public Ghosts() {
		setName("Fantasmikos");

		info = new GhostInfo();
		fsms = new EnumMap<GHOST, FSM>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			FSM fsm = new FSM(ghost.name());

			// fsm.addObserver(new ConsoleFSMObserver(ghost.name())); // Por consola
			GraphFSMObserver graphObserver = new GraphFSMObserver(ghost.name());
			fsm.addObserver(graphObserver);

			SimpleState lair = new SimpleState("lair", new LairAction(ghost));

			Transition die = new GhostDiesTransition(ghost);
			Transition noLairTime = new GhostRequiresActionTransition(ghost);
			Transition pacmanDies = new MsPacManDiesTransition(ghost);
			Transition edible = new GhostEdibleTransition(ghost);
			Transition notEdible = new GhostNotEdibleAndMsPacManFarPowerPillTransition(ghost);
			Transition pacmanNearToPPill = new MsPacManNearPowerPillTransition();
			Transition nearToPacMan = new GhostNearMsPacManTransition(ghost);
			Transition someoneNearToPacMan = new GhostFarMsPacManTransition(ghost);

			// --------------------------------------------

			FSM cfsm_cover = new FSM("Cover");
			GraphFSMObserver cover_observer = new GraphFSMObserver(cfsm_cover.toString());
			cfsm_cover.addObserver(cover_observer);

			SimpleState coverExit = new SimpleState("cover exit", new CoverExitAction(ghost));

			SimpleState coverPPill1 = new SimpleState("cover powerpill 1", new CoverPowerPillAction(ghost, 97));
			SimpleState coverPPill2 = new SimpleState("cover powerpill 2", new CoverPowerPillAction(ghost, 102));
			SimpleState coverPPill3 = new SimpleState("cover powerpill 3", new CoverPowerPillAction(ghost, 1143));
			SimpleState coverPPill4 = new SimpleState("cover powerpill 4", new CoverPowerPillAction(ghost, 1148));
			
			SimpleState coverLastPills = new SimpleState("cover last pills", new CoverLastPillsAction(ghost));

			// TODO transicion existe powerpill, hay alguien mas cerca en exit y ppill
			// cercana a pacman
			Transition coverExitToPPill1 = new RandomTransition(.35);
			Transition coverExitToPPill2 = new RandomTransition(.25);
			Transition coverExitToPPill3 = new RandomTransition(.35);
			Transition coverExitToPPill4 = new RandomTransition(.25);
			Transition coverExitToPPillLast = new RandomTransition(.25);

			cfsm_cover.add(coverExit, coverExitToPPill1, coverPPill1);
			cfsm_cover.add(coverExit, coverExitToPPill2, coverPPill2);
			cfsm_cover.add(coverExit, coverExitToPPill3, coverPPill3);
			cfsm_cover.add(coverExit, coverExitToPPill4, coverPPill4);
			cfsm_cover.add(coverExit, coverExitToPPillLast, coverLastPills);

			cfsm_cover.ready(coverExit);

			CompoundState block = new CompoundState("cover", cfsm_cover);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("Flee");
			GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
			cfsm_flee.addObserver(flee_observer);

			SimpleState fleePacMan = new SimpleState("flee pacman", new RunAwayAction(ghost));
			SimpleState fleePPill = new SimpleState("flee ppill", new GoToPowePillAction(ghost));
			SimpleState fleeDisperse = new SimpleState("flee disperse", new DisperseAction(ghost));
			SimpleState fleeToGhost = new SimpleState("flee to ghost", new GoToGhostAction(ghost));

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

			// TODO mirar si renta pasar edible ghost a cover(block)
			SimpleState chasePacMan = new SimpleState("chase pacman", new ChaseMsPacManAction(ghost));
			SimpleState chaseEdibleGhost = new SimpleState("chase edible ghost", new CoverEdibleGhostAction(ghost));

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

			fsm.add(lair, noLairTime, chase);

			fsm.ready(block);

			JFrame frame = new JFrame();
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			main.add(graphObserver.getAsPanel(true, null), BorderLayout.CENTER);
			main.add(cover_observer.getAsPanel(true, null), BorderLayout.WEST);
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

		GhostsInput in = new GhostsInput(game, info);

		for (GHOST ghost : GHOST.values()) {
			FSM fsm = fsms.get(ghost);
			MOVE move = fsm.run(in);
			result.put(ghost, move);
		}

		return result;

	}

}
