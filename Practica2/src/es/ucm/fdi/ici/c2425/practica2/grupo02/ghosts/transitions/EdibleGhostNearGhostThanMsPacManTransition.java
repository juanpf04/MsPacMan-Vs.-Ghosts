package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class EdibleGhostNearGhostThanMsPacManTransition implements Transition {
 
	private GHOST ghost;

	public EdibleGhostNearGhostThanMsPacManTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		Transition transition = new GhostEdibleTransition(this.ghost);

		return transition.evaluate(in)
				&& input.getDistanceToNearestGhost(this.ghost) < input.getDistanceMsPacMan(this.ghost);

	}

	@Override
	public String toString() {
		return "Near to Ghost than MsPacMan";
	}

}
