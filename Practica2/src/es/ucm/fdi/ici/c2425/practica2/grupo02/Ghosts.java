package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.EnumMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInfo;
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
	GhostsInfo info;

	public Ghosts() {
		setName("Fantasmikos");

		info = new GhostsInfo();
		fsms = new EnumMap<GHOST, FSM>(GHOST.class);
		for (GHOST ghost : GHOST.values()) {
			FSM fsm = new FSM(ghost.name());

			GraphFSMObserver graphObserver = new GraphFSMObserver(ghost.name());
			fsm.addObserver(graphObserver);

			// --------------------------------------------

			FSM cfsm_chase = new FSM("CHASE");
			GraphFSMObserver chase_observer = new GraphFSMObserver(cfsm_chase.toString());
			cfsm_chase.addObserver(chase_observer);

			SimpleState chasePacMan = new SimpleState(new ChaseMsPacManAction(ghost));
			SimpleState coverExit = new SimpleState(new CoverExitAction(ghost, this.info));
			SimpleState coverEdibleGhost = new SimpleState("Cover edible ghost",
					new GoToGhostAction(ghost, false, this.info));
			SimpleState coverPPill = new SimpleState("Cover PowerPill", new GoToPowePillAction(ghost, this.info));
			SimpleState coverLastPills = new SimpleState(new CoverLastPillsAction(ghost));

			Transition coverExitToPPill = new PruebaTransition(ghost);
			Transition coverExitToPPillLast = new PruebaTransition(ghost);
			Transition coverExitToEdibleGhost = new PruebaTransition(ghost);

			cfsm_chase.add(coverExit, coverExitToPPill, coverPPill);
			cfsm_chase.add(coverExit, coverExitToEdibleGhost, coverEdibleGhost);
			cfsm_chase.add(coverExit, coverExitToPPillLast, coverLastPills);

			cfsm_chase.ready(coverExit);

			CompoundState chase = new CompoundState("Chase", cfsm_chase);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("FLEE");
			GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
			cfsm_flee.addObserver(flee_observer);

			SimpleState fleePacMan = new SimpleState(new RunAwayAction(ghost));
			SimpleState fleePPill = new SimpleState(new GoToPowePillAction(ghost, this.info));
			SimpleState fleeDisperse = new SimpleState(new DisperseAction(ghost, this.info));
			SimpleState fleeToGhost = new SimpleState("Flee to ghost", new GoToGhostAction(ghost, true, this.info));

			Transition fleePacmanToDisperse = new PruebaTransition(ghost);
			Transition fleePacmanToPPill = new PruebaTransition(ghost);
			Transition fleePacmanToGhost = new PruebaTransition(ghost);
			Transition fleeDisperseToPacman = new PruebaTransition(ghost);
			Transition fleeDisperseToPPill = new PruebaTransition(ghost);
			Transition fleePPillToPacman = new PruebaTransition(ghost);
			Transition fleePPillToDisperse = new PruebaTransition(ghost);

			cfsm_flee.add(fleePacMan, fleePacmanToDisperse, fleeDisperse);
			cfsm_flee.add(fleePacMan, fleePacmanToPPill, fleePPill);
			cfsm_flee.add(fleePacMan, fleePacmanToGhost, fleeToGhost);
			cfsm_flee.add(fleeDisperse, fleeDisperseToPacman, fleePacMan);
			cfsm_flee.add(fleeDisperse, fleeDisperseToPPill, fleePPill);
			cfsm_flee.add(fleePPill, fleePPillToPacman, fleePacMan);
			cfsm_flee.add(fleePPill, fleePPillToDisperse, fleeDisperse);

			cfsm_flee.ready(fleePacMan);

			CompoundState flee = new CompoundState("Flee", cfsm_flee);

			// --------------------------------------------

			SimpleState lair = new SimpleState(new LairAction());

			Transition lairTime = new LairTimeTransition(ghost);
			Transition lairTime2 = new LairTimeTransition(ghost);
			Transition noLairTime = new GhostRequiresActionTransition(ghost);
			Transition edible = new GhostEdibleTransition(ghost);
			Transition pacmanNearToPPill = new MsPacManNearPowerPillTransition();
			Transition notEdible = new GhostNotEdibleAndMsPacManFarPowerPillTransition(ghost);
			Transition nearToPacMan = new GhostNearMsPacManTransition(ghost);

			fsm.add(chase, edible, flee);
			fsm.add(chase, pacmanNearToPPill, flee);
			fsm.add(chase, nearToPacMan, chasePacMan);
			fsm.add(chase, lairTime, lair);

			fsm.add(flee, lairTime2, lair);
			fsm.add(flee, notEdible, chasePacMan);

			fsm.add(lair, noLairTime, chasePacMan);

			fsm.ready(chase);

			// --------------------------------------------

			JFrame frame = new JFrame();
			JPanel main = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			main.add(graphObserver.getAsPanel(true, null), gbc);

			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 0.5;
			main.add(chase_observer.getAsPanel(true, null), gbc);

			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 0.5;
			main.add(flee_observer.getAsPanel(true, null), gbc);

			frame.getContentPane().add(main);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();

			Dimension size = frame.getSize();
			size.width = (int) (size.width / 2.5);
			size.height = (int) (size.height / 1.25);
			frame.setSize(size);
			frame.setVisible(true);

			// --------------------------------------------

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
