package es.ucm.fdi.ici.c2425.practica2.grupo02;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;

import java.util.Random;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.SafePaths;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.chase_actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.flee_actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.actions.pills_actions.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_chase.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_compuestos.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_flee.*;
import es.ucm.fdi.ici.c2425.practica2.grupo02.mspacman.transitions.estados_pills.*;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
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
		new SafePaths();
		fsm = new FSM("MsPacMan");

		// Transiciones de la maquina de estados

		Transition dangerFromPills = new PacmanInDangerTransition("from Pills");
		Transition dangerFromChase = new PacmanInDangerTransition("from Chase");
		Transition dieFromChase = new PacManDiedTransition("from Chase");
		Transition dieFromFlee = new PacManDiedTransition("from Pills");
		Transition safety = new SafetyPacmanTransition();
		Transition noEdibleTime = new WithoutEdibleTimeForChaseTransition();
		Transition eatPowerPill = new PacmanEatPowerPillTransition();
		Transition edibleTimeYet = new EdibleTimeActiveTransition();

		// --------------------------------------------

		FSM cfsm_pills = new FSM("Pills");

		SimpleState safePills = new SimpleState("safe path", new SafeLongestPathPillsAction());
		SimpleState morePills = new SimpleState("more pills", new MorePillsPillsAction());
		SimpleState chooseAny = new SimpleState("choose any", new ChooseAnyPillsAction());

		Transition severalPaths = new SafetyPacmanTransition();
		Transition sameNumberOfPills = new IndifferentNumbersPills();
		Transition pathSelected = new PathSelected();

		cfsm_pills.add(safePills, severalPaths, morePills);
		cfsm_pills.add(morePills, sameNumberOfPills, chooseAny);
		cfsm_pills.add(chooseAny, pathSelected, safePills);

		cfsm_pills.ready(safePills);

		CompoundState pills = new CompoundState("pills", cfsm_pills);

		// --------------------------------------------

		FSM cfsm_flee = new FSM("Flee");

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

		SimpleState moreGhosts = new SimpleState("more ghosts", new MoreGhostsChaseAction());
		SimpleState safetyGhost = new SimpleState("safety ghost", new SafetyGhostChaseAction());

		Transition withoutTimeFromMoreGhostFromGhosts = new ShortEdibleTime("From more ghosts");

		cfsm_chase.add(moreGhosts, withoutTimeFromMoreGhostFromGhosts, safetyGhost);

		cfsm_chase.ready(moreGhosts);
		CompoundState chase = new CompoundState("chase", cfsm_chase);

		// --------------------------------------------

		fsm.add(pills, dangerFromPills, flee);
		fsm.add(pills, edibleTimeYet, chase);

		fsm.add(chase, dangerFromChase, flee);
		fsm.add(chase, noEdibleTime, pills);
		fsm.add(chase, dieFromChase, pills);

		fsm.add(flee, dieFromFlee, pills);
		fsm.add(flee, safety, pills);
		fsm.add(flee, eatPowerPill, chase);

		fsm.ready(pills);
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
		try {
			Input in = new MsPacManInput(game);
			return fsm.run(in);
		} catch (Exception e) {
			MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
			Random rnd = new Random();
			return moves[rnd.nextInt(moves.length)];
		}
	}

}