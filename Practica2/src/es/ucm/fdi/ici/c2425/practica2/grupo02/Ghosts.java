package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.awt.BorderLayout;
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
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Ghosts extends GhostController {

	private EnumMap<GHOST, FSM> fsms;
	private GhostsInfo info;

	public Ghosts() {
		setName("Fantasmikos");

		this.info = new GhostsInfo();
		this.fsms = new EnumMap<GHOST, FSM>(GHOST.class);
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

			cfsm_chase.add(chasePacMan, new NearestGhostEdibleToMsPacManInDangerTransition(ghost), coverEdibleGhost);
			cfsm_chase.add(chasePacMan, new GhostCloserPowerPillThanMsPacManTransition(ghost), coverPPill);
			cfsm_chase.add(chasePacMan, new GhostBehindMsPacManTransition(ghost), coverExit);
			cfsm_chase.add(chasePacMan, new FewPillsTransition(ghost), coverLastPills);

			cfsm_chase.add(coverExit, new NearestGhostEdibleToMsPacManInDangerTransition(ghost), coverEdibleGhost);
			cfsm_chase.add(coverExit, new GhostTooCloseAndNotBehindMsPacManTransition(ghost), chasePacMan);

			cfsm_chase.add(coverEdibleGhost, new GhostTooCloseMsPacManTransition(ghost), chasePacMan);
			cfsm_chase.add(coverEdibleGhost, new EdibleGhostSafeTransition(ghost), chasePacMan);

			cfsm_chase.add(coverLastPills, new GhostTooCloseMsPacManTransition(ghost), chasePacMan);
			cfsm_chase.add(coverLastPills, new LastPillsSafeTransition(ghost), chasePacMan);

			cfsm_chase.add(coverPPill, new PowerPillSafeTransition(ghost), chasePacMan);

			cfsm_chase.ready(coverExit);

			CompoundState chase = new CompoundState("Chase", cfsm_chase);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("FLEE");
			GraphFSMObserver flee_observer = new GraphFSMObserver(cfsm_flee.toString());
			cfsm_flee.addObserver(flee_observer);

			SimpleState runAway = new SimpleState(new RunAwayAction(ghost));
			SimpleState fleeToPPill = new SimpleState("Flee to PowerPill", new GoToPowePillAction(ghost, this.info));
			SimpleState fleeDisperse = new SimpleState(new DisperseAction(ghost));
			SimpleState fleeToGhost = new SimpleState("Flee to ghost", new GoToGhostAction(ghost, true, this.info));

			cfsm_flee.add(runAway, new GhostDensityHighTransition(ghost), fleeDisperse);
			cfsm_flee.add(runAway, new GhostCloserPowerPillThanMsPacManTransition(ghost), fleeToPPill);
			cfsm_flee.add(runAway, new EdibleGhostNearGhostThanMsPacManTransition(ghost), fleeToGhost);

			// cfsm_flee.add(fleeToPPill, new GhostDensityHighTransition(ghost),
			// fleeDisperse);

			cfsm_flee.add(fleeDisperse, new GhostDensityNormalTransition(ghost), runAway);

			cfsm_flee.ready(runAway);

			CompoundState flee = new CompoundState("Flee", cfsm_flee);

			// --------------------------------------------

			SimpleState lair = new SimpleState(new LairAction());

			fsm.add(chase, new GhostEdibleTransition(ghost), flee);
			fsm.add(chase, new MsPacManNearPowerPillTransition(), flee);
			fsm.add(chase, new LairTimeTransition(ghost), lair);

			fsm.add(flee, new GhostNotEdibleAndMsPacManFarPowerPillTransition(ghost), chase);
			fsm.add(flee, new LairTimeTransition(ghost), lair);

			fsm.add(lair, new GhostRequiresActionTransition(ghost), chase);

			fsm.ready(lair);

			// --------------------------------------------

//			JFrame frame = new JFrame();
//			JPanel main = new JPanel(new GridBagLayout());
//			GridBagConstraints gbc = new GridBagConstraints();
//
//			gbc.gridx = 0;
//			gbc.gridy = 0;
//			gbc.gridwidth = 2;
//			gbc.weightx = 1.0;
//			gbc.weighty = 1.0;
//			gbc.fill = GridBagConstraints.BOTH;
//			main.add(graphObserver.getAsPanel(true, null), gbc);
//
//			gbc.gridx = 0;
//			gbc.gridy = 1;
//			gbc.gridwidth = 1;
//			gbc.weightx = 0.5;
//			gbc.weighty = 0.5;
//			main.add(chase_observer.getAsPanel(true, null), gbc);
//
//			gbc.gridx = 1;
//			gbc.gridy = 1;
//			gbc.weightx = 0.5;
//			gbc.weighty = 0.5;
//			main.add(flee_observer.getAsPanel(true, null), gbc);
//
//			frame.getContentPane().add(main);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.pack();
//
//			Dimension size = frame.getSize();
//			size.width = (int) (size.width / 2.5);
//			size.height = (int) (size.height / 1.25);
//			frame.setSize(size);
//			frame.setVisible(true);

			JFrame frame = new JFrame();
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			main.add(graphObserver.getAsPanel(true, null), BorderLayout.CENTER);
			main.add(flee_observer.getAsPanel(true, null), BorderLayout.WEST);
			main.add(chase_observer.getAsPanel(true, null), BorderLayout.EAST);
			frame.getContentPane().add(main);
			frame.pack();
			frame.setVisible(true);

			// --------------------------------------------

			this.fsms.put(ghost, fsm);
		}
	}

	public void preCompute(String opponent) {
		for (FSM fsm : fsms.values())
			fsm.reset();
	}

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> result = new EnumMap<GHOST, MOVE>(GHOST.class);

		GhostsInput in = new GhostsInput(game, this.info);

		for (GHOST ghost : GHOST.values()) {
			FSM fsm = this.fsms.get(ghost);
			MOVE move = fsm.run(in);
			result.put(ghost, move);
		}

		return result;

	}

}
