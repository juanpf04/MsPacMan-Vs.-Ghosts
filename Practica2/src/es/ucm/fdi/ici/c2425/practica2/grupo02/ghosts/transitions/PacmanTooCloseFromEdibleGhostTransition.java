package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class PacmanTooCloseFromEdibleGhostTransition implements Transition {
    private GHOST ghost;

	public PacmanTooCloseFromEdibleGhostTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
        GhostsInput input = (GhostsInput) in;
		return false;
	}

	@Override
	public String toString() {
		return "Pacman too close, was protecting edible ghost before";
	}
}
