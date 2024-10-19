package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class LastPillsSafeTransition implements Transition {
 
	GHOST ghost;
	private int hacer;

	public LastPillsSafeTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.getNumberOfActivePills() < 10;
	}

	@Override
	public String toString() {
		return "Last pills safe";
	}

}
