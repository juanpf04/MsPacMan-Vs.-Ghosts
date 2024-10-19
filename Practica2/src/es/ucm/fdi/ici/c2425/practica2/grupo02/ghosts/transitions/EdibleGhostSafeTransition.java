package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class EdibleGhostSafeTransition implements Transition {

	private static final long THRESHOLD = 100;

	private GHOST ghost;

	public EdibleGhostSafeTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.getNumberOfEdibleGhosts() == 0 || input.getDistanceFromMsPacManToEdibleGhost() < THRESHOLD;
	}

	@Override
	public String toString() {
		return "Edible ghost safe";
	}

}
