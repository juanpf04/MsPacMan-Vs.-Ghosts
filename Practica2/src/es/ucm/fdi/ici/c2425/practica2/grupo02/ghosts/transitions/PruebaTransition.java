package es.ucm.fdi.ici.c2425.practica2.grupo02.ghosts.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.Transition;
import pacman.game.Constants.GHOST;

public class PruebaTransition implements Transition {

	GHOST ghost;
	static int num;
	int id;

	public PruebaTransition(GHOST ghost) {
		super();
		this.id = ++num;
		this.ghost = ghost;
	}

	@Override
	public boolean evaluate(Input in) {
		Transition p = new GhostRequiresActionTransition(ghost);
		return p.evaluate(in);
	}

	@Override
	public String toString() {
		return this.id + "";
	}

}
