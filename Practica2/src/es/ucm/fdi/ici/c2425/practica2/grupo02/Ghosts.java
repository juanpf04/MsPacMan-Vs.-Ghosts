package es.ucm.fdi.ici.c2425.practica2.grupo02;

import java.util.EnumMap;

import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions.*;
import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.SimpleState;
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

			// --------------------------------------------

			FSM cfsm_chase = new FSM("CHASE");

			SimpleState chasePacMan = new SimpleState("chase pacman", new ChaseMsPacManAction(ghost));
			SimpleState coverExit = new SimpleState(new CoverExitAction(ghost, this.info));
			SimpleState coverEdibleGhost = new SimpleState("Cover edible ghost",
					new GoToGhostAction(ghost, false, this.info));
			SimpleState coverPPill = new SimpleState("Cover PowerPill", new GoToPowePillAction(ghost, this.info));
			SimpleState coverLastPills = new SimpleState(new CoverLastPillsAction(ghost));

			cfsm_chase.add(chasePacMan, new NearestEdibleGhostToMsPacManInDangerTransition(ghost), coverEdibleGhost);
			cfsm_chase.add(chasePacMan, new GhostCloserPowerPillThanMsPacManTransition(ghost), coverPPill);
			cfsm_chase.add(chasePacMan, new GhostBehindMsPacManTransition(ghost), coverExit);
			cfsm_chase.add(chasePacMan, new FewPillsTransition(), coverLastPills);

			cfsm_chase.add(coverExit, new NearestEdibleGhostToMsPacManInDangerTransition(ghost), coverEdibleGhost);
			cfsm_chase.add(coverExit, new GhostTooCloseAndNotBehindMsPacManTransition(ghost), chasePacMan);

			cfsm_chase.add(coverEdibleGhost, new GhostTooCloseMsPacManTransition(ghost), chasePacMan);
			cfsm_chase.add(coverEdibleGhost, new EdibleGhostSafeTransition(ghost), chasePacMan);

			cfsm_chase.add(coverLastPills, new GhostTooCloseMsPacManTransition(ghost), chasePacMan);
			cfsm_chase.add(coverLastPills, new LastPillsSafeTransition(ghost), chasePacMan);

			cfsm_chase.add(coverPPill, new PowerPillSafeTransition(ghost), chasePacMan);

			cfsm_chase.ready(chasePacMan);

			CompoundState chase = new CompoundState("Chase", cfsm_chase);

			// --------------------------------------------

			FSM cfsm_flee = new FSM("FLEE");

			SimpleState runAway = new SimpleState(new RunAwayAction(ghost));
			SimpleState fleeToPPill = new SimpleState("Flee to PowerPill", new GoToPowePillAction(ghost, this.info));
			SimpleState fleeDisperse = new SimpleState(new DisperseAction(ghost));
			SimpleState fleeToGhost = new SimpleState("Flee to ghost", new GoToGhostAction(ghost, true, this.info));

			cfsm_flee.add(runAway, new GhostDensityHighTransition(ghost), fleeDisperse);
			cfsm_flee.add(runAway, new GhostCloserPowerPillThanMsPacManTransition(ghost), fleeToPPill);
			cfsm_flee.add(runAway, new EdibleGhostNearGhostThanMsPacManTransition(ghost), fleeToGhost);

			cfsm_flee.add(fleeDisperse, new GhostDensityNormalTransition(ghost), runAway);

			cfsm_flee.ready(runAway);

			CompoundState flee = new CompoundState("Flee", cfsm_flee);

			// --------------------------------------------

			SimpleState lair = new SimpleState(new LairAction());

			fsm.add(lair, new GhostRequiresActionTransition(ghost), chase);

			fsm.add(chase, new GhostEdibleTransition(ghost), flee);
			fsm.add(chase, new MsPacManNearPowerPillTransition(), flee);
			fsm.add(chase, new LairTimeTransition(ghost), lair);

			fsm.add(flee, new GhostNotEdibleAndMsPacManFarPowerPillTransition(ghost), chase);
			fsm.add(flee, new LairTimeTransition(ghost), lair);

			fsm.ready(lair);

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
