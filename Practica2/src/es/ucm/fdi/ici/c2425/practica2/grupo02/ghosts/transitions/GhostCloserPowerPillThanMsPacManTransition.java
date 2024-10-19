package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class GhostCloserPowerPillThanMsPacManTransition implements Transition {

	private GHOST ghost;

	public GhostCloserPowerPillThanMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		Transition transition = new GhostEdibleTransition(this.ghost);

		return !transition.evaluate(in)
				&& input.getDistanceToNearestPPill(this.ghost) < input.getMinPacmanDistancePPill();
	}

	@Override
	public String toString() {
		return "Closer to Power Pill Than MsPacMan";
	}

}
