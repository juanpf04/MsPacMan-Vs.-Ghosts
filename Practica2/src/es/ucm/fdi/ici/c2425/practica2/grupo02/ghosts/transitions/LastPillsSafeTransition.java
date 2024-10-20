package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput.GhostsInfo;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class LastPillsSafeTransition implements Transition {

	private static final int THRESHOLD = 30;

	private GHOST ghost;

	public LastPillsSafeTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInfo info = ((GhostsInput) in).getInfo();
		return info.distancesFromGhostToPill.get(this.ghost) < THRESHOLD;
	}

	@Override
	public String toString() {
		return "Last pills safe";
	}

}
