package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.GhostsInput;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class LairTimeTransition implements Transition {

	GHOST ghost;
	static int num;
	int id;

	public LairTimeTransition(GHOST ghost) {
		super();
		this.ghost = ghost;
		this.id = ++num;
	}

	@Override
	public boolean evaluate(Input in) {
		GhostsInput input = (GhostsInput) in;
		return input.isGhostInLair(ghost);
	}

	@Override
	public String toString() {
		return "Lair time " + this.id;
	}

}
