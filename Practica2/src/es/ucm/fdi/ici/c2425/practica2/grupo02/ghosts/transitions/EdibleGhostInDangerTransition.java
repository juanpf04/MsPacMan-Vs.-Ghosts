package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class EdibleGhostInDangerTransition implements Transition {

	GHOST ghost;
	private int x; //TODO class


	public EdibleGhostInDangerTransition(GHOST ghost) {
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
		return "few pills";
	}

}
