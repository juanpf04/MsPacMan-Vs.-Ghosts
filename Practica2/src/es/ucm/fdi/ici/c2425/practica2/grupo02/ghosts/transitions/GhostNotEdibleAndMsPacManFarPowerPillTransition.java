package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostNotEdibleAndMsPacManFarPowerPillTransition implements Transition {

	private GHOST ghost;

	public GhostNotEdibleAndMsPacManFarPowerPillTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		GhostEdibleTransition edible = new GhostEdibleTransition(this.ghost);
		MsPacManNearPowerPillTransition near = new MsPacManNearPowerPillTransition();
		return !edible.evaluate(input) && !near.evaluate(input);
	}

	@Override
	public String toString() {
		return "Ghost not edible and MsPacman far PPill";
	}

}
